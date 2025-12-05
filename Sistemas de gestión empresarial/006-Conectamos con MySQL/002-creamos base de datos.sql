sudo mysql -u root -p

CREATE DATABASE clientes2526; -- creamos base de datos

USE clientes2526; -- entramos en la base de datos

CREATE TABLE clientes (
    Identificador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    apellidos VARCHAR(255),
    email VARCHAR(255)
);

INSERT INTO clientes VALUES(
	NULL,
  'Jose Vicente',
  'Carratala Sanchis',
  'info@jocarsa.com'
);

INSERT INTO clientes VALUES (NULL, 'María', 'Gómez Ruiz', 'maria.gomez@example.com');
INSERT INTO clientes VALUES (NULL, 'Carlos', 'López Martín', 'carlos.lopez@example.com');
INSERT INTO clientes VALUES (NULL, 'Ana', 'Serrano Díaz', 'ana.serrano@example.com');
INSERT INTO clientes VALUES (NULL, 'Lucía', 'Navarro Torres', 'lucia.navarro@example.com');
INSERT INTO clientes VALUES (NULL, 'Javier', 'Pérez Molina', 'javier.perez@example.com');
INSERT INTO clientes VALUES (NULL, 'Sandra', 'Hernández Soto', 'sandra.hernandez@example.com');
INSERT INTO clientes VALUES (NULL, 'David', 'Ramírez Vidal', 'david.ramirez@example.com');
INSERT INTO clientes VALUES (NULL, 'Elena', 'Castro Gil', 'elena.castro@example.com');
INSERT INTO clientes VALUES (NULL, 'Raúl', 'Ibáñez León', 'raul.ibanez@example.com');
INSERT INTO clientes VALUES (NULL, 'Patricia', 'Ortega Campos', 'patricia.ortega@example.com');
INSERT INTO clientes VALUES (NULL, 'Miguel', 'Domínguez Sáez', 'miguel.dominguez@example.com');
INSERT INTO clientes VALUES (NULL, 'Laura', 'Fuentes Pardo', 'laura.fuentes@example.com');
INSERT INTO clientes VALUES (NULL, 'Rubén', 'Rey Aguado', 'ruben.rey@example.com');
INSERT INTO clientes VALUES (NULL, 'Susana', 'Vega Barrios', 'susana.vega@example.com');
INSERT INTO clientes VALUES (NULL, 'Óscar', 'Cortés Bravo', 'oscar.cortes@example.com');
INSERT INTO clientes VALUES (NULL, 'Beatriz', 'Carrillo Ramos', 'beatriz.carrillo@example.com');
INSERT INTO clientes VALUES (NULL, 'Hugo', 'Santos Ferrer', 'hugo.santos@example.com');
INSERT INTO clientes VALUES (NULL, 'Cristina', 'Marín Lozano', 'cristina.marin@example.com');
INSERT INTO clientes VALUES (NULL, 'Sergio', 'Herrera Rivas', 'sergio.herrera@example.com');
INSERT INTO clientes VALUES (NULL, 'Natalia', 'Gallardo Prieto', 'natalia.gallardo@example.com');

SELECT * FROM clientes;

