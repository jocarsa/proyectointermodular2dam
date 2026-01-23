// mini-chat.js  (single-file)
// Run: node mini-chat.js  -> http://localhost:5000

var servidor = require('http');
var fs = require('fs');
var path = require('path');

var DATA_FILE = path.join(__dirname, 'mensajes.json');

// -------------------- Persist --------------------
function cargarMensajes() {
  try {
    var txt = fs.readFileSync(DATA_FILE, 'utf8');
    var arr = JSON.parse(txt);
    return Array.isArray(arr) ? arr : [];
  } catch (e) {
    return [];
  }
}

var mensajes = cargarMensajes();

// guarda cada 5 mensajes nuevos
var dirty = false;
var lastSavedLen = mensajes.length;

function guardarMensajes() {
  try {
    fs.writeFileSync(DATA_FILE, JSON.stringify(mensajes, null, 2), 'utf8');
    dirty = false;
    lastSavedLen = mensajes.length;
  } catch (e) {}
}

function maybePersistEach5() {
  if (dirty && (mensajes.length - lastSavedLen) >= 5) guardarMensajes();
}

process.on('SIGINT', function () { if (dirty) guardarMensajes(); process.exit(); });
process.on('SIGTERM', function () { if (dirty) guardarMensajes(); process.exit(); });
process.on('exit', function () { if (dirty) guardarMensajes(); });

