-- 001-usuarios.sql
-- Crea una tabla de usuarios muy simple para el login
-- (para clase: contraseña en claro, fácil de entender)

CREATE TABLE IF NOT EXISTS usuarios (
  user VARCHAR(64) PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  fullname VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Usuario de ejemplo
INSERT INTO usuarios(user,password,fullname)
VALUES ('admin','admin','Administrador/a')
ON DUPLICATE KEY UPDATE password=VALUES(password), fullname=VALUES(fullname);
