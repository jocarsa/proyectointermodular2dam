package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    AppPantallas()
                }
            }
        }
    }
}

// --------------------------------------------------
// DATASTORE
// --------------------------------------------------
private val Context.dataStore by preferencesDataStore(name = "configuracion_app")

private object ClavesDataStore {
    val ULTIMO_FILTRO = stringPreferencesKey("ultimo_filtro")
    val AUDIO_ACTIVADO = booleanPreferencesKey("audio_activado")
}

// --------------------------------------------------
// ESCENAS Y FILTROS
// --------------------------------------------------
enum class Escena {
    ALUMNOS, CONFIGURACION, EDICION, VIDEO
}

enum class Filtro {
    TODOS, MAYORES, INVALIDOS
}

// --------------------------------------------------
// FUNCIONES AUXILIARES PARA DATASTORE
// --------------------------------------------------
private fun textoAFiltro(texto: String?): Filtro {
    return try {
        if (texto == null) {
            Filtro.TODOS
        } else {
            Filtro.valueOf(texto)
        }
    } catch (e: IllegalArgumentException) {
        Filtro.TODOS
    }
}

private suspend fun guardarUltimoFiltro(context: Context, filtro: Filtro) {
    context.dataStore.edit { preferencias ->
        preferencias[ClavesDataStore.ULTIMO_FILTRO] = filtro.name
    }
}

private suspend fun guardarAudioActivado(context: Context, activado: Boolean) {
    context.dataStore.edit { preferencias ->
        preferencias[ClavesDataStore.AUDIO_ACTIVADO] = activado
    }
}

