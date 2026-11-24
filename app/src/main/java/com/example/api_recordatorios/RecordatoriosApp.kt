package com.example.api_recordatorios

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import com.example.api_recordatorios.data.*
import com.example.api_recordatorios.data.local.BaseRecordatorios
import com.example.api_recordatorios.data.remote.ServicioRecordatorios
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RecordatoriosApp : Application() {
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var repositorio: RepositorioLocal
        private set

    override fun onCreate() {
        super.onCreate()
        val cliente = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
        val servicio = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ServicioRecordatorios::class.java)
        repositorio = RepositorioLocal(
            BaseRecordatorios.obtener(this), servicio, PreferenciasRepository(this)
        )

        val conectividad = getSystemService(ConnectivityManager::class.java)
        conectividad.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                sincronizar()
            }
        })
        sincronizar()
    }

    fun sincronizar() {
        alcance.launch { repositorio.sincronizar() }
    }
}
