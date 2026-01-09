
const express = require("express");
const { MongoClient } = require("mongodb");

const app = express();

app.set("view engine", "pug");
app.set("views", "./views");

const url = "mongodb://127.0.0.1:27017";
const dbName = "empresadam2";

app.get("/", async (req, res) => {
  const client = new MongoClient(url);
  try {
    await client.connect();
    const db = client.db(dbName);
    const clientesCol = db.collection("clientes");

    // Listar (puedes ordenar por apellidos/nombre)
    const clientes = await clientesCol.find().sort({ apellidos: 1, nombre: 1 }).toArray();

    res.render("clientes", { clientes });
  } catch (err) {
    console.error("Error:", err);
    res.status(500).send("Error al leer clientes");
  } finally {
    await client.close();
  }
});

app.listen(5000, () => {
  console.log("Server running on http://localhost:5000");
});
