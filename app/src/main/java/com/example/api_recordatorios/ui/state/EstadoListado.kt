package com.example.api_recordatorios.ui.state

import com.example.api_recordatorios.model.Recordatorio

sealed interface EstadoListado {
    data object Cargando : EstadoListado
    data object Vacio : EstadoListado

    data class ConDatos(
        val recordatorios: List<Recordatorio>
    ) : EstadoListado

    data class Error(
        val mensaje: String
    ) : EstadoListado
}