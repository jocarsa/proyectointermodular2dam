-- crea usuario nuevo con contraseña
-- creamos el nombre de usuario que queramos
CREATE USER 
'aplicacionempresarial'@'localhost' 
IDENTIFIED  BY 'Aplicacionempresarial123$';

-- permite acceso a ese usuario
GRANT USAGE ON *.* TO 'aplicacionempresarial'@'localhost';

-- quitale todos los limites que tenga
ALTER USER 'aplicacionempresarial'@'localhost' 
REQUIRE NONE 
WITH MAX_QUERIES_PER_HOUR 0 
MAX_CONNECTIONS_PER_HOUR 0 
MAX_UPDATES_PER_HOUR 0 
MAX_USER_CONNECTIONS 0;

-- dale acceso a la base de datos empresadam
GRANT ALL PRIVILEGES ON aplicacionempresarial.* 
TO 'aplicacionempresarial'@'localhost';

-- recarga la tabla de privilegios
FLUSH PRIVILEGES;