package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppPantallas()
                }
            }
        }
    }
}

// --------------------------------------------------
// CONTROL SIMPLE DE ESCENAS
// --------------------------------------------------
enum class Escena {
    ALUMNOS, PROFESORES
}

@Composable
fun AppPantallas() {
    var escenaActual by remember { mutableStateOf(Escena.ALUMNOS) }

    val grupo = remember { crearGrupoDemo() }

    val alumnos = remember {
        mutableStateListOf<Alumno>().apply {
            addAll(grupo.alumnos)
        }
    }

    var siguienteId by remember {
        mutableStateOf((alumnos.maxOfOrNull { it.id } ?: 0) + 1)
    }

    if (escenaActual == Escena.ALUMNOS) {
        PantallaGrupoCompleta(
            grupo = grupo,
            alumnos = alumnos,
            siguienteId = siguienteId,
            onSiguienteIdChange = { nuevoId ->
                siguienteId = nuevoId
            },
            irAProfesores = {
                escenaActual = Escena.PROFESORES
            }
        )
    } else {
        PantallaProfesores(
            volverAAlumnos = {
                escenaActual = Escena.ALUMNOS
            }
        )
    }
}

enum class Filtro {
    TODOS, MAYORES, INVALIDOS
}

@Composable
fun PantallaGrupoCompleta(
    grupo: Grupo,
    alumnos: SnapshotStateList<Alumno>,
    siguienteId: Int,
    onSiguienteIdChange: (Int) -> Unit,
    irAProfesores: () -> Unit
) {
    // El contexto representa la actividad actual
    // Lo utilizamos para reproducir audio (mediante MediaPlayer)
    val context = LocalContext.current

    var filtro by remember { mutableStateOf(Filtro.TODOS) }
    var mensaje by remember { mutableStateOf("") }

    var nombreInput by remember { mutableStateOf("") }
    var edadInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val nombreFocus = remember { FocusRequester() }
    val edadFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }

    val alumnosFiltrados: List<Alumno> =
        if (filtro == Filtro.TODOS) {
            alumnos
        } else if (filtro == Filtro.MAYORES) {
            alumnos.filter { it.edad >= 18 }
        } else {
            alumnos.filter { esAlumnoInvalido(it) }
        }

    val scrollState = rememberScrollState()

    // El mensaje desaparece solo para poder volver a ver la animación
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2500)
            mensaje = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Técnicas de animación y sonido",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Grupo: ${grupo.nombre}")
            }

            IconButton(onClick = irAProfesores) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Ir a pantalla de profesores"
                )
            }
        }

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
                        reproducirSonido(context, R.raw.sonido_error)
                        return@KeyboardActions
                    }

                    if (edad == null) {
                        mensaje = "Error: la edad no es válida."
                        reproducirSonido(context, R.raw.sonido_error)
                        return@KeyboardActions
                    }

                    val nuevo = Alumno(
                        id = siguienteId,
                        nombre = nombre,
                        edad = edad,
                        email = email
                    )

                    alumnos.add(nuevo)
                    onSiguienteIdChange(siguienteId + 1)

                    nombreInput = ""
                    edadInput = ""
                    emailInput = ""

                    mensaje = "Alumno añadido: ${nuevo.nombre}"
                    reproducirSonido(context, R.raw.sonido_acierto)
                    focusManager.clearFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val nombre = nombreInput.trim()
                val edad = edadInput.trim().toIntOrNull()
                val email = emailInput.trim().ifEmpty { null }

                if (nombre.isEmpty()) {
                    mensaje = "Error: el nombre está vacío."
                    reproducirSonido(context, R.raw.sonido_error)
                    return@Button
                }

                if (edad == null) {
                    mensaje = "Error: la edad no es válida."
                    reproducirSonido(context, R.raw.sonido_error)
                    return@Button
                }

                val nuevo = Alumno(
                    id = siguienteId,
                    nombre = nombre,
                    edad = edad,
                    email = email
                )

                alumnos.add(nuevo)
                onSiguienteIdChange(siguienteId + 1)

                nombreInput = ""
                edadInput = ""
                emailInput = ""

                mensaje = "Alumno añadido: ${nuevo.nombre}"
                reproducirSonido(context, R.raw.sonido_acierto)
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Mensaje:")

        AnimatedVisibility(
            visible = mensaje.isNotEmpty()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

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

@Composable
fun PantallaProfesores(volverAAlumnos: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Pantalla de profesores",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Esta pantalla está vacía a propósito.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ejercicio: añadir un listado de profesores con formulario para agregarlos, siguiendo una estructura similar a la pantalla de alumnos."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = volverAAlumnos) {
            Text("Volver a alumnos")
        }
    }
}

fun avatarParaAlumno(alumno: Alumno): Int {
    return when (alumno.id % 3) {
        0 -> R.drawable.avatar1
        1 -> R.drawable.avatar2
        else -> R.drawable.avatar3
    }
}

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
    var expandido by remember { mutableStateOf(false) }

    val categoria = etiquetaEdad(alumno.edad)
    val emailTexto = alumno.email ?: "sin email"
    val invalido = esAlumnoInvalido(alumno)
    val avatarRes = avatarParaAlumno(alumno)

    val infiniteTransition = rememberInfiniteTransition(label = "transicionNombre")

    val colorNombre by if (invalido) {
        infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.error,
            targetValue = MaterialTheme.colorScheme.onSurface,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "colorNombreInvalido"
        )
    } else {
        rememberUpdatedState(MaterialTheme.colorScheme.primary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
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
                            style = MaterialTheme.typography.titleMedium,
                            color = colorNombre
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconoEstadoAlumno(alumno)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Edad: ${alumno.edad} -> $categoria"
                    )

                    if (expandido) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Email: $emailTexto")

                        Text(
                            text = "Estado: ${if (invalido) "INVÁLIDO" else "OK"}"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        expandido = !expandido
                    }
                ) {
                    Text(if (expandido) "Menos" else "Más")
                }
            }
        }
    }
}

fun esAlumnoInvalido(alumno: Alumno): Boolean {
    if (alumno.edad < 0) return true
    if (alumno.email != null && !validarEmail(alumno.email)) return true
    return false
}

/**
 * Reproduce un sonido corto desde res/raw
 * Recibe el contexto y el nombre del archivo de audio
 */
fun reproducirSonido(context: android.content.Context, sonidoResId: Int) {
    // Creamos el reproductor
    val mediaPlayer = MediaPlayer.create(context, sonidoResId)
    // Reproducimos el audio
    mediaPlayer.start()

    // Cuando el sonido se termina liberamos memoria
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}