var servidor = require('http');                   // Primero incluyo la librería http
var archivos = require('fs');                     // PAra cargar archivos
var mysql = require('mysql2')											// npm install mysql2

var conexion = mysql.createConnection({
    host:"localhost",
    user:"clientes2526",
    password:"clientes2526",
    database:"clientes2526"
});

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
      let datos;
      conexion.connect(function(err){
          if(err) throw err;
          console.log("conectado")
          conexion.query("SELECT * FROM clientes", function(err, result){
              if(err) throw err;
              res.end(JSON.stringify(result));
          });
      });
      
      break;
  }
}).listen(5000) // Escucho en el puerto 5000
