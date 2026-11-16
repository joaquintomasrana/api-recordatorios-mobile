package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_recordatorios.data.RepositorioRecordatorios
import com.example.api_recordatorios.ui.state.EstadoListado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListadoViewModel(
    internal val repositorio: RepositorioRecordatorios
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoListado>(EstadoListado.Cargando)
    val estado: StateFlow<EstadoListado> = _estado.asStateFlow()

    private var carga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        carga?.cancel()
        carga = viewModelScope.launch {
            _estado.value = EstadoListado.Cargando
            delay(800)

            _estado.value = try {
                val lista = repositorio.obtenerTodos()
                if (lista.isEmpty()) EstadoListado.Vacio else EstadoListado.ConDatos(lista)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                EstadoListado.Error("No se pudieron cargar los recordatorios")
            }
        }
    }
}
