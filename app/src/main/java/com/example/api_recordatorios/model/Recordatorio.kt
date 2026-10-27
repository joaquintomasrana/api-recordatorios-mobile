package com.example.api_recordatorios.model

data class Recordatorio(
    val id: Int,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val descripcion: String? = null
) {
    init {
        require(id > 0) {
            "El identificador debe ser mayor que cero"
        }

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío"
        }
    }
}