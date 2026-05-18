package com.example.desarrollodeaplicacionesparadispositivosmviles

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Cambia esta URL si metes el php en una carpeta dentro de htdocs
// 10.0.2.2 es una ip especial porque usar localhost apuntaría al propio emulador
private const val URL_SERVIDOR_ALUMNOS = "http://192.168.1.25/ServidorAndroid/alumnos.php"

// --------------------------------------------------
// PETICIÓN GET
// --------------------------------------------------
suspend fun consultarApellidoServidor(nombre: String): String {
    val nombreCodificado = URLEncoder.encode(nombre, "UTF-8")
    val rutaCompleta =
        "$URL_SERVIDOR_ALUMNOS?peticion=apellido&nombre=$nombreCodificado"

    val json = hacerPeticionGet(rutaCompleta)
    return json.optString("apellido", "apellido desconocido")
}

// --------------------------------------------------
// PETICIÓN POST
// --------------------------------------------------
suspend fun consultarAprobadoServidor(nombre: String): String {
    val cuerpoPost = construirCuerpoPostAprobado(nombre)

    val json = hacerPeticionPost(
        urlDestino = URL_SERVIDOR_ALUMNOS,
        cuerpoPost = cuerpoPost
    )

    return json.optString("aprobado", "desconocido")
}

private suspend fun hacerPeticionGet(rutaCompleta: String): JSONObject =
    withContext(Dispatchers.IO) {
        val url = URL(rutaCompleta)
        val conexion = url.openConnection() as HttpURLConnection

        try {
            conexion.requestMethod = "GET"
            conexion.connectTimeout = 5000
            conexion.readTimeout = 5000

            val codigoRespuesta = conexion.responseCode

            if (codigoRespuesta != HttpURLConnection.HTTP_OK) {
                throw Exception("Error HTTP GET: $codigoRespuesta")
            }

            val lector = BufferedReader(InputStreamReader(conexion.inputStream))
            val respuesta = lector.use { it.readText() }

            JSONObject(respuesta)
        } finally {
            conexion.disconnect()
        }
    }

private suspend fun hacerPeticionPost(
    urlDestino: String,
    cuerpoPost: String
): JSONObject = withContext(Dispatchers.IO) {
    val url = URL(urlDestino)
    val conexion = url.openConnection() as HttpURLConnection

    try {
        conexion.requestMethod = "POST"
        conexion.connectTimeout = 5000
        conexion.readTimeout = 5000
        conexion.doOutput = true
        conexion.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )

        val escritor = OutputStreamWriter(conexion.outputStream)
        escritor.use {
            it.write(cuerpoPost)
            it.flush()
        }

        val codigoRespuesta = conexion.responseCode

        if (codigoRespuesta != HttpURLConnection.HTTP_OK) {
            throw Exception("Error HTTP POST: $codigoRespuesta")
        }

        val lector = BufferedReader(InputStreamReader(conexion.inputStream))
        val respuesta = lector.use { it.readText() }

        JSONObject(respuesta)
    } finally {
        conexion.disconnect()
    }
}

private fun construirCuerpoPostAprobado(nombre: String): String {
    val peticionCodificada = URLEncoder.encode("aprobado", "UTF-8")
    val nombreCodificado = URLEncoder.encode(nombre, "UTF-8")

    return "peticion=$peticionCodificada&nombre=$nombreCodificado"
}