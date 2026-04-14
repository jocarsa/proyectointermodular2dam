package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import sun.awt.www.content.audio.wav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme { Surface(modifier = Modifier.fillMaxSize()) { PantallaTareas() } }
        }
    }
}

// ==============================
// MODELO
// ==============================

data class Tarea(val id: Int, val titulo: String, val descripcion: String, val estado: String)

// ==============================
// ENUM FILTRO
// ==============================

enum class FiltroTareas {
    TODAS,
    PENDIENTES,
    COMPLETADAS
}

// ==============================
// PANTALLA PRINCIPAL
// ==============================

@Composable
fun PantallaTareas() {

    // TODO 1: crear un val llamado context usando LocalContext.current

    val context = LocalContext.current

    // TODO 2: crear un val llamado tareasPendientes como lista mutable de Tarea con remember

    val tareasPendientes = remember { mutableStateListOf < Tarea() }

    // TODO 3: crear un val llamado tareasCompletadas como lista mutable de Tarea con remember

    val tareasCompletadas = remember { mutableStateListOf < Tarea() }

    // TODO 4: crear un var llamado siguienteId inicializado a 1 usando remember

    var siguienteId by remember { mutableStateOf(1) }

    // TODO 5: crear un var llamado tituloInput como String vacío usando remember

    var tituloInput by remember { mutableStateOf("") }

    // TODO 6: crear un var llamado descripcionInput como String vacío usando remember

    var descripcionInput by remember { mutableStateOf("") }

    // TODO 7: crear un var llamado mensaje como String vacío usando remember

    var mensaje by remember { mutableStateOf("") }

    // TODO 8: crear un var llamado filtro inicializado a FiltroTareas.TODAS usando remember

    var filtro by remember { mutableStateOf(FiltroTareas.TODAS) }

    // TODO 9: crear un val llamado scrollState usando rememberScrollState()

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp)) {
        Text("Lista de tareas", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        // ==============================
        // FORMULARIO
        // ==============================

        OutlinedTextField(
                value = tituloInput,
                onValueChange = { tituloInput = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
                value = descripcionInput,
                onValueChange = { descripcionInput = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
                onClick = {

                    // TODO 10: comprobar que el título no esté vacío.
                    // Si está vacío, mostrar mensaje de error, reproducir sonido de error y salir
                    if (tituloInput.isBlank()) {
                        mensaje = "El título no puede estar vacío"
                        val mediaPlayer = MediaPlayer.create(context, sonido_error.wav)
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener { it.release() }
                        return@Button
                    }
                    val titulo = tituloInput.trim()
                    val descripcion = descripcionInput.trim()

                    // TODO 11: crear un objeto Tarea con estado "Pendiente"
                    val nuevaTarea = Tarea(siguienteId, titulo, descripcion, "Pendiente")

                    // TODO 12: añadir la tarea a la lista de pendientes y aumentar el ID

                    tareasPendientes.add(nuevaTarea)
                    siguienteId++

                    // TODO 13: limpiar los campos del formulario

                    tituloInput = ""
                    descripcionInput = ""

                    mensaje = "Tarea añadida correctamente"

                    // TODO 14: reproducir sonido de acierto

                    val mediaPlayer1 = MediaPlayer.create(context, sonido_acierto.wav)
                    mediaPlayer1.start()
                    mediaPlayer1.setOnCompletionListener { it.release() }
                },
                modifier = Modifier.fillMaxWidth()
        ) { Text("Añadir tarea") }

        Spacer(modifier = Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==============================
        // FILTROS
        // ==============================

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                    onClick = {
                        // TODO 15: cambiar el filtro para mostrar TODAS las tareas
                        filtro = FiltroTareas.TODAS
                    },
                    modifier = Modifier.weight(1f)
            ) { Text("Todas") }

            Button(
                    onClick = {
                        // TODO 16: cambiar el filtro para mostrar solo tareas pendientes
                        filtro = FiltroTareas.PENDIENTES
                    },
                    modifier = Modifier.weight(1f)
            ) { Text("Pendientes") }

            Button(
                    onClick = {
                        // TODO 17: cambiar el filtro para mostrar solo tareas completadas
                        filtro = FiltroTareas.COMPLETADAS
                    },
                    modifier = Modifier.weight(1f)
            ) { Text("Completadas") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==============================
        // LISTA PENDIENTES
        // ==============================

        if (filtro == FiltroTareas.TODAS || filtro == FiltroTareas.PENDIENTES) {

            Text("Pendientes", style = MaterialTheme.typography.titleMedium)

            tareasPendientes.forEach { tarea ->
                TarjetaTareaPendiente(
                        tarea = tarea,
                        onCompletar = {
                            tareasPendientes.remove(tarea)
                            tareasCompletadas.add(tarea.copy(estado = "Completada"))

                            // TODO 18: reproducir sonido de acierto
                            val mediaPlayer = MediaPlayer.create(context, sonido_acierto.wav)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                        },
                        onEliminar = {
                            tareasPendientes.remove(tarea)

                            // TODO 19: reproducir sonido de error
                            val mediaPlayer = MediaPlayer.create(context, sonido_error.wav)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ==============================
        // LISTA COMPLETADAS
        // ==============================

        if (filtro == FiltroTareas.TODAS || filtro == FiltroTareas.COMPLETADAS) {

            Text("Completadas", style = MaterialTheme.typography.titleMedium)

            tareasCompletadas.forEach { tarea ->
                TarjetaTareaCompletada(
                        tarea = tarea,
                        onMover = {
                            tareasCompletadas.remove(tarea)
                            tareasPendientes.add(tarea.copy(estado = "Pendiente"))

                            // TODO 20: reproducir sonido de acierto
                            val mediaPlayer = MediaPlayer.create(context, sonido_acierto.wav)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                        },
                        onEliminar = {
                            tareasCompletadas.remove(tarea)

                            // TODO 21: reproducir sonido de error
                            val mediaPlayer = MediaPlayer.create(context, sonido_error.wav)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ==============================
// TARJETA PENDIENTE
// ==============================

@Composable
fun TarjetaTareaPendiente(tarea: Tarea, onCompletar: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {

                // TODO 22: añadir imagen iconotarea.png usando Image y painterResource

                Image(
                        painter = painterResource(id = R.drawable.iconotarea),
                        contentDescription = "Icono tarea",
                        modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(tarea.titulo)
                    Text(tarea.descripcion)

                    // TODO 23: mostrar el estado de la tarea

                    Text(text = tarea.estado)
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onEliminar) { Text("Eliminar") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onCompletar) { Text("Completar") }
            }
        }
    }
}

// ==============================
// TARJETA COMPLETADA
// ==============================

@Composable
fun TarjetaTareaCompletada(tarea: Tarea, onMover: () -> Unit, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {

                // TODO 24: añadir imagen iconotarea.png usando Image y painterResource
                Image(
                        painter = painterResource(id = R.drawable.iconotarea),
                        contentDescription = "Icono tarea",
                        modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(tarea.titulo)
                    Text(tarea.descripcion)

                    // TODO 25: mostrar el estado de la tarea
                    Text(text = tarea.estado)
                }
            }

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onEliminar) { Text("Eliminar") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onMover) { Text("Mover a pendientes") }
            }
        }
    }
}

// ==============================
// SONIDO
// ==============================

fun reproducirSonido(context: android.content.Context, resId: Int) {
    val mediaPlayer = MediaPlayer.create(context, resId)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener { it.release() }
}
