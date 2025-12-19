sudo mysql -u root -p

-- Crear la base de datos
CREATE DATABASE aplicacionempresarial
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE aplicacionempresarial;

-- Crear la tabla usuarios
CREATE TABLE usuarios (
  user VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  fullname VARCHAR(255) NOT NULL,
  PRIMARY KEY (user)
);

-- Insertar usuario inicial
INSERT INTO usuarios (user, password, fullname) VALUES
('jocarsa', 'jocarsa', 'Jose Vicente Carratala');
