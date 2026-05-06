package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun PantallaVideoAlumno(
    alumno: Alumno,
    volverAAlumnos: () -> Unit
) {
    val context = LocalContext.current

    val rutaVideo = remember {
        Uri.parse("android.resource://${context.packageName}/${videoPresentacionDemo()}")
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(rutaVideo)
            setMediaItem(mediaItem)
            prepare()
            pause()
        }
    }

    var estadoVideo by remember { mutableStateOf(EstadoVideo.PREPARADO) }
    var posicionVideoMs by remember { mutableIntStateOf(0) }
    var duracionVideoMs by remember { mutableIntStateOf(0) }

    // Esta marca nos ayuda a distinguir un "detener" manual de una pausa normal.
    var detenidoManual by remember { mutableStateOf(false) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {

            // Reacciona a cambios de estado en el video
            override fun onPlaybackStateChanged(playbackState: Int) {
                val duracionActual = exoPlayer.duration
                // Si el tiempo es indefinido o mayor a 0
                if (duracionActual != C.TIME_UNSET && duracionActual >= 0) {
                    duracionVideoMs = duracionActual.toInt()
                }

                if (playbackState == Player.STATE_ENDED) {
                    estadoVideo = EstadoVideo.FINALIZADO
                    posicionVideoMs = duracionVideoMs
                    detenidoManual = false
                }

                if (playbackState == Player.STATE_READY &&
                    !exoPlayer.isPlaying &&
                    !detenidoManual &&
                    exoPlayer.currentPosition == 0L
                ) {
                    estadoVideo = EstadoVideo.PREPARADO
                }
            }

            // Reacciona a si el video se está reproduciendo o no
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    estadoVideo = EstadoVideo.REPRODUCIENDO
                    detenidoManual = false
                } else {
                    if (exoPlayer.playbackState == Player.STATE_READY) {
                        if (detenidoManual) {
                            estadoVideo = EstadoVideo.DETENIDO
                        } else if (exoPlayer.currentPosition > 0L) {
                            estadoVideo = EstadoVideo.PAUSADO
                        } else {
                            estadoVideo = EstadoVideo.PREPARADO
                        }
                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            val posicionActual = exoPlayer.currentPosition
            val duracionActual = exoPlayer.duration

            if (posicionActual >= 0) {
                posicionVideoMs = posicionActual.toInt()
            }

            if (duracionActual != C.TIME_UNSET && duracionActual >= 0) {
                duracionVideoMs = duracionActual.toInt()
            }

            delay(200)
        }
    }

    fun reproducirVideo() {
        if (estadoVideo == EstadoVideo.FINALIZADO || estadoVideo == EstadoVideo.DETENIDO) {
            exoPlayer.seekTo(0)
            posicionVideoMs = 0
        }

        detenidoManual = false
        exoPlayer.play()
        estadoVideo = EstadoVideo.REPRODUCIENDO
    }

    fun pausarVideo() {
        exoPlayer.pause()
        posicionVideoMs = exoPlayer.currentPosition.toInt()
        estadoVideo = EstadoVideo.PAUSADO
    }

    fun detenerVideo() {
        exoPlayer.pause()
        exoPlayer.seekTo(0)
        posicionVideoMs = 0
        detenidoManual = true
        estadoVideo = EstadoVideo.DETENIDO
    }

    fun retrasarVideo5Segundos() {
        val nuevaPosicion = (exoPlayer.currentPosition - 5000L).coerceAtLeast(0L)
        exoPlayer.seekTo(nuevaPosicion)
        posicionVideoMs = nuevaPosicion.toInt()
    }

    fun adelantarVideo5Segundos() {
        val duracionReferencia =
            if (exoPlayer.duration != C.TIME_UNSET && exoPlayer.duration >= 0) {
                exoPlayer.duration
            } else {
                duracionVideoMs.toLong()
            }

        val nuevaPosicion = (exoPlayer.currentPosition + 5000L)
            .coerceAtMost(duracionReferencia)

        exoPlayer.seekTo(nuevaPosicion)
        posicionVideoMs = nuevaPosicion.toInt()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Vídeo de presentación",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Alumno: ${alumno.nombre}")

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { contexto ->
                PlayerView(contexto).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            update = { vista ->
                vista.player = exoPlayer
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Fuente actual: recurso local en res/raw")
                Text("Estado del vídeo: ${textoEstadoVideo(estadoVideo)}")
                Text(
                    text = "Tiempo: ${formatearTiempoVideo(posicionVideoMs)} / " +
                            formatearTiempoVideo(duracionVideoMs)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { reproducirVideo() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { pausarVideo() },
            enabled = estadoVideo == EstadoVideo.REPRODUCIENDO,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pausar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { detenerVideo() },
            enabled = estadoVideo == EstadoVideo.REPRODUCIENDO ||
                    estadoVideo == EstadoVideo.PAUSADO ||
                    estadoVideo == EstadoVideo.FINALIZADO,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detener")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { retrasarVideo5Segundos() },
                modifier = Modifier.weight(1f)
            ) {
                Text("-5 s")
            }

            Button(
                onClick = { adelantarVideo5Segundos() },
                modifier = Modifier.weight(1f)
            ) {
                Text("+5 s")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                exoPlayer.pause()
                volverAAlumnos()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}