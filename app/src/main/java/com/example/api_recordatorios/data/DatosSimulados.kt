package com.example.api_recordatorios.data

import com.example.api_recordatorios.model.Categoria
import com.example.api_recordatorios.model.EstadoRecordatorio
import com.example.api_recordatorios.model.Prioridad
import com.example.api_recordatorios.model.Recordatorio
import androidx.compose.ui.graphics.Color

object DatosSimulados {

    val recordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 1,
            titulo = "Estudiar Kotlin",
            categoria = Categoria("Estudio"),
            prioridad = Prioridad.Alta,
            descripcion = "Repasar clases y objetos"
        ),
        Recordatorio(
            id = 2,
            titulo = "Comprar alimentos",
            categoria = Categoria("Personal"),
            prioridad = Prioridad.Media
        ),
        Recordatorio(
            id = 3,
            titulo = "Entregar actividad",
            categoria = Categoria("Estudio"),
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = 4,
            titulo = "Ordenar el escritorio",
            categoria = Categoria("Personal"),
            prioridad = Prioridad.Baja
        )
    )
    val categorias: Set<Categoria> = recordatorios
        .map { it.categoria }
        .toSet()

    val coloresPorCategoria: Map<Categoria, Color> = mapOf(
        Categoria("Estudio") to Color(0xFF1565C0),
        Categoria("Personal") to Color(0xFF2E7D32)
    )

    val pendientes: List<Recordatorio> = recordatorios.filter {
        it.estado == EstadoRecordatorio.Pendiente
    }

    val dePrioridadAlta: List<Recordatorio> = recordatorios.filter {
        it.prioridad == Prioridad.Alta
    }



}
