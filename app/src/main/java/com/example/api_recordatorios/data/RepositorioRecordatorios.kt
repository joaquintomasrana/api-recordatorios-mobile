package com.example.api_recordatorios.data

import com.example.api_recordatorios.model.Recordatorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RepositorioRecordatorios {
    val mensajeSincronizacion: StateFlow<String>
    fun observarTodos(): Flow<List<Recordatorio>>
    fun observar(id: Int): Flow<Recordatorio?>
    suspend fun guardar(recordatorio: Recordatorio)
    suspend fun eliminar(id: Int)
    suspend fun sincronizar()
}
