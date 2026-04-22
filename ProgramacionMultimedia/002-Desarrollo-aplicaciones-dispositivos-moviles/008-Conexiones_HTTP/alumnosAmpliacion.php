<?php
header('Content-Type: application/json; charset=utf-8');

$notas = [
    'Joshua' => 8.5,
    'Marcos' => 7.0,
    'Angel' => 9.25,
    'Sidik' => 4.0,
    'Jorge' => 3.5
];

$metodo = $_SERVER['REQUEST_METHOD'];

// --------------------------------------------------
// PETICIÓN GET -> nota
// --------------------------------------------------
if ($metodo === 'GET') {
    $peticion = $_GET['peticion'] ?? '';
    $nombre = $_GET['nombre'] ?? '';

    if ($peticion === 'nota') {
        $nota = $notas[$nombre] ?? 'nota desconocida';

        echo json_encode([
            'ok' => true,
            'metodo' => 'GET',
            'peticion' => 'nota',
            'nombre' => $nombre,
            'nota' => $nota
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}

// --------------------------------------------------
// PETICIÓN POST -> mayorEdad
// --------------------------------------------------
if ($metodo === 'POST') {
    $peticion = $_POST['peticion'] ?? '';
    $nombre = $_POST['nombre'] ?? '';

    if ($peticion === 'mayorEdad') {
        if (!isset($_POST['edad'])) {
            http_response_code(400);
            echo json_encode([
                'ok' => false,
                'error' => 'Falta la edad'
            ], JSON_UNESCAPED_UNICODE);
            exit;
        }

        $edad = intval($_POST['edad']);
        $resultado = ($edad >= 18) ? 'sí' : 'no';

        echo json_encode([
            'ok' => true,
            'metodo' => 'POST',
            'peticion' => 'mayorEdad',
            'nombre' => $nombre,
            'edad' => $edad,
            'mayorEdad' => $resultado
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