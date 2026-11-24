package com.example.api_recordatorios.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecordatorioEntity::class], version = 1, exportSchema = false)
abstract class BaseRecordatorios : RoomDatabase() {
    abstract fun recordatorios(): RecordatorioDao

    companion object {
        @Volatile private var instancia: BaseRecordatorios? = null

        fun obtener(context: Context): BaseRecordatorios = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext, BaseRecordatorios::class.java, "recordatorios.db"
            ).build().also { instancia = it }
        }
    }
}
