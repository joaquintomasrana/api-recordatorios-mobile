package com.example.api_recordatorios

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.api_recordatorios.data.*
import com.example.api_recordatorios.data.local.*
import com.example.api_recordatorios.data.remote.*
import com.example.api_recordatorios.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class PersistenciaTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    private class ServicioPrueba : ServicioRecordatorios {
        val registros = mutableMapOf<String, RecordatorioDto>()
        var desconectado = false
        var perderRespuesta = false
        var duranteEnvio: (suspend () -> Unit)? = null
        override suspend fun obtenerTodos(): Response<List<RecordatorioDto>> {
            if (desconectado) throw IOException()
            return Response.success(registros.values.toList())
        }
        override suspend fun guardar(clave: String, recordatorio: RecordatorioDto): Response<RecordatorioDto> {
            if (desconectado) throw IOException()
            registros[clave] = recordatorio
            duranteEnvio?.invoke()
            duranteEnvio = null
            if (perderRespuesta) {
                perderRespuesta = false
                throw IOException("Respuesta perdida después de guardar")
            }
            return Response.success(recordatorio)
        }
    }

    private fun ejemplo(id: Int = 0) = Recordatorio(
        id, "Leer apuntes", Categoria("Estudio"), Prioridad.Alta
    )

    @Test fun cierreReaperturaEdicionYBorrado() = runBlocking {
        val nombre = "prueba-${System.nanoTime()}.db"
        fun abrir() = Room.databaseBuilder(context, BaseRecordatorios::class.java, nombre).build()
        var base = abrir()
        val servicio = ServicioPrueba()
        fun repositorio() = RepositorioLocal(base, servicio, PreferenciasRepository(context))
        try {
            repositorio().guardar(ejemplo())
            val id = repositorio().observarTodos().first().single().id
            base.close()
            base = abrir()
            assertEquals("Leer apuntes", repositorio().observar(id).first()?.titulo)
            repositorio().guardar(ejemplo(id).copy(titulo = "Título actualizado"))
            assertEquals("Título actualizado", repositorio().observar(id).first()?.titulo)
            repositorio().eliminar(id)
            base.close()
            base = abrir()
            assertTrue(repositorio().observarTodos().first().isEmpty())
            assertTrue(base.recordatorios().pendientes().single().eliminado)
        } finally {
            base.close()
            context.deleteDatabase(nombre)
        }
    }

    @Test fun offlineReintentoSinDuplicadosYBorradoRemoto() = runBlocking {
        val base = Room.inMemoryDatabaseBuilder(context, BaseRecordatorios::class.java).build()
        val servicio = ServicioPrueba()
        val repo = RepositorioLocal(base, servicio, PreferenciasRepository(context))
        try {
            servicio.desconectado = true
            repo.guardar(ejemplo())
            repo.sincronizar()
            assertEquals(1, repo.observarTodos().first().size)
            assertTrue(base.recordatorios().pendientes().isNotEmpty())
            servicio.desconectado = false
            servicio.perderRespuesta = true
            repo.sincronizar()
            repo.sincronizar()
            repo.sincronizar()
            assertEquals(1, servicio.registros.size)
            assertEquals(1, repo.observarTodos().first().size)
            assertTrue(base.recordatorios().pendientes().isEmpty())
            val id = repo.observarTodos().first().single().id
            repo.eliminar(id)
            repo.sincronizar()
            assertTrue(servicio.registros.values.single().eliminado)
            assertTrue(repo.observarTodos().first().isEmpty())
        } finally { base.close() }
    }

    @Test fun edicionDuranteSincronizacionPermanecePendiente() = runBlocking {
        val base = Room.inMemoryDatabaseBuilder(context, BaseRecordatorios::class.java).build()
        val servicio = ServicioPrueba()
        val repo = RepositorioLocal(base, servicio, PreferenciasRepository(context))
        try {
            repo.guardar(ejemplo())
            val id = repo.observarTodos().first().single().id
            servicio.duranteEnvio = { repo.guardar(ejemplo(id).copy(titulo = "Edición durante envío")) }
            repo.sincronizar()
            assertEquals("Edición durante envío", repo.observar(id).first()?.titulo)
            assertTrue(base.recordatorios().pendientes().isNotEmpty())
            repo.sincronizar()
            assertEquals("Edición durante envío", servicio.registros.values.single().titulo)
            assertTrue(base.recordatorios().pendientes().isEmpty())
        } finally { base.close() }
    }

    @Test fun preferenciasPersistenYFiltran() = runBlocking {
        val preferencias = PreferenciasRepository(context)
        val anterior = preferencias.preferencias.first()
        try {
            preferencias.guardar(Preferencias(true, false, "Título"))
            val otra = PreferenciasRepository(context)
            assertEquals(Preferencias(true, false, "Título"), otra.preferencias.first())
            preferencias.guardar(Preferencias(orden = "desconocido"))
            assertEquals("Fecha", preferencias.preferencias.first().orden)
        } finally { preferencias.guardar(anterior) }
    }
}
