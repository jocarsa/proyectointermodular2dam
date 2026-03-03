package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaGrupoCompleta()
                }
            }
        }
    }
}

// Tipo enumerado con varios posibles valores
enum class Filtro {
    TODOS, MAYORES, INVALIDOS
}

@Composable
fun PantallaGrupoCompleta() {

    // Datos iniciales
    val grupo = remember { crearGrupoDemo() }

    // Estado: alumnos en memoria (lista mutable observable por Compose)
    // Añade todos los alumnos a esta lista
    val alumnos = remember { mutableStateListOf<Alumno>().apply { addAll(grupo.alumnos) } }

    // Estado: filtro actual
    var filtro by remember { mutableStateOf(Filtro.TODOS) }

    // Estado: mensaje informativo
    var mensaje by remember { mutableStateOf("Listo. Puedes filtrar o añadir alumnos.") }

    // Estado: formulario
    var nombreInput by remember { mutableStateOf("") }
    var edadInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    // ID automático
    var siguienteId by remember {
        mutableStateOf(
            (alumnos.maxOfOrNull { it.id } ?: 0) + 1
        )
    }

    // Filtrado
    val alumnosFiltrados: List<Alumno>

    if (filtro == Filtro.TODOS) {
        alumnosFiltrados = alumnos
    } else if (filtro == Filtro.MAYORES) {
        alumnosFiltrados = alumnos.filter { it.edad >= 18 }
    } else {
        alumnosFiltrados = alumnos.filter { esAlumnoInvalido(it) }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Interfaces de usuario (Compose)",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Grupo: ${grupo.nombre}")

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Filtro:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { filtro = Filtro.TODOS; mensaje = "Mostrando todos." }, modifier = Modifier.weight(1f)) {
                Text("Todos")
            }
            Button(onClick = { filtro = Filtro.MAYORES; mensaje = "Mostrando mayores de edad." }, modifier = Modifier.weight(1f)) {
                Text("Mayores")
            }
            Button(onClick = { filtro = Filtro.INVALIDOS; mensaje = "Mostrando inválidos." }, modifier = Modifier.weight(1f)) {
                Text("Inválidos")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Añadir alumno con TextField
        Text(text = "Añadir alumno:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = nombreInput,
            onValueChange = { nombreInput = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = edadInput,
            onValueChange = { edadInput = it },
            label = { Text("Edad (número)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val nombre = nombreInput.trim()
                val edad = edadInput.trim().toIntOrNull()
                val email = emailInput.trim().ifEmpty { null }

                if (nombre.isEmpty()) {
                    mensaje = "Error: el nombre está vacío."
                    return@Button
                }

                if (edad == null) {
                    mensaje = "Error: la edad no es un número válido."
                    return@Button
                }

                val nuevo = Alumno(
                    id = siguienteId,
                    nombre = nombre,
                    edad = edad,
                    email = email
                )

                alumnos.add(nuevo)
                siguienteId++

                // limpiamos formulario
                nombreInput = ""
                edadInput = ""
                emailInput = ""

                mensaje = "Alumno añadido: ${nuevo.nombre} (id=${nuevo.id})"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje
        Text(text = "Mensaje:")
        Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de alumnos en pantalla
        Text(
            text = "Lista de alumnos (${alumnosFiltrados.size}):",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        alumnosFiltrados.forEach { alumno ->
            // Asignamos el valor de cada parámetro de la función por nombre del parámetro y no por orden de estos
            AlumnoItem(alumno = alumno)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen simple
        val totalMayores = alumnos.count { it.edad >= 18 }
        Text(text = "Resumen: total=${alumnos.size}, mayores=$totalMayores")
    }
}

@Composable
fun AlumnoItem(alumno: Alumno) {
    val categoria = etiquetaEdad(alumno.edad)
    val emailTexto = alumno.email ?: "sin email"
    val invalido = esAlumnoInvalido(alumno)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "${alumno.nombre} (id=${alumno.id})", style = MaterialTheme.typography.titleMedium)
            Text(text = "Edad: ${alumno.edad} -> $categoria")
            Text(text = "Email: $emailTexto")
            Text(text = "Estado: ${if (invalido) "INVÁLIDO" else "OK"}")
        }
    }
}

/**
 * Criterio simple para "inválido":
 * - edad < 0
 * - email no nulo y no válido
 *
 * (Así un email null NO se considera inválido automáticamente)
 */
fun esAlumnoInvalido(alumno: Alumno): Boolean {
    if (alumno.edad < 0) return true
    if (alumno.email != null && !validarEmail(alumno.email)) return true
    return false
}