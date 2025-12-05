var servidor = require('http');                   // Primero incluyo la librería http
var archivos = require('fs');                     // PAra cargar archivos

servidor.createServer(function(req,res){
  res.writeHead(200,{'Content-Type':'text/html'})
  switch(req.url){
    case "/":
      archivos.readFile('inicio.html',function(err,data){
          res.write(data)
          res.end("")
      });
      break;
    case "/api":
      var datos = {"nombre":"Jose Vicente","apellidos":"Carratalá Sanchis"};
      res.end(JSON.stringify(datos));
      break;
  }
}).listen(5000) // Escucho en el puerto 5000


