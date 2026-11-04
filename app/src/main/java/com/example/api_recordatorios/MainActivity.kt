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
import com.example.api_recordatorios.model.Recordatorio
import com.example.api_recordatorios.ui.components.TarjetaRecordatorio
import com.example.api_recordatorios.ui.screens.PantallaRecordatorios

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApirecordatoriosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRecordatorios(
                        recordatorios = DatosSimulados.recordatorios,
                        onRecordatorioClick = {},
                        modifier = Modifier.padding(innerPadding)
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
        PantallaRecordatorios(
            recordatorios = DatosSimulados.recordatorios,
            onRecordatorioClick = {}
        )
    }
}

