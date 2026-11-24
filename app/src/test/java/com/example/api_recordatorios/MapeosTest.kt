package com.example.api_recordatorios

import com.example.api_recordatorios.data.ordenValido
import com.example.api_recordatorios.data.local.RecordatorioEntity
import com.example.api_recordatorios.data.local.aModelo
import com.example.api_recordatorios.data.remote.*
import com.example.api_recordatorios.model.*
import org.junit.Assert.*
import org.junit.Test

class MapeosTest {
    @Test fun conversionesConservanIdentidadYDatos() {
        val entidad = RecordatorioEntity(
            id = 7, clave = "clave-estable", titulo = "Leer apuntes",
            descripcion = "Clase 3", categoria = "Estudio", prioridad = "Alta",
            completado = true, fecha = 123456L
        )
        val dto = entidad.aDto()
        val copia = dto.aEntidad(entidad.id)
        assertEquals(entidad.clave, copia.clave)
        assertEquals(entidad.titulo, copia.titulo)
        assertFalse(copia.pendiente)
        assertEquals(EstadoRecordatorio.Completado, copia.aModelo().estado)
        assertEquals(Prioridad.Alta, copia.aModelo().prioridad)
    }

    @Test fun respuestasInvalidasSeRechazan() {
        val dto = RecordatorioDto("abc", "Leer", null, "Estudio", "Alta", false, 1L)
        assertTrue(dto.valido())
        assertFalse(dto.copy(titulo = " x ").valido())
        assertFalse(dto.copy(prioridad = "Urgente").valido())
        assertFalse(dto.copy(descripcion = "a".repeat(201)).valido())
    }

    @Test fun ordenDesconocidoUsaValorPredeterminado() {
        assertEquals("Fecha", ordenValido(null))
        assertEquals("Fecha", ordenValido("opcion-desconocida"))
        assertEquals("Título", ordenValido("Título"))
    }
}
