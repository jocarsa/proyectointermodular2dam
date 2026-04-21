package com.example.desarrollodeaplicacionesparadispositivosmviles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// --------------------------------------------------
// PANTALLA DE CONFIGURACIÓN
// --------------------------------------------------
@Composable
fun PantallaConfiguracion(
    audioActivado: Boolean,
    onAudioActivadoChange: (Boolean) -> Unit,
    volverAAlumnos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Desde aquí podemos guardar ajustes sencillos usando DataStore."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = audioActivado,
                    onCheckedChange = { marcado ->
                        onAudioActivadoChange(marcado)
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Reproducir audio",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (audioActivado) {
                            "Actualmente el audio está activado."
                        } else {
                            "Actualmente el audio está desactivado."
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = volverAAlumnos) {
            Text("Volver a alumnos")
        }
    }
}