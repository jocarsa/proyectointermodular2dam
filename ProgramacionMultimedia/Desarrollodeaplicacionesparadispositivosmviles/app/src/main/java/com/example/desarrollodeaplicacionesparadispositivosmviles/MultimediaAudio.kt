package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool

// --------------------------------------------------
// AUDIO MULTIMEDIA
// --------------------------------------------------

// Devuelve el audio largo de demostración.
// Usa un único archivo para todos los alumnos.
fun audioPresentacionDemo(): Int {
    return R.raw.audio_presentacion
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