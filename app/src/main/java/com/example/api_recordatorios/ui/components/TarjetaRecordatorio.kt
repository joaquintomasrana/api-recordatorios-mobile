package com.example.api_recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.model.Recordatorio

@Composable
fun TarjetaRecordatorio(
    recordatorio: Recordatorio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = recordatorio.titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Categoría: ${recordatorio.categoria.nombre}",
                color = DatosSimulados.coloresPorCategoria[
                    recordatorio.categoria
                ] ?: Color.Gray
            )

            Text(
                text = "Prioridad: ${recordatorio.prioridad.etiqueta}"
            )

            Text(
                text = "Estado: ${recordatorio.estado.etiqueta}"
            )
        }
    }
}