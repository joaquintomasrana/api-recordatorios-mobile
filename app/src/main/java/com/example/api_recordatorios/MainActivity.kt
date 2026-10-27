package com.example.api_recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.api_recordatorios.ui.theme.ApirecordatoriosTheme
import androidx.compose.ui.unit.dp
import com.example.api_recordatorios.data.DatosSimulados
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApirecordatoriosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRecordatorios(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Mis tareas",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        items(
            items = DatosSimulados.recordatorios,
            key = { recordatorio -> recordatorio.id }
        ) { recordatorio ->
            Card(
                modifier = Modifier.fillMaxWidth()
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
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApirecordatoriosTheme {
        PantallaRecordatorios()
    }
}