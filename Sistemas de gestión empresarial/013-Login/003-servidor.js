var servidor = require('http');     // http
var archivos = require('fs');       // fs
var mysql = require('mysql2');      // mysql driver

// Conexión MySQL (root)
var conexion = mysql.createConnection({
  host: 'localhost',
  user: 'aplicacionempresarial',
  password: 'Aplicacionempresarial123$',   // <- pon tu password de root
  database: 'aplicacionempresarial'
});

// Ahora creo un servidor
servidor.createServer(function(req,res){

  // ======== POST /comprobar ========
  if(req.method === "POST" && req.url === "/comprobar"){		/////////// IMPORTANTE: SI EL ENDPOINT ES COMPROBAR

    var body = "";
    req.on("data", function(chunk){
      body += chunk.toString();
    });

    req.on("end", function(){

      // body viene tipo: usuario=xxx&contrasena=yyy
      var datos = {};
      body.split("&").forEach(function(par){
        var trozos = par.split("=");
        var clave = decodeURIComponent((trozos[0] || "").replace(/\+/g," "));
        var valor = decodeURIComponent((trozos[1] || "").replace(/\+/g," "));
        datos[clave] = valor;
      });

      var usuario = (datos.usuario || "").trim();
      var contrasena = (datos.contrasena || "").trim();

      // consulta MySQL (tabla usuarios: user, password, fullname)
      conexion.query(
        "SELECT user,password,fullname FROM usuarios WHERE user = ? LIMIT 1",
        [usuario],
        function(err, resultados){

          if(err){
            res.writeHead(500, {'Content-Type':'text/plain; charset=utf-8'});
            res.end("Error de base de datos");
            return;
          }

          if(resultados.length === 0){
            res.writeHead(401, {'Content-Type':'text/plain; charset=utf-8'});
            res.end("Usuario o contraseña incorrectos");
            return;
          }

          // password en claro (como en tu INSERT)
          if(resultados[0].password !== contrasena){
            res.writeHead(401, {'Content-Type':'text/plain; charset=utf-8'});
            res.end("Usuario o contraseña incorrectos");
            return;
          }

          // OK -> ir al escritorio
          res.writeHead(302, { Location: "/escritorio" });
          res.end();
        }
      );
    });

    return; // importante: no seguir con el switch de GET
  }

  // ======== GET (tu switch tal cual) ========
  switch(req.url){
    case "/":
        archivos.readFile('static/inicio.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/comprobar":
        archivos.readFile('static/comprobar.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    case "/escritorio":
        archivos.readFile('static/escritorio.html',function(err,data){
            res.write(data)
            res.end("")
        });
        break;
    default:
      res.end("Página no encontrada");
  }

}).listen(5000) // Escucho en el puerto 5000
