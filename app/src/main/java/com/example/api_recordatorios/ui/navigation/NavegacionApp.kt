package com.example.api_recordatorios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.ui.screens.PantallaRecordatorios
import androidx.compose.material3.Text
import com.example.api_recordatorios.ui.screens.PantallaDetalle
import com.example.api_recordatorios.ui.screens.PantallaCrear
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.api_recordatorios.model.Recordatorio


@Composable
fun NavegacionApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val recordatorios = remember { mutableStateOf(DatosSimulados.recordatorios) }

    NavHost(
        navController = navController,
        startDestination = "listado",
        modifier = modifier
    ) {
        composable(route = "listado") {
            PantallaRecordatorios(
                recordatorios = recordatorios.value,
                onRecordatorioClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onCrearRecordatorio = {
                    navController.navigate("crear")
                }
            )
        }

        composable(route = "crear") {
            PantallaCrear(
                onCancelar = {
                    navController.popBackStack()
                },
                onGuardar = { titulo, descripcion, categoria, prioridad ->
                    val siguienteId = (recordatorios.value.maxOfOrNull { it.id } ?: 0) + 1

                    val nuevoRecordatorio = Recordatorio(
                        id = siguienteId,
                        titulo = titulo,
                        descripcion = descripcion,
                        categoria = categoria,
                        prioridad = prioridad
                    )

                    recordatorios.value = recordatorios.value + nuevoRecordatorio
                    navController.popBackStack()
                }
            )
        }

        composable(route = "detalle/{id}") { entrada ->
            val id = entrada.arguments?.getString("id")?.toIntOrNull()

            val recordatorio = recordatorios.value.find {
                it.id == id
            }

            if (recordatorio != null) {
                PantallaDetalle(
                    recordatorio = recordatorio,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            } else {
                Text(text = "No se encontró el recordatorio")
            }
        }
    }
}
