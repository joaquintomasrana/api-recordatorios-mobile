package com.example.api_recordatorios.data.remote

import com.example.api_recordatorios.data.local.RecordatorioEntity
import retrofit2.Response
import retrofit2.http.*

data class RecordatorioDto(
    val clave: String,
    val titulo: String,
    val descripcion: String?,
    val categoria: String,
    val prioridad: String,
    val completado: Boolean,
    val fecha: Long,
    val eliminado: Boolean = false
) {
    fun valido() = clave.isNotBlank() && titulo.trim().length >= 4 &&
        (descripcion?.length ?: 0) <= 200 && categoria in listOf("Estudio", "Personal") &&
        prioridad in listOf("Alta", "Media", "Baja") && fecha > 0
}

fun RecordatorioEntity.aDto() = RecordatorioDto(
    clave, titulo, descripcion, categoria, prioridad, completado, fecha, eliminado
)

fun RecordatorioDto.aEntidad(idLocal: Int = 0) = RecordatorioEntity(
    id = idLocal, clave = clave, titulo = titulo, descripcion = descripcion,
    categoria = categoria, prioridad = prioridad, completado = completado,
    fecha = fecha, eliminado = eliminado, pendiente = false
)

interface ServicioRecordatorios {
    @GET("recordatorios")
    suspend fun obtenerTodos(): Response<List<RecordatorioDto>>

    @PUT("recordatorios/{clave}")
    suspend fun guardar(
        @Path("clave") clave: String,
        @Body recordatorio: RecordatorioDto
    ): Response<RecordatorioDto>
}
