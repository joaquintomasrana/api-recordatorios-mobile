package com.example.api_recordatorios.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MensajeListado(
    mensaje: String,
    textoBoton: String? = null,
    onAccion: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    cargando: Boolean = false
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (cargando) CircularProgressIndicator()
        Text(text = mensaje)

        if (textoBoton != null && onAccion != null) {
            Button(onClick = onAccion) {
                Text(text = textoBoton)
            }
        }
    }
}
