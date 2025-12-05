var servidor = require('http');                   // Primero incluyo la librería http
var archivos = require('fs');                     // PAra cargar archivos

// Ahora creo un servidor
servidor.createServer(function(req,res){
  archivos.readFile('inicio.html',function(err,data){
    res.writeHead(200,{'Content-Type':'text/html'});
    res.write(data)
    res.end("")
  });
}).listen(5000) // Escucho en el puerto 5000


