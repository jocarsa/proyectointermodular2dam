const express = require("express");
const { MongoClient } = require("mongodb");
const path = require("path");

const app = express();
const PORT = 5000;

const MONGO_URL = "mongodb://127.0.0.1:27017";
const DB_NAME = "informes";
const COLLECTION = "facturas";

// Configurar EJS
app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));

// CSS estático
app.use(express.static(path.join(__dirname, "public")));

function euros(n) {
  return n.toLocaleString("es-ES", {
    style: "currency",
    currency: "EUR"
  });
}

app.get("/factura", async (req, res) => {
  const client = new MongoClient(MONGO_URL);

  try {
    await client.connect();
    const db = client.db(DB_NAME);
    const factura = await db.collection(COLLECTION).findOne();

    if (!factura) {
      return res.status(404).send("No hay facturas");
    }

    const base = factura.lineas.reduce(
      (s, l) => s + l.precio * l.unidades,
      0
    );

    const iva = base * factura.datos_de_la_factura.iva / 100;
    const irpf = base * factura.datos_de_la_factura.irpf / 100;
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

app.listen(PORT, () => {
  console.log(`Factura en http://localhost:${PORT}/factura`);
});
