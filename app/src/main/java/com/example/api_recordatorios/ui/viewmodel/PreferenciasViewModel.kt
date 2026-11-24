package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_recordatorios.data.Preferencias
import com.example.api_recordatorios.data.PreferenciasRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PreferenciasViewModel(private val repositorio: PreferenciasRepository) : ViewModel() {
    val estado = repositorio.preferencias.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Preferencias())
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun guardar(valor: Preferencias) {
        viewModelScope.launch {
            try {
                repositorio.guardar(valor)
                _error.value = null
            } catch (_: java.io.IOException) {
                _error.value = "No se pudo guardar la configuración"
            }
        }
    }
}