// --------------------------------------------------
// APP PRINCIPAL
// --------------------------------------------------
@Composable
fun AppPantallas() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var escenaActual by remember { mutableStateOf(Escena.ALUMNOS) }

    val grupoBase = remember { crearGrupoDemo() }
    val grupo = remember {
        Grupo(
            nombre = grupoBase.nombre,
            alumnos = emptyArray()
        )
    }

    val crud = remember(context) { AlumnoCRUD(context) }

    val alumnos = remember {
        mutableStateListOf<Alumno>()
    }

    var siguienteId by remember { mutableStateOf(1) }

    var filtroGuardado by remember { mutableStateOf(Filtro.TODOS) }
    var audioActivado by remember { mutableStateOf(true) }

    // Guardamos aquí el alumno que vamos a editar
    var alumnoEnEdicion by remember { mutableStateOf<Alumno?>(null) }

    // Guardamos aquí el alumno cuyo vídeo vamos a ver
    var alumnoEnVideo by remember { mutableStateOf<Alumno?>(null) }

    // Estados del apartado de hilos
    var cargandoInicial by remember { mutableStateOf(true) }
    var recargandoAlumnos by remember { mutableStateOf(false) }

    suspend fun cargarListaDesdeSQLite(simularRetardo: Long, esCargaInicial: Boolean) {
        if (esCargaInicial) {
            cargandoInicial = true
        } else {
            recargandoAlumnos = true
        }

        delay(simularRetardo)

        val listaFinal = crud.consultarTodos()

        alumnos.clear()
        alumnos.addAll(listaFinal)

        siguienteId = (alumnos.maxOfOrNull { it.id } ?: 0) + 1

        if (esCargaInicial) {
            cargandoInicial = false
        } else {
            recargandoAlumnos = false
        }
    }

    // --------------------------------------------------
    // CARGA INICIAL DE SQLITE
    // --------------------------------------------------
    LaunchedEffect(Unit) {
        val listaBD = crud.consultarTodos()

        // Si la base de datos está vacía, insertamos los alumnos demo una sola vez
        if (listaBD.isEmpty()) {
            for (alumnoDemo in grupoBase.alumnos) {
                crud.insertarAlumno(alumnoDemo)
            }
        }

        cargarListaDesdeSQLite(
            simularRetardo = 2000L,
            esCargaInicial = true
        )
    }

    // --------------------------------------------------
    // LECTURA DE DATASTORE
    // --------------------------------------------------
    LaunchedEffect(Unit) {
        try {
            context.dataStore.data.collect { preferencias ->
                val textoFiltro = preferencias[ClavesDataStore.ULTIMO_FILTRO]
                filtroGuardado = if (textoFiltro == null) {
                    Filtro.TODOS
                } else {
                    textoAFiltro(textoFiltro)
                }

                val audioGuardado = preferencias[ClavesDataStore.AUDIO_ACTIVADO]
                audioActivado = if (audioGuardado == null) {
                    true
                } else {
                    audioGuardado
                }
            }
        } catch (error: IOException) {
            filtroGuardado = Filtro.TODOS
            audioActivado = true
        }
    }

    when (escenaActual) {
        Escena.ALUMNOS -> {
            if (cargandoInicial) {
                PantallaCargaInicial()
            } else {
                PantallaGrupoCompleta(
                    grupo = grupo,
                    alumnos = alumnos,
                    siguienteId = siguienteId,
                    onSiguienteIdChange = { nuevoId ->
                        siguienteId = nuevoId
                    },
                    filtroActual = filtroGuardado,
                    onFiltroChange = { nuevoFiltro ->
                        filtroGuardado = nuevoFiltro

                        coroutineScope.launch {
                            guardarUltimoFiltro(context, nuevoFiltro)
                        }
                    },
                    audioActivado = audioActivado,
                    recargandoAlumnos = recargandoAlumnos,
                    onRecargarAlumnos = {
                        if (!recargandoAlumnos) {
                            coroutineScope.launch {
                                cargarListaDesdeSQLite(
                                    simularRetardo = 2000L,
                                    esCargaInicial = false
                                )
                            }
                        }
                    },
                    onInsertarAlumno = { nuevoAlumno ->
                        val resultado = crud.insertarAlumno(nuevoAlumno)

                        if (resultado != -1L) {
                            alumnos.add(nuevoAlumno)
                            siguienteId = nuevoAlumno.id + 1
                            true
                        } else {
                            false
                        }
                    },
                    onBorrarAlumno = { alumnoABorrar ->
                        val filasBorradas = crud.borrarAlumno(alumnoABorrar.id)

                        if (filasBorradas > 0) {
                            val indice = alumnos.indexOfFirst { it.id == alumnoABorrar.id }
                            if (indice != -1) {
                                alumnos.removeAt(indice)
                            }
                            true
                        } else {
                            false
                        }
                    },
                    onIrAEditarAlumno = { alumnoAEditar ->
                        alumnoEnEdicion = alumnoAEditar
                        escenaActual = Escena.EDICION
                    },
                    onIrAVideoAlumno = { alumnoVideo ->
                        alumnoEnVideo = alumnoVideo
                        escenaActual = Escena.VIDEO
                    },
                    irAConfiguracion = {
                        escenaActual = Escena.CONFIGURACION
                    }
                )
            }
        }

        Escena.CONFIGURACION -> {
            PantallaConfiguracion(
                audioActivado = audioActivado,
                onAudioActivadoChange = { nuevoValor ->
                    audioActivado = nuevoValor

                    coroutineScope.launch {
                        guardarAudioActivado(context, nuevoValor)
                    }
                },
                volverAAlumnos = {
                    escenaActual = Escena.ALUMNOS
                }
            )
        }

        Escena.EDICION -> {
            val alumnoEditar = alumnoEnEdicion

            if (alumnoEditar != null) {
                PantallaEdicionAlumno(
                    alumno = alumnoEditar,
                    audioActivado = audioActivado,
                    onActualizarAlumno = { alumnoActualizado ->
                        val filasActualizadas = crud.actualizarAlumno(alumnoActualizado)

                        if (filasActualizadas > 0) {
                            val indice = alumnos.indexOfFirst { it.id == alumnoActualizado.id }

                            if (indice != -1) {
                                alumnos[indice] = alumnoActualizado
                            }

                            alumnoEnEdicion = null
                            escenaActual = Escena.ALUMNOS
                            true
                        } else {
                            false
                        }
                    },
                    volverAAlumnos = {
                        alumnoEnEdicion = null
                        escenaActual = Escena.ALUMNOS
                    }
                )
            } else {
                escenaActual = Escena.ALUMNOS
            }
        }

        Escena.VIDEO -> {
            val alumnoVideo = alumnoEnVideo

            if (alumnoVideo != null) {
                PantallaVideoAlumno(
                    alumno = alumnoVideo,
                    volverAAlumnos = {
                        alumnoEnVideo = null
                        escenaActual = Escena.ALUMNOS
                    }
                )
            } else {
                escenaActual = Escena.ALUMNOS
            }
        }
    }
}