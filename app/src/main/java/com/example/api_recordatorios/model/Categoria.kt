package com.example.api_recordatorios.model

data class Categoria(
    val nombre: String
) {
    init {
        require(nombre.isNotBlank()) {
            "El nombre de la categoría no puede estar vacío"
        }
    }
}