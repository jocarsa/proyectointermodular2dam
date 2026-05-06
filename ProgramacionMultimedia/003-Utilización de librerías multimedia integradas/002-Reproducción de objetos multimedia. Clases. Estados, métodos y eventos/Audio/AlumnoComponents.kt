package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --------------------------------------------------
// FUNCIONES VISUALES DE ALUMNOS
// --------------------------------------------------
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
fun AlumnoItem(
    alumno: Alumno,
    audioActivado: Boolean,
    onBorrarAlumno: (Alumno) -> Unit,
    onEditarAlumno: (Alumno) -> Unit,
    onVerVideoAlumno: (Alumno) -> Unit
) {
    val context = LocalContext.current

    var expandido by remember { mutableStateOf(false) }

    val categoria = etiquetaEdad(alumno.edad)
    val emailTexto = alumno.email ?: "sin email"
    val invalido = esAlumnoInvalido(alumno)
    val avatarRes = avatarParaAlumno(alumno)

    val infiniteTransition = rememberInfiniteTransition(label = "transicionNombre")
    val coroutineScope = rememberCoroutineScope()

    // Estados para la información del servidor
    var apellidoServidor by remember(alumno.id) { mutableStateOf("") }
    var aprobadoServidor by remember(alumno.id) { mutableStateOf("") }
    var cargandoServidor by remember(alumno.id) { mutableStateOf(false) }
    var errorServidor by remember(alumno.id) { mutableStateOf("") }

    // Estados nuevos del audio largo
    var mediaPlayer by remember(alumno.id) { mutableStateOf<MediaPlayer?>(null) }
    var estadoAudio by remember(alumno.id) { mutableStateOf(EstadoAudio.PREPARADO) }
    var posicionAudioMs by remember(alumno.id) { mutableIntStateOf(0) }
    var duracionAudioMs by remember(alumno.id) { mutableIntStateOf(0) }

    fun liberarAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun prepararAudioSiHaceFalta() {
        if (mediaPlayer == null) {
            val nuevoPlayer = MediaPlayer.create(context, audioPresentacionDemo())

            nuevoPlayer.setOnCompletionListener {
                estadoAudio = EstadoAudio.FINALIZADO
                posicionAudioMs = nuevoPlayer.duration
                duracionAudioMs = nuevoPlayer.duration
            }

            mediaPlayer = nuevoPlayer
            posicionAudioMs = 0
            duracionAudioMs = nuevoPlayer.duration
            estadoAudio = EstadoAudio.PREPARADO
        }
    }

    fun reproducirAudio() {
        if (!audioActivado) {
            return
        }

        prepararAudioSiHaceFalta()

        mediaPlayer?.start()
        duracionAudioMs = mediaPlayer?.duration ?: 0
        estadoAudio = EstadoAudio.REPRODUCIENDO
    }

    fun pausarAudio() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            posicionAudioMs = mediaPlayer?.currentPosition ?: posicionAudioMs
            estadoAudio = EstadoAudio.PAUSADO
        }
    }

    fun detenerAudio() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            liberarAudio()
        }

        posicionAudioMs = 0
        duracionAudioMs = 0
        estadoAudio = EstadoAudio.DETENIDO
    }

    LaunchedEffect(estadoAudio, mediaPlayer) {
        while (estadoAudio == EstadoAudio.REPRODUCIENDO && mediaPlayer != null) {
            posicionAudioMs = mediaPlayer?.currentPosition ?: posicionAudioMs
            duracionAudioMs = mediaPlayer?.duration ?: duracionAudioMs
            delay(300)
        }
    }

    DisposableEffect(alumno.id) {
        onDispose {
            liberarAudio()
        }
    }

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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
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

                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (!expandido) {
                            expandido = true

                            apellidoServidor = ""
                            aprobadoServidor = ""
                            errorServidor = ""
                            cargandoServidor = true

                            coroutineScope.launch {
                                try {
                                    apellidoServidor = consultarApellidoServidor(alumno.nombre)
                                    aprobadoServidor = consultarAprobadoServidor(alumno.nombre)
                                } catch (e: Exception) {
                                    errorServidor = "No se pudo conectar con el servidor."
                                } finally {
                                    cargandoServidor = false
                                }
                            }
                        } else {
                            expandido = false
                            detenerAudio()
                        }
                    }
                ) {
                    Text(if (expandido) "Menos" else "Más")
                }
            }

            if (expandido) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Email: $emailTexto")

                Text(
                    text = "Estado: ${if (invalido) "INVÁLIDO" else "OK"}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = cargandoServidor) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "Consultando servidor HTTP...",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (apellidoServidor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Apellido servidor: $apellidoServidor")
                }

                if (aprobadoServidor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Aprobado servidor: $aprobadoServidor")
                }

                if (errorServidor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Error servidor: $errorServidor")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Audio multimedia:",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("Fuente actual: recurso local en res/raw")
                Text("Estado del audio: ${textoEstadoAudio(estadoAudio)}")
                Text(
                    text = "Tiempo: ${formatearTiempoAudio(posicionAudioMs)} / " +
                            formatearTiempoAudio(duracionAudioMs)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { reproducirAudio() },
                        enabled = audioActivado,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reproducir")
                    }

                    Button(
                        onClick = { pausarAudio() },
                        enabled = audioActivado && estadoAudio == EstadoAudio.REPRODUCIENDO,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pausar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { detenerAudio() },
                    enabled = audioActivado &&
                            (estadoAudio == EstadoAudio.REPRODUCIENDO ||
                                    estadoAudio == EstadoAudio.PAUSADO ||
                                    estadoAudio == EstadoAudio.FINALIZADO),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Detener")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onVerVideoAlumno(alumno)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver vídeo de presentación")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onBorrarAlumno(alumno)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Borrar")
                    }

                    Button(
                        onClick = {
                            onEditarAlumno(alumno)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}

// --------------------------------------------------
// VALIDACIONES
// --------------------------------------------------
fun esAlumnoInvalido(alumno: Alumno): Boolean {
    if (alumno.edad < 0) return true
    if (alumno.email != null && !validarEmail(alumno.email)) return true
    return false
}