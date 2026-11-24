package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_recordatorios.data.RepositorioRecordatorios
import com.example.api_recordatorios.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetalleViewModel(private val repositorio: RepositorioRecordatorios, id: Int?) : ViewModel() {
    sealed interface Estado {
        data object Cargando : Estado
        data class Encontrado(val recordatorio: Recordatorio) : Estado
        data class Error(val mensaje: String) : Estado
        data object Eliminado : Estado
    }

    private val _estado = MutableStateFlow<Estado>(Estado.Cargando)
    val estado = _estado.asStateFlow()
    private var eliminando = false

    init {
        viewModelScope.launch {
            if (id == null) {
                _estado.value = Estado.Error("Identificador inválido")
                return@launch
            }
            repositorio.observar(id)
                .catch { _estado.value = Estado.Error("No se pudo leer el recordatorio") }
                .collect { valor ->
                    _estado.value = when {
                        eliminando && valor == null -> Estado.Eliminado
                        valor != null -> Estado.Encontrado(valor)
                        else -> Estado.Error("No se encontró el recordatorio")
                    }
                }
        }
    }

    fun cambiarEstado() {
        val actual = (_estado.value as? Estado.Encontrado)?.recordatorio ?: return
        viewModelScope.launch {
            try {
                val estado = if (actual.estado == EstadoRecordatorio.Pendiente) EstadoRecordatorio.Completado else EstadoRecordatorio.Pendiente
                repositorio.guardar(actual.copy(estado = estado))
                repositorio.sincronizar()
            } catch (_: Exception) {
                _estado.value = Estado.Error("No se pudo actualizar el recordatorio")
            }
        }
    }

    fun eliminar() {
        val actual = (_estado.value as? Estado.Encontrado)?.recordatorio ?: return
        viewModelScope.launch {
            try {
                eliminando = true
                repositorio.eliminar(actual.id)
                _estado.value = Estado.Eliminado
                repositorio.sincronizar()
            } catch (_: Exception) {
                eliminando = false
                _estado.value = Estado.Error("No se pudo eliminar el recordatorio")
            }
        }
    }
}
