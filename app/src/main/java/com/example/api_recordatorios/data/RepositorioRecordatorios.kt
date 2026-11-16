package com.example.api_recordatorios.data

import com.example.api_recordatorios.model.Recordatorio

interface RepositorioRecordatorios {
    fun obtenerTodos(): List<Recordatorio>

    fun guardar(recordatorio: Recordatorio)
}