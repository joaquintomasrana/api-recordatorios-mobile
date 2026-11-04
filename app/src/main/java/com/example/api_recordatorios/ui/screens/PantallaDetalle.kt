package com.example.api_recordatorios.ui.screens
import com.example.api_recordatorios.model.Recordatorio
import androidx.compose.ui.tooling.preview.Preview
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.ui.theme.ApirecordatoriosTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PantallaDetalle(
    recordatorio: Recordatorio,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = recordatorio.titulo)

        Text(text = "Categoría: ${recordatorio.categoria.nombre}")

        Text(text = "Prioridad: ${recordatorio.prioridad.etiqueta}")

        Text(text = "Estado: ${recordatorio.estado.etiqueta}")

        Text(
            text = recordatorio.descripcion ?: "Sin descripción"
        )
        Button(
            onClick = onVolver
        ) {
            Text(text = "Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaDetallePreview() {
    ApirecordatoriosTheme {
        PantallaDetalle(
            recordatorio = DatosSimulados.recordatorios.first(),
            onVolver = {}
        )
    }
}