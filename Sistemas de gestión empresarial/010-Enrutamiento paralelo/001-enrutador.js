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
    case "/clientes":
        archivos.readFile('static/clientes.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/productos":
        archivos.readFile('static/productos.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/pedidos":
        archivos.readFile('static/pedidos.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    default:
      res.end("Página no encontrada");
  }
}).listen(5000) // Escucho en el puerto 5000


