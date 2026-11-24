package com.example.api_recordatorios.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.api_recordatorios.RecordatoriosApp
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.ui.components.MensajeListado
import com.example.api_recordatorios.ui.screens.*
import com.example.api_recordatorios.ui.state.*
import com.example.api_recordatorios.ui.viewmodel.*

@Composable
fun NavegacionApp(modifier: Modifier = Modifier) {
    val app = LocalContext.current.applicationContext as RecordatoriosApp
    val repositorio = app.repositorio
    val navController = rememberNavController()
    val listadoViewModel = viewModel { ListadoViewModel(repositorio) }

    NavHost(navController, startDestination = "listado", modifier = modifier) {
        composable("listado") {
            val estado = listadoViewModel.estado.collectAsStateWithLifecycle().value
            val mensaje = listadoViewModel.sincronizacion.collectAsStateWithLifecycle().value
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = listadoViewModel::sincronizar) { Text("Sincronizar") }
                    Button(onClick = { navController.navigate("preferencias") }) { Text("Ajustes") }
                }
                Text(mensaje, Modifier.padding(horizontal = 16.dp))
                Box(Modifier.weight(1f)) {
                    when (val actual = estado) {
                        EstadoListado.Cargando -> MensajeListado("Cargando…", cargando = true)
                        EstadoListado.Vacio -> MensajeListado(
                            "No hay recordatorios", "Crear recordatorio",
                            { navController.navigate("crear") }
                        )
                        is EstadoListado.Error -> MensajeListado(
                            actual.mensaje, "Reintentar", listadoViewModel::cargar
                        )
                        is EstadoListado.ConDatos -> PantallaRecordatorios(
                            actual.recordatorios,
                            onRecordatorioClick = { navController.navigate("detalle/$it") },
                            onCrearRecordatorio = { navController.navigate("crear") }
                        )
                    }
                }
            }
        }

        composable("crear") {
            val vm = viewModel { CreacionViewModel(repositorio, DatosSimulados.categorias) }
            Formulario(vm, onVolver = {
                app.sincronizar()
                navController.popBackStack()
            })
        }

        composable("editar/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entrada ->
            val id = entrada.arguments?.getInt("id")
            val vm = viewModel { CreacionViewModel(repositorio, DatosSimulados.categorias, id) }
            Formulario(vm, onVolver = {
                app.sincronizar()
                navController.popBackStack()
            })
        }

        composable("detalle/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { entrada ->
            val id = entrada.arguments?.getInt("id")
            val vm = viewModel { DetalleViewModel(repositorio, id) }
            val estado = vm.estado.collectAsStateWithLifecycle().value
            LaunchedEffect(estado) {
                if (estado == DetalleViewModel.Estado.Eliminado) navController.popBackStack()
            }
            when (val actual = estado) {
                DetalleViewModel.Estado.Cargando -> MensajeListado("Cargando…", cargando = true)
                DetalleViewModel.Estado.Eliminado -> Unit
                is DetalleViewModel.Estado.Error -> MensajeListado(
                    actual.mensaje, "Volver", { navController.popBackStack() }
                )
                is DetalleViewModel.Estado.Encontrado -> PantallaDetalle(
                    actual.recordatorio,
                    onVolver = { navController.popBackStack() },
                    onEditar = { navController.navigate("editar/$id") },
                    onCambiarEstado = vm::cambiarEstado,
                    onEliminar = vm::eliminar
                )
            }
        }

        composable("preferencias") {
            val vm = viewModel { PreferenciasViewModel(repositorio.preferencias) }
            val preferencias = vm.estado.collectAsStateWithLifecycle().value
            val error = vm.error.collectAsStateWithLifecycle().value
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ajustes", style = MaterialTheme.typography.headlineSmall)
                Text("Tema oscuro")
                Switch(preferencias.temaOscuro, { vm.guardar(preferencias.copy(temaOscuro = it)) })
                Text("Mostrar completados")
                Switch(preferencias.mostrarCompletados, { vm.guardar(preferencias.copy(mostrarCompletados = it)) })
                Text("Orden del listado")
                Button(onClick = {
                    vm.guardar(preferencias.copy(orden = if (preferencias.orden == "Fecha") "Título" else "Fecha"))
                }) { Text(preferencias.orden) }
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
                Button(onClick = { navController.popBackStack() }) { Text("Volver") }
            }
        }
    }
}

@Composable
private fun Formulario(vm: CreacionViewModel, onVolver: () -> Unit) {
    val estado = vm.estado.collectAsStateWithLifecycle().value
    BackHandler(enabled = estado.envio == EstadoEnvio.Guardando) {}
    LaunchedEffect(estado.envio) {
        if (estado.envio == EstadoEnvio.Exito) onVolver()
    }
    PantallaCrear(
        estado, vm.categorias,
        onTituloChange = vm::cambiarTitulo,
        onDescripcionChange = vm::cambiarDescripcion,
        onCategoriaChange = vm::elegirCategoria,
        onPrioridadChange = vm::elegirPrioridad,
        onCancelar = onVolver,
        onGuardar = vm::guardar
    )
}