// -------------------- Server --------------------
function esc(s){
  return String(s || '')
    .replace(/&/g,'&amp;')
    .replace(/</g,'&lt;')
    .replace(/>/g,'&gt;')
    .replace(/"/g,'&quot;')
    .replace(/'/g,'&#39;');
}

servidor.createServer(function (req, res) {
  var url = new URL(req.url, 'http://localhost:5000');

  // Routes
  if (url.pathname === '/send') {
    var user = (url.searchParams.get('u') || '').trim();
    var text = (url.searchParams.get('m') || '').trim();

    if (user && text) {
      mensajes.push({
        user: user,
        text: text,
        dt: new Date().toISOString() // datetime persistente
      });
      dirty = true;
      maybePersistEach5();
    }
    res.writeHead(302, { 'Location': '/' });
    return res.end();
  }

  if (url.pathname === '/msgs') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control':'no-store' });
    return res.end(JSON.stringify(mensajes));
  }

  // UI
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(`<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Mini Chat</title>
<style>
  :root{
    --bg:#f6f7fb;
    --card:#ffffff;
    --text:#111827;
    --muted:#6b7280;
    --border:#e5e7eb;
    --shadow: 0 10px 28px rgba(17,24,39,.08);
    --radius: 14px;
    --focus: rgba(59,130,246,.25);
    --accent:#2563eb;
    --accent2:#0ea5e9;
  }
  *{box-sizing:border-box}
  html,body{height:100%}
  body{
    margin:0;
    font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, Cantarell, "Noto Sans", Arial, sans-serif;
    background: radial-gradient(1200px 800px at 20% 10%, rgba(37,99,235,.10), transparent 55%),
                radial-gradient(900px 600px at 80% 30%, rgba(14,165,233,.10), transparent 55%),
                var(--bg);
    color:var(--text);
    line-height:1.35;
  }
  .wrap{
    max-width: 860px;
    margin: 28px auto;
    padding: 0 16px;
  }
  .top{
    display:flex;
    align-items:flex-end;
    justify-content:space-between;
    gap:12px;
    margin-bottom: 12px;
  }
  .brand{
    display:flex;
    gap:10px;
    align-items:center;
  }
  .dot{
    width:14px;height:14px;border-radius:50%;
    background: linear-gradient(135deg, var(--accent), var(--accent2));
    box-shadow: 0 8px 20px rgba(37,99,235,.25);
  }
  h1{
    margin:0;
    font-size: 18px;
    font-weight: 700;
    letter-spacing: .2px;
  }
  .hint{
    margin:0;
    color:var(--muted);
    font-size: 12px;
  }
  .card{
    background: var(--card);
    border: 1px solid var(--border);
    border-radius: var(--radius);
    box-shadow: var(--shadow);
    overflow:hidden;
  }
  form{
    display:flex;
    gap:10px;
    padding: 12px;
    border-bottom: 1px solid var(--border);
    background: linear-gradient(180deg, rgba(17,24,39,.02), transparent);
  }
  input{
    width:100%;
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 10px 12px;
    font-size: 14px;
    outline:none;
    background:#fff;
  }
  input:focus{
    border-color: rgba(37,99,235,.45);
    box-shadow: 0 0 0 4px var(--focus);
  }
  #u{flex: 0 0 170px;}
  #m{flex: 1 1 auto;}
  button{
    border:0;
    border-radius: 12px;
    padding: 10px 14px;
    font-size: 14px;
    font-weight: 650;
    color: white;
    background: linear-gradient(135deg, var(--accent), var(--accent2));
    box-shadow: 0 10px 22px rgba(37,99,235,.20);
    cursor:pointer;
    white-space:nowrap;
  }
  button:active{ transform: translateY(1px); }
  .log{
    height: 420px;
    overflow:auto;
    padding: 14px;
    background:
      linear-gradient(180deg, rgba(17,24,39,.02), transparent 70%),
      #fff;
  }
  .msg{
    display:flex;
    gap:10px;
    padding: 10px 10px;
    border: 1px solid var(--border);
    border-radius: 12px;
    margin-bottom: 10px;
    background: #fff;
  }
  .avatar{
    width:34px;height:34px;border-radius:10px;
    background: linear-gradient(135deg, rgba(37,99,235,.18), rgba(14,165,233,.18));
    border: 1px solid rgba(37,99,235,.15);
    display:flex;align-items:center;justify-content:center;
    font-weight: 800;
    color: rgba(17,24,39,.75);
    flex: 0 0 34px;
  }
  .meta{
    display:flex;
    align-items:baseline;
    gap:8px;
    margin-bottom: 2px;
  }
  .name{ font-weight: 750; font-size: 13px; }
  .time{ color: var(--muted); font-size: 12px; }
  .text{ font-size: 14px; white-space: pre-wrap; word-break: break-word; }
  .footer{
    padding: 10px 12px;
    border-top: 1px solid var(--border);
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:10px;
    color: var(--muted);
    font-size: 12px;
    background: #fff;
  }
  .pill{
    padding: 4px 8px;
    border-radius: 999px;
    border: 1px solid var(--border);
    background: rgba(17,24,39,.02);
  }
</style>
</head>
<body>
  <div class="wrap">
    <div class="top">
      <div class="brand">
        <div class="dot"></div>
        <div>
          <h1>Mini Chat</h1>
          <p class="hint">Name + message, persisted to <span class="pill">mensajes.json</span> every 5 new messages</p>
        </div>
      </div>
      <p class="hint" id="status">…</p>
    </div>

    <div class="card">
      <form id="f" action="/send" method="get">
        <input id="u" name="u" placeholder="your name" autocomplete="off">
        <input id="m" name="m" placeholder="type a message…" autocomplete="off">
        <button>Send</button>
      </form>

      <div class="log" id="log"></div>

      <div class="footer">
        <span>Auto-refresh: <span class="pill">500ms</span></span>
        <span>Total: <span class="pill" id="count">0</span></span>
      </div>
    </div>
  </div>

<script>
  const u = document.getElementById('u');
  const m = document.getElementById('m');
  const log = document.getElementById('log');
  const count = document.getElementById('count');
  const status = document.getElementById('status');

  // remember user name locally
  u.value = localStorage.getItem('miniChatUser') || '';
  u.addEventListener('input', () => localStorage.setItem('miniChatUser', u.value));

  // focus logic
  if(!u.value.trim()) u.focus(); else m.focus();

  function fmtTime(iso){
    try{
      const d = new Date(iso);
      return d.toLocaleString(undefined, {year:'numeric',month:'2-digit',day:'2-digit',hour:'2-digit',minute:'2-digit',second:'2-digit'});
    }catch(e){
      return iso || '';
    }
  }
  function avatarLetter(name){
    name = (name||'').trim();
    return name ? name[0].toUpperCase() : '?';
  }
  function esc(s){
    return String(s||'')
      .replace(/&/g,'&amp;')
      .replace(/</g,'&lt;')
      .replace(/>/g,'&gt;')
      .replace(/"/g,'&quot;')
      .replace(/'/g,'&#39;');
  }

  let lastLen = -1;

  async function tick(){
    try{
      const r = await fetch('/msgs', {cache:'no-store'});
      const a = await r.json();

      count.textContent = a.length;

      if(a.length !== lastLen){
        lastLen = a.length;

        log.innerHTML = a.map(item => {
          const name = esc(item.user || '');
          const text = esc(item.text || '');
          const time = esc(fmtTime(item.dt || ''));
          return \`
            <div class="msg">
              <div class="avatar">\${esc(avatarLetter(item.user))}</div>
              <div>
                <div class="meta">
                  <span class="name">\${name || 'Anonymous'}</span>
                  <span class="time">\${time}</span>
                </div>
                <div class="text">\${text}</div>
              </div>
            </div>\`;
        }).join('');

        log.scrollTop = log.scrollHeight;
      }

      status.textContent = 'online';
    }catch(e){
      status.textContent = 'offline';
    }
  }

  // enter-to-send (without shift)
  document.getElementById('f').addEventListener('submit', (e) => {
    if(!u.value.trim()){
      e.preventDefault();
      u.focus();
      u.style.boxShadow = '0 0 0 4px rgba(59,130,246,.25)';
      setTimeout(()=>u.style.boxShadow='', 350);
      return;
    }
    if(!m.value.trim()){
      e.preventDefault();
      m.focus();
      return;
    }
  });

  setInterval(tick, 500);
  tick();
</script>
</body>
</html>`);
}).listen(5000);
