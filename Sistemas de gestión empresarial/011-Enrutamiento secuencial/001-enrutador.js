var servidor = require('http');                   // Primero incluyo la librería http
var archivos = require('fs');                     // PAra cargar archivos

// Ahora creo un servidor
servidor.createServer(function(req,res){
  switch(req.url){
    case "/":
        archivos.readFile('static/inicio.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/producto":
        archivos.readFile('static/producto.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/pedido":
        archivos.readFile('static/pedido.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/finalizar":
        archivos.readFile('static/finalizar.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    default:
      res.end("Página no encontrada");
  }
}).listen(5000) // Escucho en el puerto 5000


