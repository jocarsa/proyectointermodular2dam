var servidor = require('http');

var contador = 1;

servidor.createServer(function(req,res){
  res.writeHead(200,{'Content-Type':'text/html'})
  contador++;
  res.end("El contador es: "+contador)
}).listen(5000) 


