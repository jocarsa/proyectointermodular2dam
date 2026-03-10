package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
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
    val alumnos = remember { mutableStateListOf<Alumno>().apply { addAll(grupo.alumnos) } }

    // Estado: filtro actual
    var filtro by remember { mutableStateOf(Filtro.TODOS) }

    // Estado: mensaje informativo
    var mensaje by remember { mutableStateOf("Listo. Puedes filtrar o añadir alumnos.") }

    // Estado: formulario
    var nombreInput by remember { mutableStateOf("") }
    var edadInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    // Gestor de foco
    val focusManager = LocalFocusManager.current

    // Controladores de foco
    val nombreFocus = remember { FocusRequester() }
    val edadFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }

    // ID automático
    var siguienteId by remember {
        mutableStateOf((alumnos.maxOfOrNull { it.id } ?: 0) + 1)
    }

    // Filtrado
    val alumnosFiltrados: List<Alumno> =
        if (filtro == Filtro.TODOS) {
            alumnos
        } else if (filtro == Filtro.MAYORES) {
            alumnos.filter { it.edad >= 18 }
        } else {
            alumnos.filter { esAlumnoInvalido(it) }
        }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Acontecimientos del teclado (Compose)",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Grupo: ${grupo.nombre}")

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Filtro:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    filtro = Filtro.TODOS
                    mensaje = "Mostrando todos."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Todos")
            }

            Button(
                onClick = {
                    filtro = Filtro.MAYORES
                    mensaje = "Mostrando mayores de edad."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mayores")
            }

            Button(
                onClick = {
                    filtro = Filtro.INVALIDOS
                    mensaje = "Mostrando inválidos."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Inválidos")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Añadir alumno con teclado:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(6.dp))

        // CAMPO NOMBRE
        OutlinedTextField(
            value = nombreInput,
            onValueChange = { nombreInput = it },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nombreFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    edadFocus.requestFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // CAMPO EDAD
        OutlinedTextField(
            value = edadInput,
            onValueChange = { edadInput = it },
            label = { Text("Edad") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(edadFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailFocus.requestFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // CAMPO EMAIL
        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val nombre = nombreInput.trim()
                    val edad = edadInput.trim().toIntOrNull()
                    val email = emailInput.trim().ifEmpty { null }

                    if (nombre.isEmpty()) {
                        mensaje = "Error: el nombre está vacío."
                        return@KeyboardActions
                    }

                    if (edad == null) {
                        mensaje = "Error: la edad no es válida."
                        return@KeyboardActions
                    }

                    val nuevo = Alumno(
                        id = siguienteId,
                        nombre = nombre,
                        edad = edad,
                        email = email
                    )

                    alumnos.add(nuevo)
                    siguienteId++

                    nombreInput = ""
                    edadInput = ""
                    emailInput = ""

                    mensaje = "Alumno añadido: ${nuevo.nombre}"
                    focusManager.clearFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // BOTÓN NORMAL (opcional)
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
                    mensaje = "Error: la edad no es válida."
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

                nombreInput = ""
                edadInput = ""
                emailInput = ""

                mensaje = "Alumno añadido: ${nuevo.nombre}"
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Mensaje:")
        Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lista de alumnos (${alumnosFiltrados.size}):",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        alumnosFiltrados.forEach { alumno ->
            AlumnoItem(alumno = alumno)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val totalMayores = alumnos.count { it.edad >= 18 }
        Text(text = "Resumen: total=${alumnos.size}, mayores=$totalMayores")
    }
}

/**
 * Asigna una imagen NORMAL (bitmap) distinta según el id del alumno.
 * Necesitas en drawable: avatar1, avatar2, avatar3
 */
fun avatarParaAlumno(alumno: Alumno): Int {
    return when (alumno.id % 3) {
        0 -> R.drawable.avatar1
        1 -> R.drawable.avatar2
        else -> R.drawable.avatar3
    }
}

/**
 * Icono de estado usando solo iconos básicos.
 */
@Composable
fun IconoEstadoAlumno(alumno: Alumno) {
    val invalido = esAlumnoInvalido(alumno)
    val esMenor = alumno.edad in 0..17

    when {
        invalido -> Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Alumno inválido"
        )

        esMenor -> Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Alumno menor"
        )

        else -> Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Alumno correcto"
        )
    }
}

@Composable
fun AlumnoItem(alumno: Alumno) {
    val categoria = etiquetaEdad(alumno.edad)
    val emailTexto = alumno.email ?: "sin email"
    val invalido = esAlumnoInvalido(alumno)
    val avatarRes = avatarParaAlumno(alumno)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Avatar de ${alumno.nombre}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_student),
                        contentDescription = "Icono alumno"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${alumno.nombre} (id=${alumno.id})",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconoEstadoAlumno(alumno)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Edad: ${alumno.edad} -> $categoria")
                Text(text = "Email: $emailTexto")
                Text(text = "Estado: ${if (invalido) "INVÁLIDO" else "OK"}")
            }
        }
    }
}

/**
 * Criterio simple para "inválido":
 * - edad < 0
 * - email no nulo y no válido
 */
fun esAlumnoInvalido(alumno: Alumno): Boolean {
    if (alumno.edad < 0) return true
    if (alumno.email != null && !validarEmail(alumno.email)) return true
    return false
}