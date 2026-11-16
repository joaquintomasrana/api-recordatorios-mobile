package com.example.api_recordatorios.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.api_recordatorios.data.RepositorioRecordatorios
import com.example.api_recordatorios.model.Categoria
import com.example.api_recordatorios.model.Prioridad
import com.example.api_recordatorios.model.Recordatorio
import com.example.api_recordatorios.ui.state.EstadoCreacion
import com.example.api_recordatorios.ui.state.EstadoEnvio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreacionViewModel(
    private val repositorio: RepositorioRecordatorios,
    val categorias: Set<Categoria>
) : ViewModel() {
    private val _estado = MutableStateFlow(EstadoCreacion())
    val estado = _estado.asStateFlow()

    fun cambiarTitulo(titulo: String) = editar {
        it.copy(
            titulo = titulo,
            errorTitulo = if (titulo.isNotEmpty() && titulo.trim().length < 4) {
                "El título debe tener al menos 4 caracteres"
            } else null
        )
    }

    fun cambiarDescripcion(descripcion: String) = editar {
        it.copy(
            descripcion = descripcion,
            errorDescripcion = if (descripcion.length > 200) {
                "La descripción admite hasta 200 caracteres"
            } else null
        )
    }

    fun elegirCategoria(categoria: Categoria) = editar {
        it.copy(categoria = categoria, categoriaValida = categoria in categorias)
    }

    fun elegirPrioridad(prioridad: Prioridad) = editar {
        it.copy(prioridad = prioridad)
    }

    private fun editar(cambio: (EstadoCreacion) -> EstadoCreacion) {
        _estado.update { actual ->
            when (actual.envio) {
                EstadoEnvio.Guardando, EstadoEnvio.Exito -> actual
                else -> cambio(actual).copy(envio = EstadoEnvio.Pendiente)
            }
        }
    }

    fun guardar() {
        val formulario = _estado.value
        if (!formulario.puedeGuardar) return
        val categoria = formulario.categoria ?: return
        val prioridad = formulario.prioridad ?: return

        _estado.update { it.copy(envio = EstadoEnvio.Guardando) }
        viewModelScope.launch {
            delay(800)
            val resultado = try {
                val siguienteId = (repositorio.obtenerTodos().maxOfOrNull { it.id } ?: 0) + 1
                repositorio.guardar(
                    Recordatorio(
                        id = siguienteId,
                        titulo = formulario.titulo.trim(),
                        descripcion = formulario.descripcion.ifBlank { null },
                        categoria = categoria,
                        prioridad = prioridad
                    )
                )
                EstadoEnvio.Exito
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                EstadoEnvio.Error("No se pudo guardar el recordatorio. Volvé a intentar.")
            }
            _estado.update { it.copy(envio = resultado) }
        }
    }
}
