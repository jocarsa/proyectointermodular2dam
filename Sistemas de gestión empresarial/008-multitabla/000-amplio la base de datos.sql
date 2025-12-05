CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('pendiente','procesando','enviado','completado','cancelado') 
           NOT NULL DEFAULT 'pendiente',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_pedidos_clientes
        FOREIGN KEY (cliente_id) REFERENCES clientes(Identificador)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


CREATE TABLE lineas_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total_linea DECIMAL(10,2) AS (cantidad * precio_unitario) STORED,
    CONSTRAINT fk_lineas_pedido_pedidos
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_lineas_pedido_productos
        FOREIGN KEY (producto_id) REFERENCES productos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


CREATE TABLE pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo ENUM('tarjeta','transferencia','paypal','efectivo') NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    referencia_externa VARCHAR(255),
    CONSTRAINT fk_pagos_pedidos
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


CREATE TABLE direcciones_cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    tipo ENUM('envio','facturacion') NOT NULL DEFAULT 'envio',
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    codigo_postal VARCHAR(20),
    pais VARCHAR(100) DEFAULT 'España',
    es_principal TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_direcciones_clientes
        FOREIGN KEY (cliente_id) REFERENCES clientes(Identificador)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

INSERT INTO productos (nombre, descripcion, precio, stock) VALUES
('Portátil 15"', 'Portátil profesional 15 pulgadas, 16GB RAM', 899.00, 10),
('Ratón inalámbrico', 'Ratón ergonómico Bluetooth', 25.90, 50),
('Teclado mecánico', 'Teclado mecánico retroiluminado', 79.90, 30),
('Monitor 27"', 'Monitor IPS 27 pulgadas 144Hz', 299.00, 20),
('Disco SSD 1TB', 'Unidad SSD NVMe 1TB', 129.00, 40);


INSERT INTO pedidos (cliente_id, estado, total) VALUES
(1, 'pendiente', 0.00),
(2, 'procesando', 0.00),
(3, 'pendiente', 0.00);


INSERT INTO lineas_pedido (pedido_id, producto_id, cantidad, precio_unitario)
VALUES
(1, 1, 1, 899.00),  -- portátil
(1, 2, 2, 25.90);   -- 2 ratones


INSERT INTO lineas_pedido (pedido_id, producto_id, cantidad, precio_unitario)
VALUES
(2, 3, 1, 79.90),   -- teclado
(2, 5, 1, 129.00);  -- SSD 1TB


INSERT INTO lineas_pedido (pedido_id, producto_id, cantidad, precio_unitario)
VALUES
(3, 4, 1, 299.00),  -- monitor
(3, 3, 1, 79.90);   -- teclado


UPDATE pedidos SET total = 950.80 WHERE id = 1;
UPDATE pedidos SET total = 208.90 WHERE id = 2;
UPDATE pedidos SET total = 378.90 WHERE id = 3;


INSERT INTO pagos (pedido_id, metodo, importe)
VALUES (1, 'tarjeta', 950.80);


INSERT INTO pagos (pedido_id, metodo, importe)
VALUES (2, 'paypal', 208.90);


INSERT INTO pagos (pedido_id, metodo, importe)
VALUES (3, 'efectivo', 378.90);



