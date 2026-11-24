package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_recordatorios.data.RepositorioRecordatorios
import com.example.api_recordatorios.ui.state.EstadoListado
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListadoViewModel(internal val repositorio: RepositorioRecordatorios) : ViewModel() {
    private val _estado = MutableStateFlow<EstadoListado>(EstadoListado.Cargando)
    val estado = _estado.asStateFlow()
    val sincronizacion = repositorio.mensajeSincronizacion
    private var consulta: Job? = null

    init { cargar() }

    fun cargar() {
        consulta?.cancel()
        consulta = viewModelScope.launch {
            repositorio.observarTodos()
                .map { lista ->
                    if (lista.isEmpty()) EstadoListado.Vacio else EstadoListado.ConDatos(lista)
                }
                .catch { emit(EstadoListado.Error("No se pudo leer el almacenamiento. Reintentá.")) }
                .collect { _estado.value = it }
        }
    }

    fun sincronizar() {
        viewModelScope.launch { repositorio.sincronizar() }
    }
}
