package com.example.desarrollodeaplicacionesparadispositivosmviles

// --------------------------------------------------
// VÍDEO MULTIMEDIA
// --------------------------------------------------

// Vídeo de demostración.
// De momento usamos el mismo recurso para todos los alumnos.
fun videoPresentacionDemo(): Int {
    return R.raw.video_presentacion
}

// Estados sencillos para explicar el ciclo de vida del vídeo.
enum class EstadoVideo {
    PREPARADO,
    REPRODUCIENDO,
    PAUSADO,
    DETENIDO,
    FINALIZADO
}

fun textoEstadoVideo(estado: EstadoVideo): String {
    return when (estado) {
        EstadoVideo.PREPARADO -> "Preparado"
        EstadoVideo.REPRODUCIENDO -> "Reproduciendo"
        EstadoVideo.PAUSADO -> "Pausado"
        EstadoVideo.DETENIDO -> "Detenido"
        EstadoVideo.FINALIZADO -> "Finalizado"
    }
}

fun formatearTiempoVideo(milisegundos: Int): String {
    val totalSegundos = milisegundos / 1000
    val minutos = totalSegundos / 60
    val segundos = totalSegundos % 60

    return "%02d:%02d".format(minutos, segundos)
}