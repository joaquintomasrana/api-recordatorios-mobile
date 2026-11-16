package com.example.api_recordatorios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.api_recordatorios.model.Categoria
import com.example.api_recordatorios.model.Prioridad
import com.example.api_recordatorios.ui.state.EstadoCreacion
import com.example.api_recordatorios.ui.state.EstadoEnvio

@Composable
fun PantallaCrear(
    estado: EstadoCreacion,
    categorias: Set<Categoria>,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCategoriaChange: (Categoria) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onCancelar: () -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val guardando = estado.envio == EstadoEnvio.Guardando
    val editable = !guardando && estado.envio != EstadoEnvio.Exito

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Nuevo recordatorio", style = MaterialTheme.typography.headlineSmall)

        CampoRecordatorio(
            etiqueta = "Título",
            valor = estado.titulo,
            onCambio = onTituloChange,
            ayuda = "Mínimo 4 caracteres",
            mensajeError = estado.errorTitulo,
            habilitado = editable,
            unaLinea = true
        )

        CampoRecordatorio(
            etiqueta = "Descripción",
            valor = estado.descripcion,
            onCambio = onDescripcionChange,
            ayuda = "${estado.descripcion.length}/200 caracteres",
            mensajeError = estado.errorDescripcion,
            habilitado = editable
        )

        Text("Categoría")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categorias.forEach { opcion ->
                FilterChip(
                    selected = estado.categoria == opcion,
                    onClick = { onCategoriaChange(opcion) },
                    label = { Text(opcion.nombre) },
                    enabled = editable
                )
            }
        }

        Text("Prioridad")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Prioridad.Baja, Prioridad.Media, Prioridad.Alta).forEach { opcion ->
                FilterChip(
                    selected = estado.prioridad == opcion,
                    onClick = { onPrioridadChange(opcion) },
                    label = { Text(opcion.etiqueta) },
                    enabled = editable
                )
            }
        }

        val envio = estado.envio
        if (envio is EstadoEnvio.Error) {
            Text(
                text = envio.mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onGuardar, enabled = estado.puedeGuardar) {
                Text(if (guardando) "Guardando…" else "Guardar")
            }
            Button(onClick = onCancelar, enabled = editable) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun CampoRecordatorio(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    ayuda: String,
    mensajeError: String?,
    habilitado: Boolean,
    modifier: Modifier = Modifier,
    unaLinea: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        supportingText = { Text(mensajeError ?: ayuda) },
        isError = mensajeError != null,
        enabled = habilitado,
        singleLine = unaLinea,
        modifier = modifier.fillMaxWidth().semantics {
            if (mensajeError != null) error(mensajeError)
        }
    )
}
