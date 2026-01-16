// server.js — ERP educativo (Express + Pug + MySQL)
// - Login simple (tabla `usuarios`)
// - Dashboard: lista de tablas
// - CRUD dinámico: listar / insertar / editar / borrar

const express = require("express");
const session = require("express-session");
const mysql = require("mysql2");
const path = require("path");

const app = express();

/* =========================
   CONFIGURACIÓN
   ========================= */
const PORT = process.env.PORT || 5000;

// Ajusta estos datos a tu MySQL
const DB_HOST = process.env.DB_HOST || "localhost";
const DB_USER = process.env.DB_USER || "empresadam2";
const DB_PASS = process.env.DB_PASS || "empresadam2";
const DB_NAME = process.env.DB_NAME || "empresadam";

const pool = mysql
  .createPool({
    host: DB_HOST,
    user: DB_USER,
    password: DB_PASS,
    database: DB_NAME,
    connectionLimit: 10,
    namedPlaceholders: true
  })
  .promise();

/* =========================
   MIDDLEWARES
   ========================= */
app.set("view engine", "pug");
app.set("views", path.join(__dirname, "views"));

app.use(express.urlencoded({ extended: true }));
app.use(express.json({ limit: "1mb" }));

app.use(
  session({
    secret: process.env.SESSION_SECRET || "erp-simple-secret",
    resave: false,
    saveUninitialized: false,
    cookie: {
      httpOnly: true,
      sameSite: "lax"
    }
  })
);

app.use("/public", express.static(path.join(__dirname, "public")));

/* =========================
   UTILIDADES
   ========================= */
function h(str) {
  return String(str ?? "");
}

function requireAuth(req, res, next) {
  if (!req.session || !req.session.user) {
    return res.redirect("/login");
  }
  next();
}

async function dbQuery(sql, params) {
  const [rows] = await pool.query(sql, params);
  return rows;
}

function isSafeIdent(name) {
  // identificadores simples: letras, números y guion bajo
  return /^[a-zA-Z0-9_]+$/.test(name || "");
}

async function getTables() {
  // Tablas (solo base de datos actual)
  const rows = await dbQuery(
    `SELECT TABLE_NAME AS name
     FROM INFORMATION_SCHEMA.TABLES
     WHERE TABLE_SCHEMA = :db
       AND TABLE_TYPE = 'BASE TABLE'
     ORDER BY TABLE_NAME`,
    { db: DB_NAME }
  );

  // Filtrado básico: oculta tablas de sistema o auxiliares si quisieras
  return rows.map((r) => r.name);
}

async function getTableMeta(table) {
  if (!isSafeIdent(table)) throw new Error("Nombre de tabla no válido");

  const cols = await dbQuery(
    `SELECT COLUMN_NAME AS name,
            DATA_TYPE AS dataType,
            COLUMN_KEY AS colKey,
            IS_NULLABLE AS isNullable,
            COLUMN_DEFAULT AS colDefault,
            EXTRA AS extra
     FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = :db
       AND TABLE_NAME = :t
     ORDER BY ORDINAL_POSITION`,
    { db: DB_NAME, t: table }
  );

  const pk = cols.find((c) => c.colKey === "PRI");

  return {
    table,
    columns: cols,
    pk: pk ? pk.name : null
  };
}

function normalizeRowForJson(row) {
  // MySQL devuelve fechas/decimales a veces en formatos especiales
  // Lo dejamos tal cual, pero convertimos Buffers
  const out = {};
  for (const k of Object.keys(row)) {
    const v = row[k];
    if (Buffer.isBuffer(v)) out[k] = v.toString("utf8");
    else out[k] = v;
  }
  return out;
}

/* =========================
   RUTAS (VISTAS)
   ========================= */
app.get("/", (req, res) => {
  if (req.session && req.session.user) return res.redirect("/dashboard");
  return res.redirect("/login");
});

app.get("/login", (req, res) => {
  res.render("login", {
    titulo: "Acceso al ERP",
    error: null
  });
});

app.post("/login", async (req, res) => {
  try {
    const usuario = String(req.body.usuario || "").trim();
    const contrasena = String(req.body.contrasena || "").trim();

    if (!usuario || !contrasena) {
      return res.status(400).render("login", {
        titulo: "Acceso al ERP",
        error: "Debes escribir usuario y contraseña"
      });
    }

    // Tabla `usuarios`: user, password, fullname (igual que el ejemplo del temario)
    const rows = await dbQuery(
      "SELECT user, password, fullname FROM usuarios WHERE user = :u LIMIT 1",
      { u: usuario }
    );

    if (!rows || rows.length === 0) {
      return res.status(401).render("login", {
        titulo: "Acceso al ERP",
        error: "Usuario o contraseña incorrectos"
      });
    }

    if (String(rows[0].password) !== contrasena) {
      return res.status(401).render("login", {
        titulo: "Acceso al ERP",
        error: "Usuario o contraseña incorrectos"
      });
    }

    req.session.user = {
      user: rows[0].user,
      fullname: rows[0].fullname
    };

    return res.redirect("/dashboard");
  } catch (err) {
    console.error(err);
    return res.status(500).render("login", {
      titulo: "Acceso al ERP",
      error: "Error interno al iniciar sesión"
    });
  }
});

app.get("/logout", (req, res) => {
  req.session.destroy(() => {
    res.redirect("/login");
  });
});

app.get("/dashboard", requireAuth, async (req, res) => {
  res.render("dashboard", {
    titulo: "Dashboard del ERP",
    usuario: req.session.user
  });
});

/* =========================
   API (JSON)
   ========================= */
