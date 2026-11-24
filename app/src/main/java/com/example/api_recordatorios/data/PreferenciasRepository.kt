package com.example.api_recordatorios.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "preferencias")

data class Preferencias(
    val temaOscuro: Boolean = false,
    val mostrarCompletados: Boolean = true,
    val orden: String = "Fecha"
)

internal fun ordenValido(valor: String?) =
    valor?.takeIf { it in listOf("Fecha", "Título") } ?: "Fecha"

class PreferenciasRepository(context: Context) {
    private val almacen = context.applicationContext.dataStore
    private val oscuro = booleanPreferencesKey("tema_oscuro")
    private val completados = booleanPreferencesKey("mostrar_completados")
    private val orden = stringPreferencesKey("orden")

    val preferencias = almacen.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }.map {
        Preferencias(
            temaOscuro = it[oscuro] ?: false,
            mostrarCompletados = it[completados] ?: true,
            orden = ordenValido(it[orden])
        )
    }

    suspend fun guardar(valor: Preferencias) {
        almacen.edit {
            it[oscuro] = valor.temaOscuro
            it[completados] = valor.mostrarCompletados
            it[orden] = ordenValido(valor.orden)
        }
    }
}
