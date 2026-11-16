package com.example.api_recordatorios.data

import com.example.api_recordatorios.model.Recordatorio

class RepositorioRecordatoriosEnMemoria(
    iniciales: List<Recordatorio> = DatosSimulados.recordatorios,
    private var fallarPrimeraCarga: Boolean = false,
    private var fallarPrimerGuardado: Boolean = false
) : RepositorioRecordatorios {
    private var recordatorios = iniciales.toList()

    override fun obtenerTodos(): List<Recordatorio> {
        if (fallarPrimeraCarga) {
            fallarPrimeraCarga = false
            error("No se pudieron cargar los recordatorios")
        }
        return recordatorios
    }

    override fun guardar(recordatorio: Recordatorio) {
        if (fallarPrimerGuardado) {
            fallarPrimerGuardado = false
            error("No se pudo guardar el recordatorio")
        }
        recordatorios = recordatorios + recordatorio
    }
}
