var servidor = require('http');

var mensajes = [];

servidor.createServer(function(req, res) {
  // --- Parse URL (GET ?m=...) ---
  var url = new URL(req.url, 'http://localhost:5000');
  var m = (url.searchParams.get('m') || '').trim();

  // --- Routes ---
  if (url.pathname === '/send') {
    if (m) mensajes.push(m);
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
