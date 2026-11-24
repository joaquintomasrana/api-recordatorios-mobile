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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import java.text.DateFormat
import java.util.Date

@Composable
fun PantallaDetalle(
    recordatorio: Recordatorio,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    onEditar: () -> Unit = {},
    onCambiarEstado: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = recordatorio.titulo)

        Text(text = "Categoría: ${recordatorio.categoria.nombre}")

        Text(text = "Prioridad: ${recordatorio.prioridad.etiqueta}")

        Text(text = "Estado: ${recordatorio.estado.etiqueta}")
        Text(text = "Fecha: ${DateFormat.getDateTimeInstance().format(Date(recordatorio.fecha))}")
        Text(text = recordatorio.sincronizacion)

        Text(
            text = recordatorio.descripcion ?: "Sin descripción"
        )
        Button(
            onClick = onVolver
        ) {
            Text(text = "Volver")
        }
        Button(onClick = onEditar) { Text("Editar") }
        Button(onClick = onCambiarEstado) { Text("Cambiar estado") }
        Button(onClick = onEliminar) { Text("Eliminar") }
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
