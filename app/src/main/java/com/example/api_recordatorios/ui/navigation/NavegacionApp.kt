package com.example.api_recordatorios.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.data.RepositorioRecordatoriosEnMemoria
import com.example.api_recordatorios.ui.components.MensajeListado
import com.example.api_recordatorios.ui.screens.PantallaCrear
import com.example.api_recordatorios.ui.screens.PantallaDetalle
import com.example.api_recordatorios.ui.screens.PantallaRecordatorios
import com.example.api_recordatorios.ui.state.EstadoEnvio
import com.example.api_recordatorios.ui.state.EstadoListado
import com.example.api_recordatorios.ui.viewmodel.CreacionViewModel
import com.example.api_recordatorios.ui.viewmodel.DetalleViewModel
import com.example.api_recordatorios.ui.viewmodel.ListadoViewModel

// Activar de a una para verificar los casos de la actividad.
private const val INICIAR_SIN_RECORDATORIOS = false
private const val FALLAR_PRIMERA_CARGA = false
private const val FALLAR_PRIMER_GUARDADO = false

@Composable
fun NavegacionApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val listadoViewModel = viewModel {
        ListadoViewModel(
            RepositorioRecordatoriosEnMemoria(
                iniciales = if (INICIAR_SIN_RECORDATORIOS) emptyList() else DatosSimulados.recordatorios,
                fallarPrimeraCarga = FALLAR_PRIMERA_CARGA,
                fallarPrimerGuardado = FALLAR_PRIMER_GUARDADO
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = "listado",
        modifier = modifier
    ) {
        composable(route = "listado") {
            when (val estado = listadoViewModel.estado.collectAsStateWithLifecycle().value) {
                EstadoListado.Cargando -> MensajeListado(mensaje = "Cargando…", cargando = true)
                EstadoListado.Vacio -> MensajeListado(
                    mensaje = "No hay recordatorios",
                    textoBoton = "Crear recordatorio",
                    onAccion = { navController.navigate("crear") }
                )
                is EstadoListado.Error -> MensajeListado(
                    mensaje = estado.mensaje,
                    textoBoton = "Reintentar",
                    onAccion = listadoViewModel::cargar
                )
                is EstadoListado.ConDatos -> PantallaRecordatorios(
                    recordatorios = estado.recordatorios,
                    onRecordatorioClick = { id -> navController.navigate("detalle/$id") },
                    onCrearRecordatorio = { navController.navigate("crear") }
                )
            }
        }

        composable(route = "crear") {
            val creacionViewModel = viewModel {
                CreacionViewModel(listadoViewModel.repositorio, DatosSimulados.categorias)
            }
            val estado = creacionViewModel.estado.collectAsStateWithLifecycle().value

            BackHandler(enabled = estado.envio == EstadoEnvio.Guardando) {}
            LaunchedEffect(estado.envio) {
                if (estado.envio == EstadoEnvio.Exito) {
                    listadoViewModel.cargar()
                    navController.popBackStack()
                }
            }

            PantallaCrear(
                estado = estado,
                categorias = creacionViewModel.categorias,
                onTituloChange = creacionViewModel::cambiarTitulo,
                onDescripcionChange = creacionViewModel::cambiarDescripcion,
                onCategoriaChange = creacionViewModel::elegirCategoria,
                onPrioridadChange = creacionViewModel::elegirPrioridad,
                onCancelar = { navController.popBackStack() },
                onGuardar = creacionViewModel::guardar
            )
        }

        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("id")
            val detalleViewModel = viewModel {
                DetalleViewModel(listadoViewModel.repositorio, id)
            }
            val estado = detalleViewModel.estado.collectAsStateWithLifecycle().value
            val onVolver: () -> Unit = { navController.popBackStack() }

            when (estado) {
                is DetalleViewModel.Estado.Encontrado -> PantallaDetalle(
                    recordatorio = estado.recordatorio,
                    onVolver = onVolver
                )
                is DetalleViewModel.Estado.Error -> MensajeListado(
                    mensaje = estado.mensaje,
                    textoBoton = "Volver",
                    onAccion = onVolver
                )
            }
        }
    }
}
