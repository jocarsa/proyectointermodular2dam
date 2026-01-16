const express = require("express");
const { MongoClient } = require("mongodb");
const path = require("path");

const app = express();
const PORT = 5000;

const MONGO_URL = "mongodb://127.0.0.1:27017";
const DB_NAME = "informes";
const COLLECTION = "facturas";

// EJS
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));

// Static (CSS)
app.use(express.static(path.join(__dirname, "public")));

function euros(n) {
  return n.toLocaleString("es-ES", {
    style: "currency",
    currency: "EUR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
}

// Normaliza n (posición) y evita valores raros
function parseIndexParam(nParam) {
  const n = Number.parseInt(nParam, 10);
  if (!Number.isFinite(n) || n < 1) return null;
  return n;
}

app.get("/factura/:n", async (req, res) => {
  const n = parseIndexParam(req.params.n);
  if (!n) return res.status(400).send("Parámetro inválido. Usa /factura/1, /factura/2, ...");

  const client = new MongoClient(MONGO_URL);

  try {
    await client.connect();
    const db = client.db(DB_NAME);
    const col = db.collection(COLLECTION);

    // n=1 -> skip 0, n=2 -> skip 1, etc.
    const factura = await col.find().skip(n - 1).limit(1).next();

    if (!factura) {
      return res.status(404).send(`No existe la factura número ${n}`);
    }

    const base = factura.lineas.reduce((s, l) => s + (l.precio * l.unidades), 0);
    const iva = base * (factura.datos_de_la_factura.iva / 100);
    const irpf = base * (factura.datos_de_la_factura.irpf / 100);
    const total = base + iva - irpf;

    res.render("factura", {
      f: factura,
      base,
      iva,
      irpf,
      total,
      euros
    });

  } catch (e) {
    res.status(500).send(e.toString());
  } finally {
    await client.close();
  }
});

// Ruta opcional: redirige /factura -> /factura/1
app.get("/factura", (req, res) => res.redirect("/factura/1"));

app.listen(PORT, () => {
  console.log(`Servidor en http://localhost:${PORT}`);
  console.log(`Ejemplos: http://localhost:${PORT}/factura/1  |  /factura/2`);
});
