package com.example.desarrollodeaplicacionesparadispositivosmviles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --------------------------------------------------
// PANTALLA DE EDICIÓN DE ALUMNOS
// --------------------------------------------------
@Composable
fun PantallaEdicionAlumno(
    alumno: Alumno,
    audioActivado: Boolean,
    onActualizarAlumno: (Alumno) -> Boolean,
    volverAAlumnos: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var mensaje by remember { mutableStateOf("") }

    var nombreInput by remember(alumno.id) { mutableStateOf(alumno.nombre) }
    var edadInput by remember(alumno.id) { mutableStateOf(alumno.edad.toString()) }
    var emailInput by remember(alumno.id) { mutableStateOf(alumno.email ?: "") }

    val nombreFocus = remember { FocusRequester() }
    val edadFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    // Estado nuevo para mostrar visualmente el guardado
    var guardandoCambios by remember { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(2500)
            mensaje = ""
        }
    }

    fun intentarActualizarAlumno() {
        if (guardandoCambios) {
            return
        }

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

        val alumnoActualizado = Alumno(
            id = alumno.id,
            nombre = nombre,
            edad = edad,
            email = email
        )

        guardandoCambios = true
        focusManager.clearFocus()

        // Lanzamos una corrutina con su propio código (en vez de llamar a una función)
        coroutineScope.launch {
            // Simulamos espera para que se note visualmente el estado de guardado
            delay(2000L)

            val actualizado = onActualizarAlumno(alumnoActualizado)

            guardandoCambios = false

            if (!actualizado) {
                mensaje = "Error: no se pudo actualizar el alumno en SQLite."
                reproducirSonidoSiProcede(
                    context = context,
                    audioActivado = audioActivado,
                    sonidoResId = R.raw.sonido_error
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Edición de alumno",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "ID del alumno: ${alumno.id}")

        Spacer(modifier = Modifier.height(16.dp))

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
            ),
            enabled = !guardandoCambios
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            ),
            enabled = !guardandoCambios
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                    intentarActualizarAlumno()
                }
            ),
            enabled = !guardandoCambios
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    intentarActualizarAlumno()
                },
                modifier = Modifier.weight(1f),
                enabled = !guardandoCambios
            ) {
                Text(
                    text = if (guardandoCambios) {
                        "Guardando..."
                    } else {
                        "Guardar cambios"
                    }
                )
            }

            Button(
                onClick = volverAAlumnos,
                modifier = Modifier.weight(1f),
                enabled = !guardandoCambios
            ) {
                Text("Cancelar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = guardandoCambios
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Guardando cambios en SQLite...",
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = mensaje.isNotEmpty()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}