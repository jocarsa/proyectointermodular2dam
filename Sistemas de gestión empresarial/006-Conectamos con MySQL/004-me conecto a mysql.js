var mysql = require('mysql2')

var conexion = mysql.createConnection({
    host:"localhost",
    user:"clientes2526",
    password:"clientes2526",
    database:"clientes2526"
});

conexion.connect(function(err){
    if(err) throw err;
    console.log("conectado")
    conexion.query("SELECT * FROM clientes", function(err, result){
        if(err) throw err;
        console.log(result)
    });
});
