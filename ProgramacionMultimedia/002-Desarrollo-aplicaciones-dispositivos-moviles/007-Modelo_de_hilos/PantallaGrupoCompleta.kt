package com.example.desarrollodeaplicacionesparadispositivosmviles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// --------------------------------------------------
// PANTALLA DE ALUMNOS
// --------------------------------------------------
@Composable
fun PantallaGrupoCompleta(
    grupo: Grupo,
    alumnos: SnapshotStateList<Alumno>,
    siguienteId: Int,
    onSiguienteIdChange: (Int) -> Unit,
    filtroActual: Filtro,
    onFiltroChange: (Filtro) -> Unit,
    audioActivado: Boolean,
    recargandoAlumnos: Boolean,
    onRecargarAlumnos: () -> Unit,
    onInsertarAlumno: (Alumno) -> Boolean,
    onBorrarAlumno: (Alumno) -> Boolean,
    onIrAEditarAlumno: (Alumno) -> Unit,
    irAConfiguracion: () -> Unit
) {
    val context = LocalContext.current

    var mensaje by remember { mutableStateOf("") }

    var nombreInput by remember { mutableStateOf("") }
    var edadInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val nombreFocus = remember { FocusRequester() }
    val edadFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }

    val alumnosFiltrados: List<Alumno> =
        if (filtroActual == Filtro.TODOS) {
            alumnos
        } else if (filtroActual == Filtro.MAYORES) {
            alumnos.filter { it.edad >= 18 }
        } else {
            alumnos.filter { esAlumnoInvalido(it) }
        }

    val scrollState = rememberScrollState()

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            kotlinx.coroutines.delay(2500)
            mensaje = ""
        }
    }

    fun intentarGuardarAlumno() {
        val nombre = nombreInput.trim()
        val edad = edadInput.trim().toIntOrNull()
        val email = emailInput.trim().ifEmpty { null }

        if (nombre.isEmpty()) {
            mensaje = "Error: el nombre está vacío."
            reproducirSonidoSiProcede(
                context = context,
                audioActivado = audioActivado,
                sonidoResId = R.raw.sonido_error
            )
            return
        }

        if (edad == null) {
            mensaje = "Error: la edad no es válida."
            reproducirSonidoSiProcede(
                context = context,
                audioActivado = audioActivado,
                sonidoResId = R.raw.sonido_error
            )
            return
        }

        val nuevo = Alumno(
            id = siguienteId,
            nombre = nombre,
            edad = edad,
            email = email
        )

        val insertado = onInsertarAlumno(nuevo)

        if (!insertado) {
            mensaje = "Error: no se pudo guardar el alumno en SQLite."
            reproducirSonidoSiProcede(
                context = context,
                audioActivado = audioActivado,
                sonidoResId = R.raw.sonido_error
            )
            return
        }

        onSiguienteIdChange(siguienteId + 1)

        nombreInput = ""
        edadInput = ""
        emailInput = ""

        mensaje = "Alumno añadido y guardado en SQLite: ${nuevo.nombre}"
        reproducirSonidoSiProcede(
            context = context,
            audioActivado = audioActivado,
            sonidoResId = R.raw.sonido_acierto
        )
        focusManager.clearFocus()
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
                    text = "Persistencia de datos con SQLite y DataStore",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Grupo: ${grupo.nombre}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Audio activado: ${if (audioActivado) "Sí" else "No"}"
                )
            }

            IconButton(onClick = irAConfiguracion) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Ir a configuración"
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
                    onFiltroChange(Filtro.TODOS)
                    mensaje = "Mostrando todos."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Todos")
            }

            Button(
                onClick = {
                    onFiltroChange(Filtro.MAYORES)
                    mensaje = "Mostrando mayores de edad."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mayores")
            }

            Button(
                onClick = {
                    onFiltroChange(Filtro.INVALIDOS)
                    mensaje = "Mostrando inválidos."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Inválidos")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Filtro actual guardado: ${filtroActual.name}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Recargar alumnos desde SQLite:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onRecargarAlumnos,
            enabled = !recargandoAlumnos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (recargandoAlumnos) {
                    "Recargando..."
                } else {
                    "Recargar lista"
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = recargandoAlumnos
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Recargando alumnos desde SQLite...",
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Añadir alumno y guardarlo en SQLite:",
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
                    intentarGuardarAlumno()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                intentarGuardarAlumno()
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
            AlumnoItem(
                alumno = alumno,
                onBorrarAlumno = { alumnoABorrar ->
                    val borrado = onBorrarAlumno(alumnoABorrar)

                    if (borrado) {
                        mensaje = "Alumno borrado de SQLite: ${alumnoABorrar.nombre}"
                        reproducirSonidoSiProcede(
                            context = context,
                            audioActivado = audioActivado,
                            sonidoResId = R.raw.sonido_acierto
                        )
                    } else {
                        mensaje = "Error: no se pudo borrar el alumno."
                        reproducirSonidoSiProcede(
                            context = context,
                            audioActivado = audioActivado,
                            sonidoResId = R.raw.sonido_error
                        )
                    }
                },
                onEditarAlumno = { alumnoAEditar ->
                    onIrAEditarAlumno(alumnoAEditar)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val totalMayores = alumnos.count { it.edad >= 18 }
        Text(text = "Resumen: total=${alumnos.size}, mayores=$totalMayores")
    }
}