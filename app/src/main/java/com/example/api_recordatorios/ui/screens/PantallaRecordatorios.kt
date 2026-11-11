package com.example.api_recordatorios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.model.Recordatorio
import com.example.api_recordatorios.ui.components.TarjetaRecordatorio
import androidx.compose.material3.Button
@Composable
fun PantallaRecordatorios(
    recordatorios: List<Recordatorio>,
    onRecordatorioClick: (Int) -> Unit,
    onCrearRecordatorio: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onCrearRecordatorio
            ) {
                Text(text = "Crear recordatorio")
            }
        }

        item {
            Text(
                text = "Mis tareas",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(
            items = recordatorios,
            key = { recordatorio -> recordatorio.id }
        ) { recordatorio ->
            TarjetaRecordatorio(
                recordatorio = recordatorio,
                onClick = {
                    onRecordatorioClick(recordatorio.id)
                }
            )

        }
    }
}