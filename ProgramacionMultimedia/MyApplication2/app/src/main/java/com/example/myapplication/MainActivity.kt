package com.example.myapplication
// ==============================
// IMPORTS
// ==============================

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

// ==============================
// ACTIVITY PRINCIPAL
// ==============================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaTareas()
                }
            }
        }
    }
}

// ==============================
// MODELO DE DATOS
// ==============================

data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val estado: String // "Pendiente" o "Completada"
)

// ==============================
// ENUM PARA FILTRO
// ==============================

enum class FiltroTareas {
    TODAS, PENDIENTES, COMPLETADAS
}

// ==============================
// PANTALLA PRINCIPAL
// ==============================

@Composable
fun PantallaTareas() {
    // ---------- CONTEXTO (para sonido) ----------
    // TODO 1: crear un val llamado context usando LocalContext.current
    val context = LocalContext.current

    // ---------- LISTAS DE ESTADO ----------
    // TODO 2: crear un val llamado tareasPendientes como lista mutable de Tarea con remember
    val tareasPendientes = remember {
        mutableStateListOf(
            Tarea(1, "Estudiar Kotlin", "Repasar funciones y clases", "Pendiente"),
            Tarea(2, "Preparar examen", "Revisar Compose", "Pendiente")
        )
    }

    // TODO 3: crear un val llamado tareasCompletadas como lista mutable de Tarea con remember
    val tareasCompletadas = remember {
        mutableStateListOf(
            Tarea(3, "Instalar Android Studio", "Configurar entorno", "Completada")
        )
    }

    // ---------- ESTADOS DE UI ----------
    // TODO 4: crear un var llamado siguienteId inicializado a 1 usando remember
    var siguienteId by remember { mutableStateOf(4) }

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

    // ==============================
    // UI PRINCIPAL
    // ==============================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // ---------- TÍTULO ----------
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

        // ---------- BOTÓN AÑADIR ----------
        Button(
            onClick = {
                val titulo = tituloInput.trim()

                // TODO 10: comprobar que el título no esté vacío.
                // Si está vacío, mostrar mensaje de error, reproducir sonido de error y salir
                if (titulo.isEmpty()) {
                    mensaje = "Error: el título no puede estar vacío."
                    reproducirSonido(context, R.raw.sonido_error)
                    return@Button
                }

                // TODO 11: crear un objeto Tarea con estado "Pendiente"
                val nuevaTarea = Tarea(
                    id = siguienteId,
                    titulo = titulo,
                    descripcion = descripcionInput,
                    estado = "Pendiente"
                )

                // TODO 12: añadir la tarea a la lista de pendientes y aumentar el ID
                tareasPendientes.add(nuevaTarea)
                siguienteId++

                // TODO 13: limpiar los campos del formulario
                tituloInput = ""
                descripcionInput = ""

                mensaje = "Tarea añadida correctamente."

                // TODO 14: reproducir sonido de acierto
                reproducirSonido(context, R.raw.sonido_acierto)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir tarea")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- MENSAJE ----------
        if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==============================
        // FILTROS
        // ==============================

        Text("Filtro", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                // TODO 15: cambiar el filtro para mostrar TODAS las tareas
                onClick = { filtro = FiltroTareas.TODAS },
                modifier = Modifier.weight(1f)
            ) { Text("Todas") }

            Button(
                // TODO 16: cambiar el filtro para mostrar solo tareas pendientes
                onClick = { filtro = FiltroTareas.PENDIENTES },
                modifier = Modifier.weight(1f)
            ) { Text("Pendientes") }

            Button(
                // TODO 17: cambiar el filtro para mostrar solo tareas completadas
                onClick = { filtro = FiltroTareas.COMPLETADAS },
                modifier = Modifier.weight(1f)
            ) { Text("Completadas") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==============================
        // LISTA DE PENDIENTES
        // ==============================

        if (filtro == FiltroTareas.TODAS || filtro == FiltroTareas.PENDIENTES) {

            Text("Pendientes (${tareasPendientes.size})", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (tareasPendientes.isEmpty()) {
                Text("No hay tareas pendientes.")
            } else {
                tareasPendientes.forEach { tarea ->

                    TarjetaTareaPendiente(
                        tarea = tarea,
                        onCompletar = {
                            tareasPendientes.remove(tarea)
                            tareasCompletadas.add(tarea.copy(estado = "Completada"))

                            // TODO 18: reproducir sonido de acierto
                            reproducirSonido(context, R.raw.sonido_acierto)
                        },
                        onEliminar = {
                            tareasPendientes.remove(tarea)

                            // TODO 19: reproducir sonido de error
                            reproducirSonido(context, R.raw.sonido_error)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ==============================
        // LISTA DE COMPLETADAS
        // ==============================

        if (filtro == FiltroTareas.TODAS || filtro == FiltroTareas.COMPLETADAS) {

            Text("Completadas (${tareasCompletadas.size})", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (tareasCompletadas.isEmpty()) {
                Text("No hay tareas completadas.")
            } else {
                tareasCompletadas.forEach { tarea ->

                    TarjetaTareaCompletada(
                        tarea = tarea,
                        onMoverAPendientes = {
                            tareasCompletadas.remove(tarea)
                            tareasPendientes.add(tarea.copy(estado = "Pendiente"))

                            // TODO 20: reproducir sonido de acierto
                            reproducirSonido(context, R.raw.sonido_acierto)
                        },
                        onEliminar = {
                            tareasCompletadas.remove(tarea)

                            // TODO 21: reproducir sonido de error
                            reproducirSonido(context, R.raw.sonido_error)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ==============================
// TARJETA: TAREA PENDIENTE
// ==============================

@Composable
fun TarjetaTareaPendiente(
    tarea: Tarea,
    onCompletar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row {
                // TODO 22: añadir imagen iconotarea.png usando Image y painterResource
                Image(
                    painter = painterResource(id = R.drawable.iconotarea),
                    contentDescription = "Icono tarea",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(tarea.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(tarea.descripcion)

                    // TODO 23: mostrar el estado de la tarea
                    Text("Estado: ${tarea.estado}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEliminar) { Text("Eliminar") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onCompletar) {
                    Text("Completar")
                }
            }
        }
    }
}

// ==============================
// TARJETA: TAREA COMPLETADA
// ==============================

@Composable
fun TarjetaTareaCompletada(
    tarea: Tarea,
    onMoverAPendientes: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row {
                Image(
                    // TODO 24: añadir imagen iconotarea.png usando Image y painterResource
                    painter = painterResource(id = R.drawable.iconotarea),
                    contentDescription = "Icono tarea",
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(tarea.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(tarea.descripcion)

                    // TODO 25: mostrar el estado de la tarea
                    Text("Estado: ${tarea.estado}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEliminar) { Text("Eliminar") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onMoverAPendientes) {
                    Text("Mover a pendientes")
                }
            }
        }
    }
}

// ==============================
// FUNCIÓN DE SONIDO
// ==============================

fun reproducirSonido(context: android.content.Context, resId: Int) {
    val mediaPlayer = MediaPlayer.create(context, resId)
    mediaPlayer.start()

    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}