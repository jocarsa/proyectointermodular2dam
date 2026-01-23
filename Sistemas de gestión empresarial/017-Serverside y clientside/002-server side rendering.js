// server.js
// Simple Node.js HTTP server that reads productos.json and serves dynamic HTML.

const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 5000;

const PRODUCTS_PATH = path.join(__dirname, "productos.json");

const CSS = `
:root{
  --bg:#f6f7fb;
  --card:#ffffff;
  --text:#111827;
  --muted:#6b7280;
  --border:#e5e7eb;
  --shadow: 0 8px 24px rgba(17,24,39,.08);
  --radius: 14px;
  --max: 1100px;
}
*{ box-sizing:border-box; }
html,body{ height:100%; }
body{
  margin:0;
  font-family: system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, "Noto Sans", Arial, sans-serif;
  color:var(--text);
  background:var(--bg);
  line-height:1.45;
}
header, main, footer{
  max-width: var(--max);
  margin: 0 auto;
  padding: 20px;
}
header{
  padding-top: 28px;
  padding-bottom: 12px;
}
header h1{
  margin:0;
  font-size: clamp(1.4rem, 2.6vw, 2rem);
  letter-spacing:-0.02em;
}
main{
  display:grid;
  grid-template-columns: repeat( auto-fit, minmax(220px, 1fr) );
  gap: 16px;
  padding-top: 8px;
  padding-bottom: 28px;
}
article{
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 16px;
  box-shadow: var(--shadow);
  transition: transform .12s ease, box-shadow .12s ease, border-color .12s ease;
}
article:hover{
  transform: translateY(-2px);
  border-color: #d1d5db;
  box-shadow: 0 14px 32px rgba(17,24,39,.12);
}
article h3{
  margin: 0 0 8px 0;
  font-size: 1.05rem;
}
article p{
  margin: 0;
  font-weight: 700;
  color: #0f172a;
}
article p::before{
  content: "€ ";
  font-weight: 600;
  color: var(--muted);
}
footer{
  color: var(--muted);
  font-size: .95rem;
  padding-top: 10px;
  padding-bottom: 28px;
  border-top: 1px solid var(--border);
}
@media (max-width: 520px){
  header, main, footer{ padding-left: 14px; padding-right: 14px; }
  article{ padding: 14px; }
}
`;

function escapeHtml(s) {
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function readProducts() {
  // Read JSON fresh on each request (simple + no cache issues)
  const raw = fs.readFileSync(PRODUCTS_PATH, "utf8");
  const data = JSON.parse(raw);
  if (!Array.isArray(data)) return [];
  return data;
}

function renderHTML(products) {
  const cards = products
    .map((p) => {
      const nombre = escapeHtml(p?.nombre ?? "Producto");
      const precio = escapeHtml(p?.precio ?? "");
      return `
        <article>
          <h3>${nombre}</h3>
          <p>${precio}</p>
        </article>
      `;
    })
    .join("");

  return `<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Tienda de ropa</title>
    <style>${CSS}</style>
  </head>
  <body>
    <header>
      <h1>Tienda de ropa</h1>
    </header>

    <main>
      ${cards}
    </main>

    <footer>
      (c)2026 Jose Vicente Carratala
    </footer>
  </body>
</html>`;
}

const servidor = http.createServer((req, res) => {
  try {
    // Basic routing
    if (req.url === "/" || req.url === "/index.html") {
      const productos = readProducts();
      const html = renderHTML(productos);
      res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
      return res.end(html);
    }

    // Optional: serve the JSON if you want to inspect it in browser
    if (req.url === "/productos.json") {
      const raw = fs.readFileSync(PRODUCTS_PATH, "utf8");
      res.writeHead(200, { "Content-Type": "application/json; charset=utf-8" });
      return res.end(raw);
    }

    res.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("404 - Not Found");
  } catch (err) {
    res.writeHead(500, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("500 - Server error\n\n" + (err && err.stack ? err.stack : String(err)));
  }
});

servidor.listen(PORT, () => {
  console.log(`Servidor listo: http://localhost:${PORT}`);
});