app.get("/api/tables", requireAuth, async (req, res) => {
  try {
    const tables = await getTables();
    res.json({ ok: true, tables });
  } catch (err) {
    console.error(err);
    res.status(500).json({ ok: false, error: "No se pudieron leer las tablas" });
  }
});

app.get("/api/table/:table/meta", requireAuth, async (req, res) => {
  try {
    const meta = await getTableMeta(req.params.table);
    res.json({ ok: true, meta });
  } catch (err) {
    console.error(err);
    res.status(400).json({ ok: false, error: err.message || "Meta no disponible" });
  }
});

app.get("/api/table/:table/rows", requireAuth, async (req, res) => {
  try {
    const table = req.params.table;
    const limit = Math.max(1, Math.min(500, parseInt(req.query.limit || "200", 10)));

    const meta = await getTableMeta(table);

    // Si hay PK, ordenamos por PK. Si no, ordenamos por primera columna
    const orderCol = meta.pk || (meta.columns[0] ? meta.columns[0].name : null);
    if (!orderCol) return res.json({ ok: true, rows: [] });

    // Identificadores protegidos con ??
    const [rows] = await pool.query(
      "SELECT * FROM ?? ORDER BY ?? DESC LIMIT ?",
      [table, orderCol, limit]
    );

    res.json({ ok: true, rows: rows.map(normalizeRowForJson) });
  } catch (err) {
    console.error(err);
    res.status(400).json({ ok: false, error: err.message || "No se pudieron leer filas" });
  }
});

app.post("/api/table/:table/insert", requireAuth, async (req, res) => {
  try {
    const table = req.params.table;
    const meta = await getTableMeta(table);

    // Permitimos insertar solo columnas reales
    const allowed = new Set(meta.columns.map((c) => c.name));
    const data = req.body && typeof req.body === "object" ? req.body : {};

    const cols = [];
    const vals = [];

    meta.columns.forEach((c) => {
      // Si es auto_increment, no lo pedimos
      const isAuto = String(c.extra || "").toLowerCase().includes("auto_increment");
      if (isAuto) return;

      if (Object.prototype.hasOwnProperty.call(data, c.name)) {
        cols.push(c.name);
        vals.push(data[c.name]);
      }
    });

    if (cols.length === 0) {
      return res.status(400).json({ ok: false, error: "No se recibieron campos para insertar" });
    }

    // Construimos: INSERT INTO tabla (col1,col2) VALUES (?,?)
    const placeholders = cols.map(() => "?").join(",");
    const sql = `INSERT INTO ?? (${cols.map(() => "??").join(",")}) VALUES (${placeholders})`;

    const params = [table, ...cols, ...vals];
    const [result] = await pool.query(sql, params);

    res.json({ ok: true, insertedId: result.insertId || null });
  } catch (err) {
    console.error(err);
    res.status(400).json({ ok: false, error: err.message || "No se pudo insertar" });
  }
});

app.post("/api/table/:table/update", requireAuth, async (req, res) => {
  try {
    const table = req.params.table;
    const meta = await getTableMeta(table);

    if (!meta.pk) {
      return res.status(400).json({ ok: false, error: "La tabla no tiene clave primaria (PK)" });
    }

    const pkValue = req.body ? req.body.pkValue : undefined;
    const data = req.body && typeof req.body.data === "object" ? req.body.data : {};

    if (pkValue === undefined) {
      return res.status(400).json({ ok: false, error: "Falta pkValue" });
    }

    const allowed = new Set(meta.columns.map((c) => c.name));

    const sets = [];
    const values = [];

    for (const k of Object.keys(data)) {
      if (!allowed.has(k)) continue;
      if (k === meta.pk) continue; // no actualizamos la PK
      sets.push(k);
      values.push(data[k]);
    }

    if (sets.length === 0) {
      return res.status(400).json({ ok: false, error: "No hay campos para actualizar" });
    }

    // UPDATE tabla SET col1=?, col2=? WHERE pk=?
    const sql = `UPDATE ?? SET ${sets.map(() => "?? = ?").join(", ")} WHERE ?? = ? LIMIT 1`;
    const params = [table];
    for (let i = 0; i < sets.length; i++) {
      params.push(sets[i], values[i]);
    }
    params.push(meta.pk, pkValue);

    const [result] = await pool.query(sql, params);

    res.json({ ok: true, affectedRows: result.affectedRows || 0 });
  } catch (err) {
    console.error(err);
    res.status(400).json({ ok: false, error: err.message || "No se pudo actualizar" });
  }
});

app.post("/api/table/:table/delete", requireAuth, async (req, res) => {
  try {
    const table = req.params.table;
    const meta = await getTableMeta(table);

    if (!meta.pk) {
      return res.status(400).json({ ok: false, error: "La tabla no tiene clave primaria (PK)" });
    }

    const pkValue = req.body ? req.body.pkValue : undefined;
    if (pkValue === undefined) {
      return res.status(400).json({ ok: false, error: "Falta pkValue" });
    }

    const [result] = await pool.query("DELETE FROM ?? WHERE ?? = ? LIMIT 1", [table, meta.pk, pkValue]);
    res.json({ ok: true, affectedRows: result.affectedRows || 0 });
  } catch (err) {
    console.error(err);
    res.status(400).json({ ok: false, error: err.message || "No se pudo borrar" });
  }
});

/* =========================
   ARRANQUE
   ========================= */
app.listen(PORT, () => {
  console.log("ERP educativo listo en http://localhost:" + PORT);
  console.log("BD:", DB_NAME, "Usuario:", DB_USER, "Host:", DB_HOST);
});
