package com.example.api_recordatorios.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.api_recordatorios.model.*
import java.util.UUID

@Entity(tableName = "recordatorios", indices = [Index(value = ["clave"], unique = true)])
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clave: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descripcion: String?,
    val categoria: String,
    val prioridad: String,
    val completado: Boolean,
    val fecha: Long,
    val pendiente: Boolean = true,
    val eliminado: Boolean = false,
    val revision: Long = 1,
    val error: String? = null
)

fun RecordatorioEntity.aModelo() = Recordatorio(
    id = id, titulo = titulo, descripcion = descripcion,
    categoria = Categoria(categoria),
    prioridad = when (prioridad) {
        "Alta" -> Prioridad.Alta
        "Baja" -> Prioridad.Baja
        else -> Prioridad.Media
    },
    estado = if (completado) EstadoRecordatorio.Completado else EstadoRecordatorio.Pendiente,
    fecha = fecha,
    sincronizacion = when {
        error != null -> "Error de sincronización"
        pendiente -> "Pendiente de sincronización"
        else -> "Sincronizado"
    }
)
