package com.example.api_recordatorios.ui.state

import com.example.api_recordatorios.model.Categoria
import com.example.api_recordatorios.model.Prioridad

data class EstadoCreacion(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val categoriaValida: Boolean = false,
    val envio: EstadoEnvio = EstadoEnvio.Pendiente
) {
    val puedeGuardar: Boolean
        get() = titulo.isNotEmpty() && errorTitulo == null && errorDescripcion == null &&
            categoriaValida && prioridad != null &&
            (envio == EstadoEnvio.Pendiente || envio is EstadoEnvio.Error)
}

sealed interface EstadoEnvio {
    data object Pendiente : EstadoEnvio
    data object Guardando : EstadoEnvio
    data object Exito : EstadoEnvio
    data class Error(val mensaje: String) : EstadoEnvio
}
