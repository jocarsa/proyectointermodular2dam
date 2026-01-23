var servidor = require('http');

var mensajes = [];

servidor.createServer(function(req,res){
  res.writeHead(200,{'Content-Type':'text/html'})
  mensajes.push("Hola")
  res.end("La pila de mensajes es: "+mensajes);
}).listen(5000) 


