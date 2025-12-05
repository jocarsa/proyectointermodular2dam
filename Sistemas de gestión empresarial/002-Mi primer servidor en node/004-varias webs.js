var servidor = require('http');                   // Primero incluyo la librería http
var archivos = require('fs');                     // PAra cargar archivos

// Ahora creo un servidor
servidor.createServer(function(req,res){
  switch(req.url){
    res.writeHead(200,{'Content-Type':'text/html'})
    case "/":
        archivos.readFile('inicio.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/sobremi":
        archivos.readFile('sobremi.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/contacto":
        archivos.readFile('contacto.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    default:
      res.end("Página no encontrada");
}).listen(5000) // Escucho en el puerto 5000


