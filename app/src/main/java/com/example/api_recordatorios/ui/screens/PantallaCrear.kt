package com.example.api_recordatorios.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.remember
import com.example.api_recordatorios.model.Prioridad
import com.example.api_recordatorios.data.DatosSimulados
import com.example.api_recordatorios.model.Categoria

@Composable
fun PantallaCrear(
    onCancelar: () -> Unit,
    onGuardar: (String, String, Categoria, Prioridad) -> Unit
) {

    val titulo = rememberSaveable { mutableStateOf("") }
    val descripcion = rememberSaveable { mutableStateOf("") }
    val tituloValido = titulo.value.trim().length >= 4
    val descripcionValida = descripcion.value.length <= 200
    val prioridad = remember { mutableStateOf<Prioridad?>(null) }
    val categoria = remember { mutableStateOf<Categoria?>(null) }

    val formularioValido = tituloValido &&
            descripcionValida &&
            categoria.value != null &&
            prioridad.value != null


    Column {
        Text(text = "Nuevo recordatorio")

        OutlinedTextField(
            value = titulo.value,
            onValueChange = { nuevoTexto ->
                titulo.value = nuevoTexto
            },
            label = { Text(text = "Título") },
            isError = titulo.value.isNotEmpty() && !tituloValido,
            supportingText = { Text(text = "Mínimo 4 caracteres") }
        )

        OutlinedTextField(
            value = descripcion.value,
            onValueChange = { nuevoTexto ->
                descripcion.value = nuevoTexto
            },
            label = { Text(text = "Descripción") },
            isError = !descripcionValida,
            supportingText = {
                Text(text = "${descripcion.value.length}/200 caracteres")
            }
        )

        Text(text = "Categoría")

        Row {
            DatosSimulados.categorias.forEach { opcion ->
                FilterChip(
                    selected = categoria.value == opcion,
                    onClick = { categoria.value = opcion },
                    label = { Text(text = opcion.nombre) }
                )
            }
        }

        Text(text = "Prioridad")

        Row {
            listOf(Prioridad.Baja, Prioridad.Media, Prioridad.Alta).forEach { opcion ->
                FilterChip(
                    selected = prioridad.value == opcion,
                    onClick = { prioridad.value = opcion },
                    label = { Text(text = opcion.etiqueta) }
                )
            }
        }

        Button(
            onClick = {
                val categoriaElegida = categoria.value
                val prioridadElegida = prioridad.value

                if (categoriaElegida != null && prioridadElegida != null) {
                    onGuardar(
                        titulo.value.trim(),
                        descripcion.value,
                        categoriaElegida,
                        prioridadElegida
                    )
                }
            },
            enabled = formularioValido
        ) {
            Text(text = "Guardar")
        }

        Button(onClick = onCancelar) {
            Text(text = "Cancelar")
        }
    }
}
