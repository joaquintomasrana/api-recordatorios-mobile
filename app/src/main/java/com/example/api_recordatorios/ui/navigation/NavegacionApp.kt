package com.example.api_recordatorios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.ui.screens.PantallaRecordatorios
import androidx.compose.material3.Text

@Composable
fun NavegacionApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "listado",
        modifier = modifier
    ) {
        composable(route = "listado") {
            PantallaRecordatorios(
                recordatorios = DatosSimulados.recordatorios,
                onRecordatorioClick = { id ->
                    navController.navigate("detalle/$id")
                }
            )
        }
        composable(route = "detalle/{id}") {
            Text(text = "Acá mostraremos el detalle")
        }
    }
}