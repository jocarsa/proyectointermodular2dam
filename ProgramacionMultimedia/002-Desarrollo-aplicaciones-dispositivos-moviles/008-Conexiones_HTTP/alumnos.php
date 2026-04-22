<?php
header('Content-Type: application/json; charset=utf-8');

$apellidos = [
    'Joshua' => 'Navia',
    'Marcos' => 'González',
    'Angel' => 'Pastor',
    'Sidik' => 'Muniru',
    'Jorge' => 'Calcerrada'
];

$aprobados = [
    'Joshua' => 'sí',
    'Marcos' => 'sí',
    'Angel' => 'sí',
    'Sidik' => 'no',
    'Jorge' => 'no'
];

$metodo = $_SERVER['REQUEST_METHOD'];

// --------------------------------------------------
// PETICIÓN GET -> apellido
// --------------------------------------------------
if ($metodo === 'GET') {
    $peticion = $_GET['peticion'] ?? '';
    $nombre = $_GET['nombre'] ?? '';

    if ($peticion === 'apellido') {
        $apellido = $apellidos[$nombre] ?? 'apellido desconocido';

        echo json_encode([
            'ok' => true,
            'metodo' => 'GET',
            'peticion' => 'apellido',
            'nombre' => $nombre,
            'apellido' => $apellido
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}

// --------------------------------------------------
// PETICIÓN POST -> aprobado
// --------------------------------------------------
if ($metodo === 'POST') {
    $peticion = $_POST['peticion'] ?? '';
    $nombre = $_POST['nombre'] ?? '';

    if ($peticion === 'aprobado') {
        $aprobado = $aprobados[$nombre] ?? 'desconocido';

        echo json_encode([
            'ok' => true,
            'metodo' => 'POST',
            'peticion' => 'aprobado',
            'nombre' => $nombre,
            'aprobado' => $aprobado
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}

http_response_code(400);
echo json_encode([
    'ok' => false,
    'error' => 'Petición no válida'
], JSON_UNESCAPED_UNICODE);
?>