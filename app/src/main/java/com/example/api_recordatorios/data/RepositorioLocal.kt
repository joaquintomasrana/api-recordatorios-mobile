package com.example.api_recordatorios.data

import androidx.room.withTransaction
import com.example.api_recordatorios.data.local.*
import com.example.api_recordatorios.data.remote.*
import com.example.api_recordatorios.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RepositorioLocal(
    private val base: BaseRecordatorios,
    private val servicio: ServicioRecordatorios,
    val preferencias: PreferenciasRepository
) : RepositorioRecordatorios {
    private val dao = base.recordatorios()
    private val mutex = Mutex()
    private val _mensaje = MutableStateFlow("Datos locales disponibles")
    override val mensajeSincronizacion = _mensaje.asStateFlow()

    override fun observarTodos() = combine(dao.observarTodos(), preferencias.preferencias) { lista, opciones ->
        val visibles = lista.filter { opciones.mostrarCompletados || !it.completado }
        val ordenados = if (opciones.orden == "Título") visibles.sortedBy { it.titulo.lowercase() } else visibles
        ordenados.map { it.aModelo() }
    }

    override fun observar(id: Int) = dao.observarPorId(id).map { it?.aModelo() }

    override suspend fun guardar(recordatorio: Recordatorio) {
        require(recordatorio.titulo.trim().length >= 4)
        require((recordatorio.descripcion?.length ?: 0) <= 200)
        require(recordatorio.categoria.nombre in listOf("Estudio", "Personal"))
        base.withTransaction {
            val anterior = dao.obtener(recordatorio.id)
            val nuevo = RecordatorioEntity(
                id = anterior?.id ?: 0,
                clave = anterior?.clave ?: java.util.UUID.randomUUID().toString(),
                titulo = recordatorio.titulo.trim(),
                descripcion = recordatorio.descripcion,
                categoria = recordatorio.categoria.nombre,
                prioridad = recordatorio.prioridad.etiqueta,
                completado = recordatorio.estado == EstadoRecordatorio.Completado,
                fecha = anterior?.fecha ?: recordatorio.fecha,
                revision = (anterior?.revision ?: 0) + 1
            )
            if (anterior == null) dao.insertar(nuevo) else dao.actualizar(nuevo)
        }
        _mensaje.value = "Cambios guardados en el dispositivo"
    }

    override suspend fun eliminar(id: Int) {
        base.withTransaction {
            val actual = dao.obtener(id) ?: return@withTransaction
            // Se conserva la marca de borrado para poder enviarla al recuperar la red.
            dao.actualizar(actual.copy(eliminado = true, pendiente = true, revision = actual.revision + 1, error = null))
        }
    }

    override suspend fun sincronizar() {
        // Evita que el botón y el cambio de conectividad envíen operaciones en paralelo.
        if (!mutex.tryLock()) return
        try {
            _mensaje.value = "Sincronizando…"
            for (local in dao.pendientes()) {
                try {
                    val respuesta = servicio.guardar(local.clave, local.aDto())
                    comprobar(respuesta)
                    val remoto = respuesta.body()
                    require(remoto != null && remoto.valido() && remoto == local.aDto()) {
                        "El servicio devolvió datos inválidos"
                    }
                    dao.confirmar(local.id, local.revision)
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    dao.registrarError(local.id, local.revision, mensaje(error))
                    throw error
                }
            }

            val respuesta = servicio.obtenerTodos()
            comprobar(respuesta)
            val remotos = respuesta.body() ?: error("Respuesta vacía")
            require(remotos.all { it.valido() }) { "El servicio devolvió datos inválidos" }
            base.withTransaction {
                for (remoto in remotos) {
                    val local = dao.obtenerPorClave(remoto.clave)
                    // Una edición local pendiente tiene prioridad sobre la copia remota.
                    if (local == null) dao.insertar(remoto.aEntidad())
                    else if (!local.pendiente) dao.actualizar(remoto.aEntidad(local.id).copy(revision = local.revision))
                }
            }
            _mensaje.value = "Sincronización completa"
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            _mensaje.value = mensaje(error)
        } finally {
            mutex.unlock()
        }
    }

    private fun comprobar(respuesta: Response<*>) {
        if (respuesta.isSuccessful) return
        val detalle = when (respuesta.code()) {
            in 400..499 -> "Solicitud inválida (HTTP ${respuesta.code()})"
            else -> "Error del servidor (HTTP ${respuesta.code()})"
        }
        throw IllegalStateException(detalle)
    }

    private fun mensaje(error: Exception) = when (error) {
        is SocketTimeoutException -> "El servicio tardó demasiado. Podés reintentar."
        is IOException -> "Sin conexión con el servicio. Los datos siguen guardados."
        else -> error.message ?: "No se pudo sincronizar. Podés reintentar."
    }
}
