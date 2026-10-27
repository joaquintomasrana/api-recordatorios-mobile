package com.example.api_recordatorios.model
sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object Completado : EstadoRecordatorio("Completado")
}