var servidor = require('http');
var fs = require('fs');
var path = require('path');

var DATA_FILE = path.join(__dirname, 'mensajes.json');

function cargarMensajes() {
  try {
    var txt = fs.readFileSync(DATA_FILE, 'utf8');
    var arr = JSON.parse(txt);
    return Array.isArray(arr) ? arr.map(String) : [];
  } catch (e) {
    return [];
  }
}

var mensajes = cargarMensajes();

// Persist control
var dirty = false;
var lastSavedLen = mensajes.length;

function guardarMensajes() {
  try {
    fs.writeFileSync(DATA_FILE, JSON.stringify(mensajes, null, 2), 'utf8');
    dirty = false;
    lastSavedLen = mensajes.length;
  } catch (e) {
    // silencioso (ultra-minimal); si quieres, puedes loguearlo
    // console.error('No pude guardar:', e);
  }
}

function maybePersistEach5() {
  // guarda cuando se hayan añadido 5 mensajes desde el último guardado
  if (dirty && (mensajes.length - lastSavedLen) >= 5) guardarMensajes();
}

// (Opcional) también intenta guardar al cerrar el proceso
process.on('SIGINT', function () { if (dirty) guardarMensajes(); process.exit(); });
process.on('SIGTERM', function () { if (dirty) guardarMensajes(); process.exit(); });
process.on('exit', function () { if (dirty) guardarMensajes(); });

servidor.createServer(function (req, res) {
  // --- Parse URL (GET ?m=...) ---
  var url = new URL(req.url, 'http://localhost:5000');
  var m = (url.searchParams.get('m') || '').trim();

  // --- Routes ---
  if (url.pathname === '/send') {
    if (m) {
      mensajes.push(m);
      dirty = true;
      maybePersistEach5();
    }
    res.writeHead(302, { 'Location': '/' });
    return res.end();
  }

  if (url.pathname === '/msgs') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    return res.end(JSON.stringify(mensajes));
  }

  // --- Mega-minimal embedded HTML (chat UI) ---
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(`<!doctype html>
<meta charset="utf-8">
<title>Mini Chat</title>
<form id=f action="/send" method="get" style="margin:0 0 8px 0">
  <input name="m" id=m autocomplete="off" style="width:70%" placeholder="mensaje...">
  <button>Enviar</button>
</form>
<pre id=o style="border:1px solid #ccc;padding:8px;height:220px;overflow:auto;margin:0"></pre>
<script>
async function tick(){
  const r = await fetch('/msgs');
  const a = await r.json();
  o.textContent = a.map((x,i)=> (i+1)+'. '+x).join('\\n');
  o.scrollTop = o.scrollHeight;
}
setInterval(tick, 500);
tick();
</script>`);
}).listen(5000);
