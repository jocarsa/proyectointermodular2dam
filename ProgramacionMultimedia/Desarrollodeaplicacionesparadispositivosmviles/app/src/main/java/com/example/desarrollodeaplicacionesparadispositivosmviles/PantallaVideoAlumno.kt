package com.example.desarrollodeaplicacionesparadispositivosmviles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.material3.Player

fun videoPresentacionDemo(): Int {
    return R.raw.video_presentacion
}

@Composable
fun PantallaVideoAlumno(
    alumno: Alumno,
    volverAAlumnos: () -> Unit
) {
    val context = LocalContext.current

    val rutaVideo = remember {
        "android.resource://${context.packageName}/${videoPresentacionDemo()}"
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(rutaVideo)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
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

        Player(
            player = exoPlayer,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                exoPlayer.seekTo(0)
                exoPlayer.play()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir vídeo")
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