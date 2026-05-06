package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun PantallaGrabacionAudioAlumno(
    alumno: Alumno,
    volverAAlumnos: () -> Unit,
    onAceptarPistaGrabada: () -> Unit
) {
    val context = LocalContext.current

    val rutaTemporal = remember(alumno.id) {
        rutaAudioTemporalAlumno(context, alumno.id)
    }

    val rutaFinal = remember(alumno.id) {
        rutaAudioPresentacionAlumno(context, alumno.id)
    }

    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var grabando by remember { mutableStateOf(false) }
    var reproduciendo by remember { mutableStateOf(false) }
    var pistaLista by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    fun liberarRecorder() {
        mediaRecorder?.release()
        mediaRecorder = null
    }

    fun liberarPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        reproduciendo = false
    }

    fun empezarGrabacion() {
        try {
            liberarPlayer()

            File(rutaTemporal).delete()

            val recorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setOutputFile(rutaTemporal)
            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            grabando = true
            pistaLista = false
            mensaje = "Grabando audio..."
        } catch (e: Exception) {
            liberarRecorder()
            grabando = false
            mensaje = "Error al iniciar la grabación."
        }
    }

    fun detenerGrabacion() {
        try {
            mediaRecorder?.stop()
            liberarRecorder()
            grabando = false
            pistaLista = File(rutaTemporal).exists()
            mensaje = if (pistaLista) {
                "Grabación completada."
            } else {
                "No se pudo guardar la grabación."
            }
        } catch (e: Exception) {
            liberarRecorder()
            grabando = false
            pistaLista = false
            mensaje = "Error al detener la grabación."
        }
    }

    fun reproducirPistaTemporal() {
        try {
            liberarPlayer()

            val player = MediaPlayer().apply {
                setDataSource(rutaTemporal)
                prepare()
                setOnCompletionListener {
                    liberarPlayer()
                    mensaje = "Reproducción terminada."
                }
            }

            mediaPlayer = player
            player.start()
            reproduciendo = true
            mensaje = "Reproduciendo pista grabada..."
        } catch (e: Exception) {
            liberarPlayer()
            mensaje = "Error al reproducir la pista grabada."
        }
    }

    fun detenerReproduccion() {
        liberarPlayer()
        mensaje = "Reproducción detenida."
    }

    fun aceptarPista() {
        try {
            val tempFile = File(rutaTemporal)
            val finalFile = File(rutaFinal)

            if (!tempFile.exists()) {
                mensaje = "No hay ninguna pista para aceptar."
                return
            }

            if (finalFile.exists()) {
                finalFile.delete()
            }

            tempFile.copyTo(finalFile, overwrite = true)
            tempFile.delete()

            mensaje = "La pista grabada ya es el audio de presentación del alumno."
            onAceptarPistaGrabada()
        } catch (e: Exception) {
            mensaje = "Error al guardar la pista definitiva."
        }
    }

    val permisoMicroConcedido = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    var tienePermiso by remember { mutableStateOf(permisoMicroConcedido) }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermiso = concedido
        mensaje = if (concedido) {
            "Permiso de micrófono concedido."
        } else {
            "Sin permiso no se puede grabar audio."
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (grabando) {
                    mediaRecorder?.stop()
                }
            } catch (_: Exception) {
            }

            liberarRecorder()
            liberarPlayer()
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
            text = "Grabación de audio",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Alumno: ${alumno.nombre}")

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (tienePermiso) {
                "Permiso de micrófono concedido."
            } else {
                "Hace falta permiso para grabar."
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!tienePermiso) {
            Button(
                onClick = {
                    lanzadorPermiso.launch(Manifest.permission.RECORD_AUDIO)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pedir permiso de micrófono")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { empezarGrabacion() },
            enabled = tienePermiso && !grabando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar grabación")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { detenerGrabacion() },
            enabled = grabando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detener grabación")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { reproducirPistaTemporal() },
            enabled = pistaLista && !grabando && !reproduciendo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reproducir pista grabada")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { detenerReproduccion() },
            enabled = reproduciendo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Detener reproducción")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { aceptarPista() },
            enabled = pistaLista && !grabando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aceptar pista")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                File(rutaTemporal).delete()
                volverAAlumnos()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = mensaje.isNotEmpty()) {
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