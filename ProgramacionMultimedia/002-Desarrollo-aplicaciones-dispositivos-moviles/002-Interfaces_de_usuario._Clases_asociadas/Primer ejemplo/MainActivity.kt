package com.example.desarrollodeaplicacionesparadispositivosmviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// Cada pantalla es una activity
class MainActivity : ComponentActivity() {

    // onCreate se ejecuta cuando se arranca la aplicación
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaGrupoSimple()
                }
            }
        }
    }
}

// Composable sirve para dibujar la interfaz
@Composable
fun PantallaGrupoSimple() {

    // Compose puede redibujar mucas veces la pantalla. Ponemos remember para que solo se cree una vez la variable
    val grupo = remember { crearGrupoDemo() }

    // Las variables mutableStateOf redibujan la interfaz cada vez que su valor cambia
    var mensaje by remember { mutableStateOf("Pulsa un botón para calcular.") }

    // 3) UI (declarativa)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Interfaces de usuario (Compose)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Grupo: ${grupo.nombre}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val mayores = grupo.contarMayoresDeEdad()
                mensaje = "Mayores de edad: $mayores"
            }
        ) {
            Text("Contar mayores de edad")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Reutilizamos una función de tu lógica:
                val alumno = grupo.alumnos[0]
                val categoria = etiquetaEdad(alumno.edad)
                mensaje = "Ejemplo: ${alumno.nombre} -> $categoria"
            }
        ) {
            Text("Clasificar primer alumno")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val alumno = grupo.alumnos[1]
                val ok = validarEmail(alumno.email)
                mensaje = "Email de ${alumno.nombre}: válido = $ok"
            }
        ) {
            Text("Validar email (ejemplo)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Resultado:")
        Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)
    }
}