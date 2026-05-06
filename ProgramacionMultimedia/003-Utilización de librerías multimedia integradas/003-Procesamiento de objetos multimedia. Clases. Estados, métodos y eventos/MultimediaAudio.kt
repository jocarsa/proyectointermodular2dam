package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.io.File

// --------------------------------------------------
// AUDIO MULTIMEDIA
// --------------------------------------------------

// Devuelve el audio largo de demostración.
// Usa un único archivo para todos los alumnos.
fun audioPresentacionDemo(): Int {
    return R.raw.audio_largo
}

// Estados sencillos para explicar el ciclo de vida del audio largo.
enum class EstadoAudio {
    PREPARADO,
    REPRODUCIENDO,
    PAUSADO,
    DETENIDO,
    FINALIZADO
}

fun textoEstadoAudio(estado: EstadoAudio): String {
    return when (estado) {
        EstadoAudio.PREPARADO -> "Preparado"
        EstadoAudio.REPRODUCIENDO -> "Reproduciendo"
        EstadoAudio.PAUSADO -> "Pausado"
        EstadoAudio.DETENIDO -> "Detenido"
        EstadoAudio.FINALIZADO -> "Finalizado"
    }
}

fun formatearTiempoAudio(milisegundos: Int): String {
    val totalSegundos = milisegundos / 1000
    val minutos = totalSegundos / 60
    val segundos = totalSegundos % 60

    return "%02d:%02d".format(minutos, segundos)
}

class ReproductorAudioLargo(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun reproducir(sonidoResId: Int) {
        detener()

        mediaPlayer = MediaPlayer.create(context, sonidoResId)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
        }
    }

    fun detener() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun liberar() {
        detener()
    }
}

class ReproductorEfectos(context: Context) {

    private val atributosAudio = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(atributosAudio)
        .build()

    private val sonidoAciertoId = soundPool.load(context, R.raw.sonido_acierto, 1)
    private val sonidoErrorId = soundPool.load(context, R.raw.sonido_error, 1)

    fun reproducirAcierto() {
        soundPool.play(sonidoAciertoId, 1f, 1f, 1, 0, 1f)
    }

    fun reproducirError() {
        soundPool.play(sonidoErrorId, 1f, 1f, 1, 0, 1f)
    }

    fun liberar() {
        soundPool.release()
    }
}

// --------------------------------------------------
// ARCHIVOS DE AUDIO POR ALUMNO
// --------------------------------------------------
fun rutaAudioPresentacionAlumno(context: Context, alumnoId: Int): String {
    return File(
        context.filesDir,
        "audio_presentacion_alumno_$alumnoId.m4a"
    ).absolutePath
}

fun rutaAudioTemporalAlumno(context: Context, alumnoId: Int): String {
    return File(
        context.filesDir,
        "audio_tmp_alumno_$alumnoId.m4a"
    ).absolutePath
}

fun existeAudioPresentacionAlumno(context: Context, alumnoId: Int): Boolean {
    return File(rutaAudioPresentacionAlumno(context, alumnoId)).exists()
}