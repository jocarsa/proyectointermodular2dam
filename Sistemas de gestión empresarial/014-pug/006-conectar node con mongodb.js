// npm install mongodb

const { MongoClient } = require("mongodb");

const url = "mongodb://127.0.0.1:27017";
const dbName = "empresadam2";

async function main() {
  const client = new MongoClient(url);

  try {
    await client.connect();
    console.log("Conectado a MongoDB");

    const db = client.db(dbName);
    const clientes = db.collection("clientes");

    // LISTAR clientes (sin insertar)
    const lista = await clientes.find().toArray();
    console.log("Clientes:", lista);

  } catch (err) {
    console.error("Error:", err);
  } finally {
    await client.close();
  }
}

main();
