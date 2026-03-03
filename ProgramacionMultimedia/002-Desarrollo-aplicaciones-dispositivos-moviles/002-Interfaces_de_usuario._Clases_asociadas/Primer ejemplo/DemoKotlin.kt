package com.example.desarrollodeaplicacionesparadispositivosmviles

// data class (para guardar datos, como si fuera un JSON)
data class Alumno(
    val id: Int,
    val nombre: String,
    val edad: Int,
    val email: String? = null // null-safety
)

// class normal
// Los constructores van en la cabecera
class Grupo(val nombre: String, val alumnos: Array<Alumno>) {

    fun contarMayoresDeEdad(): Int {

        var contador = 0

        for (alumno in alumnos) {
            if (alumno.edad >= 18) {
                contador++
            }
        }

        return contador
    }
}

// Ejemplo: $variable y ${expresion}
// Lo usaremos para generar mensajes legibles.
fun formatearAlumno(alumno: Alumno): String {
    val edadDoble = alumno.edad * 2
    // $alumno.nombre usa una variable
    // ${edadDoble} usa una expresión
    return "Alumno: ${alumno.nombre} | edad=${alumno.edad} | edad*2=${edadDoble}"
}

// Si no te pasan saludo, usa "Hola".
// Si no te pasan puntuacion, usa 0.
// Funciona igual que en JavaScript
// Esto es muy útil para no tener múltiples sobrecargas como en Java.
fun saludar(nombre: String, saludo: String = "Hola", puntuacion: Int = 0): String {
    return "$saludo, $nombre. Puntuación=${puntuacion}"
}

// funciones top-level
fun etiquetaEdad(edad: Int): String {
    // when es como un switch, pero no solamente compara una variable con una serie de datos
    // permite comparar multiples condiciones booleanas distintas
    return when {
        edad < 0 -> "Edad inválida"
        edad < 18 -> "Menor"
        edad in 18..64 -> "Adulto"
        else -> "Senior"
    }
}

// String? permite que el valor de email sea null
fun validarEmail(email: String?): Boolean {

    if (email == null) {
        return false
    }

    if (!email.contains("@")) {
        return false
    }

    if (!email.contains(".")) {
        return false
    }

    return true
}

/**
 * Esta función devuelve un texto con el resultado.
 * La llamaremos desde MainActivity para mostrarlo en pantalla.
 */
fun ejecutarDemoKotlin(): String {
    /*
    * Kotlin tiene inferencia de tipos,
    * por lo que el compilador deduce de manera dinámica el tipo de la var o val (como en JavaScript)
    *
    * Los tipos de datos son fijos y se pueden especificar en su creación. (como en Java)
    */
    // val no permite modificar posteriormente su valor (constante)
    val titulo = "Demo Kotlin en Android"
    // var si permite modificar su valor después de declararse
    var totalProcesados = 0

    val alumnos = arrayOf(
        // Esto hace el código mucho más legible.
        Alumno(id = 1, nombre = "Joshua", edad = 17, email = "Joshua@correo.com"),
        Alumno(id = 2, nombre = "Marcos", edad = 18, email = null),
        Alumno(id = 3, nombre = "Angel", edad = 22, email = "Angel@correo.com"),
        Alumno(id = 4, nombre = "Sidik", edad = 65, email = "Sidik@correo.com"),
        Alumno(id = 5, nombre = "Jorge", edad = -3, email = "Jorge@mal") // edad inválida, email inválido
    )

    val grupo = Grupo(nombre = "2º DAM", alumnos = alumnos)

    // if como expresión
    val mensajeGrupo = if (grupo.contarMayoresDeEdad() > 0) {
        "Hay mayores de edad en el grupo."
    } else {
        "No hay mayores de edad en el grupo."
    }

    // Construimos salida como un String que luego mostramos por pantalla en el MainActivity
    // Podríamos crearnos un String normal y hacerle += todo el rato
    return buildString {

        appendLine("=== $titulo ===")
        appendLine("Grupo: ${grupo.nombre}")
        appendLine(mensajeGrupo)
        appendLine()


        appendLine("--- Ejemplos de Strings y templates ---")
        // $variable
        val nombreGrupo = grupo.nombre
        appendLine("Nombre del grupo (con \$variable): $nombreGrupo")

        // ${expresion}
        val totalAlumnos = alumnos.size
        appendLine("Total alumnos (con \${expresion}): ${totalAlumnos * 2} (doble del tamaño)")

        // Uso de la función que formatea usando templates
        appendLine(formatearAlumno(alumnos[0]))
        appendLine()

        appendLine("--- Ejemplos de parámetros por defecto ---")
        // Llamada usando valores por defecto (saludo="Hola", puntuacion=0)
        appendLine(saludar(nombre = alumnos[0].nombre))
        // Llamada pasando solo uno de los opcionales
        appendLine(saludar(nombre = alumnos[1].nombre, saludo = "Buenas"))
        // Llamada pasando todos
        appendLine(saludar(nombre = alumnos[2].nombre, saludo = "Ey", puntuacion = 10))
        appendLine()

        appendLine("--- Mayores de edad ---")

        alumnos.forEach { alumno ->

            if (alumno.edad >= 18) {

                totalProcesados++

                val categoria = etiquetaEdad(alumno.edad)
                val longitudNombre = alumno.nombre.length

                /*
                * Si email es null no hace nada
                * substringAfter() devuelve el contenido que hay en un string después de un determinado carácter
                * si el resultado del substring es null, dominio = "sin-email"
                */
                val dominio = alumno.email?.substringAfter("@") ?: "sin-email"
                val emailOk = validarEmail(alumno.email)

                appendLine(
                    "• ${alumno.nombre} (id=${alumno.id}) -> $categoria | " +
                            "nombreLen=$longitudNombre | dominio=$dominio | emailValido=$emailOk"
                )
            }
        }

        appendLine()
        appendLine("Total procesados: $totalProcesados")

        // Nombres
        // Recorremos todos los alumnos por su índice
        append("Nombres: ")
        for (i in alumnos.indices) {
            append(alumnos[i].nombre)
            if (i < alumnos.size - 1) {
                append(", ")
            }
        }
        appendLine()
        appendLine("Prueba de Scroll")
        appendLine()
        // Bucle que se ejecuta un número específico de veces
        // it es el contador interno del bucle
        repeat(50) { appendLine("Línea extra ${it + 1}") }
    }
}

fun crearGrupoDemo(): Grupo {
    val alumnos = arrayOf(
        Alumno(id = 1, nombre = "Joshua", edad = 17, email = "Joshua@correo.com"),
        Alumno(id = 2, nombre = "Marcos", edad = 18, email = null),
        Alumno(id = 3, nombre = "Angel", edad = 22, email = "Angel@correo.com"),
        Alumno(id = 4, nombre = "Sidik", edad = 65, email = "Sidik@correo.com"),
        Alumno(id = 5, nombre = "Jorge", edad = -3, email = "Jorge@mal")
    )
    return Grupo(nombre = "2º DAM", alumnos = alumnos)
}