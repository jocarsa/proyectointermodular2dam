package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlumnoCRUD(context: Context) :
    SQLiteOpenHelper(context, "alumnos.db", null, 1) {

    // Guarda las referencias a la tabla y sus columnas
    // No se duplica entre instancias de la misma clase
    companion object {
        private const val NOMBRE_TABLA = "alumno"
        private const val COLUMNA_ID = "id"
        private const val COLUMNA_NOMBRE = "nombre"
        private const val COLUMNA_EDAD = "edad"
        private const val COLUMNA_EMAIL = "email"
    }

    // Se ejecuta cuando se crea la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        val sqlCrearTabla = """
            CREATE TABLE $NOMBRE_TABLA (
                $COLUMNA_ID INTEGER PRIMARY KEY,
                $COLUMNA_NOMBRE TEXT NOT NULL,
                $COLUMNA_EDAD INTEGER NOT NULL,
                $COLUMNA_EMAIL TEXT
            )
        """.trimIndent()

        // Ejecutamos la creación de la tabla
        db.execSQL(sqlCrearTabla)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // De momento no hacemos nada aquí porque estamos en la versión 1
        // Si cambiáramos la versión añadiríamos los cambios aquí
        // La función tiene que existir, aunque sea vacía
    }

    fun insertarAlumno(alumno: Alumno): Long {
        // Abrimos la base de datos en modo escritura
        val db = writableDatabase

        // Aquí asociamos a cada columna su valor en la fila que vamos a crear
        val valores = ContentValues().apply {
            put(COLUMNA_ID, alumno.id)
            put(COLUMNA_NOMBRE, alumno.nombre)
            put(COLUMNA_EDAD, alumno.edad)

            if (alumno.email == null) {
                // Para poner un dato a null (email podía serlo)
                putNull(COLUMNA_EMAIL)
            } else {
                put(COLUMNA_EMAIL, alumno.email)
            }
        }

        // Insertamos la fila y cerramos la base
        // En resultado guardamos el id de la fila guardada o -1 si ha habido algún error
        val resultado = db.insert(NOMBRE_TABLA, null, valores)
        db.close()

        return resultado
    }

    fun consultarTodos(): List<Alumno> {
        val lista = mutableListOf<Alumno>()
        // Abre la base de datos en modo lectura
        val db = readableDatabase

        val sqlConsulta = """
            SELECT $COLUMNA_ID, $COLUMNA_NOMBRE, $COLUMNA_EDAD, $COLUMNA_EMAIL
            FROM $NOMBRE_TABLA
            ORDER BY $COLUMNA_ID
        """.trimIndent()

        // Cursor recibe la respuesta de la base de datos
        val cursor = db.rawQuery(sqlConsulta, null)

        // Avanzamos fila a fila por la respuesta de la base de datos
        if (cursor.moveToFirst()) {
            do {
                // Accedemos a cada columna por índice
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val edad = cursor.getInt(2)

                val email = if (cursor.isNull(3)) {
                    null
                } else {
                    cursor.getString(3)
                }

                // Construimos un alumno a partir de los datos devueltos y lo insertamos en la lista del return
                val alumno = Alumno(
                    id = id,
                    nombre = nombre,
                    edad = edad,
                    email = email
                )

                lista.add(alumno)

            } while (cursor.moveToNext())
        }

        // Cerramos el cursor y la base de datos
        cursor.close()
        db.close()

        return lista
    }

    fun borrarAlumno(idAlumno: Int): Int {
        // Abrimos la base de datos en modo escritura
        val db = writableDatabase

        // Borramos la fila cuyo id coincida con el recibido
        val filasBorradas = db.delete(
            NOMBRE_TABLA,
            "$COLUMNA_ID = ?",
            arrayOf(idAlumno.toString())
        )

        db.close()

        return filasBorradas
    }

    fun actualizarAlumno(alumno: Alumno): Int {
        // Abrimos la base de datos en modo escritura
        val db = writableDatabase

        // Preparamos los nuevos valores que sustituirán a los anteriores
        val valores = ContentValues().apply {
            put(COLUMNA_NOMBRE, alumno.nombre)
            put(COLUMNA_EDAD, alumno.edad)

            if (alumno.email == null) {
                putNull(COLUMNA_EMAIL)
            } else {
                put(COLUMNA_EMAIL, alumno.email)
            }
        }

        // Actualizamos la fila cuyo id coincida con el del alumno recibido
        val filasActualizadas = db.update(
            NOMBRE_TABLA,
            valores,
            "$COLUMNA_ID = ?",
            arrayOf(alumno.id.toString())
        )

        db.close()

        return filasActualizadas
    }
}