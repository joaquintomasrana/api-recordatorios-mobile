package com.example.api_recordatorios.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {
    @Query("SELECT * FROM recordatorios WHERE eliminado = 0 ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<RecordatorioEntity>>

    @Query("SELECT * FROM recordatorios WHERE id = :id AND eliminado = 0")
    fun observarPorId(id: Int): Flow<RecordatorioEntity?>

    @Query("SELECT * FROM recordatorios WHERE eliminado = 0 AND completado = :completado")
    fun filtrarPorEstado(completado: Boolean): Flow<List<RecordatorioEntity>>

    @Query("SELECT * FROM recordatorios WHERE id = :id")
    suspend fun obtener(id: Int): RecordatorioEntity?

    @Query("SELECT * FROM recordatorios WHERE clave = :clave")
    suspend fun obtenerPorClave(clave: String): RecordatorioEntity?

    @Query("SELECT * FROM recordatorios WHERE pendiente = 1")
    suspend fun pendientes(): List<RecordatorioEntity>

    @Insert
    suspend fun insertar(recordatorio: RecordatorioEntity): Long

    @Update
    suspend fun actualizar(recordatorio: RecordatorioEntity)

    @Delete
    suspend fun eliminar(recordatorio: RecordatorioEntity)

    @Query("UPDATE recordatorios SET pendiente = 0, error = NULL WHERE id = :id AND revision = :revision")
    suspend fun confirmar(id: Int, revision: Long)

    @Query("UPDATE recordatorios SET error = :mensaje WHERE id = :id AND revision = :revision")
    suspend fun registrarError(id: Int, revision: Long, mensaje: String)
}
