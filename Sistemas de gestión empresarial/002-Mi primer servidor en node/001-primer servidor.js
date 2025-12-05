// Primero incluyo la librería http
var servidor = require('http');

// Ahora creo un servidor
servidor.createServer(function(req,res){
  // Envío las cabeceras 
  // 200 es ok
  // Voy a devolver texto en formato HTML
  res.writeHead(200,{'Content-Type':'text/html'})
  // Escribo un mensaje en pantalla
  res.end("Hola mundo desde Node.js")
}).listen(3000) // Escucho en el puerto 3000


