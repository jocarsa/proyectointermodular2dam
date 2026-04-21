package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// --------------------------------------------------
// FUNCIONES VISUALES DE ALUMNOS
// --------------------------------------------------
fun avatarParaAlumno(alumno: Alumno): Int {
    return when (alumno.id % 3) {
        0 -> R.drawable.avatar1
        1 -> R.drawable.avatar2
        else -> R.drawable.avatar3
    }
}

@Composable
fun IconoEstadoAlumno(alumno: Alumno) {
    val invalido = esAlumnoInvalido(alumno)
    val esMenor = alumno.edad in 0..17

    when {
        invalido -> Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Alumno inválido"
        )

        esMenor -> Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Alumno menor"
        )

        else -> Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Alumno correcto"
        )
    }
}

@Composable
fun AlumnoItem(
    alumno: Alumno,
    onBorrarAlumno: (Alumno) -> Unit,
    onEditarAlumno: (Alumno) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    val categoria = etiquetaEdad(alumno.edad)
    val emailTexto = alumno.email ?: "sin email"
    val invalido = esAlumnoInvalido(alumno)
    val avatarRes = avatarParaAlumno(alumno)

    val infiniteTransition = rememberInfiniteTransition(label = "transicionNombre")

    val colorNombre by if (invalido) {
        infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.error,
            targetValue = MaterialTheme.colorScheme.onSurface,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "colorNombreInvalido"
        )
    } else {
        rememberUpdatedState(MaterialTheme.colorScheme.primary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Avatar de ${alumno.nombre}",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_student),
                            contentDescription = "Icono alumno"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${alumno.nombre} (id=${alumno.id})",
                            style = MaterialTheme.typography.titleMedium,
                            color = colorNombre
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconoEstadoAlumno(alumno)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Edad: ${alumno.edad} -> $categoria"
                    )

                    if (expandido) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Email: $emailTexto")

                        Text(
                            text = "Estado: ${if (invalido) "INVÁLIDO" else "OK"}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onBorrarAlumno(alumno)
                                },
                                modifier = Modifier.weight(1.05f)
                            ) {
                                Text("Borrar")
                            }

                            Button(
                                onClick = {
                                    onEditarAlumno(alumno)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        expandido = !expandido
                    }
                ) {
                    Text(if (expandido) "Menos" else "Más")
                }
            }
        }
    }
}

// --------------------------------------------------
// VALIDACIONES Y SONIDO
// --------------------------------------------------
fun esAlumnoInvalido(alumno: Alumno): Boolean {
    if (alumno.edad < 0) return true
    if (alumno.email != null && !validarEmail(alumno.email)) return true
    return false
}

fun reproducirSonidoSiProcede(
    context: Context,
    audioActivado: Boolean,
    sonidoResId: Int
) {
    if (!audioActivado) {
        return
    }

    reproducirSonido(context, sonidoResId)
}

fun reproducirSonido(context: Context, sonidoResId: Int) {
    val mediaPlayer = MediaPlayer.create(context, sonidoResId)
    mediaPlayer.start()

    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}