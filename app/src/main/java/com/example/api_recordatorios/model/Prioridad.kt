package com.example.api_recordatorios.model

sealed class Prioridad(val etiqueta: String) {
    data object Alta : Prioridad("Alta")
    data object Media : Prioridad("Media")
    data object Baja : Prioridad("Baja")
}