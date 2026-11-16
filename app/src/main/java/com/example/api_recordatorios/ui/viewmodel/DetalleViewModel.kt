package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.api_recordatorios.data.RepositorioRecordatorios
import com.example.api_recordatorios.model.Recordatorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetalleViewModel(repositorio: RepositorioRecordatorios, id: Int?) : ViewModel() {
    sealed interface Estado {
        data class Encontrado(val recordatorio: Recordatorio) : Estado
        data class Error(val mensaje: String) : Estado
    }

    private val estadoInicial: Estado = try {
        val recordatorio = repositorio.obtenerTodos().find { it.id == id }
        if (recordatorio != null) Estado.Encontrado(recordatorio)
        else Estado.Error("No se encontró el recordatorio")
    } catch (_: Exception) {
        Estado.Error("No se pudo cargar el detalle")
    }

    val estado = MutableStateFlow(estadoInicial).asStateFlow()
}
