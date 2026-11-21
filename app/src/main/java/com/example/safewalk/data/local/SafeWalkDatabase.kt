package com.example.safewalk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, Contacto::class, Trayecto::class],
    version = 1,
    exportSchema = false
)
abstract class SafeWalkDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun contactoDao(): ContactoDao
    abstract fun trayectoDao(): TrayectoDao

    companion object {
        @Volatile private var instancia: SafeWalkDatabase? = null

        fun obtener(context: Context): SafeWalkDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    SafeWalkDatabase::class.java,
                    "safewalk.db"
                )
                    .build()
                    .also { instancia = it }
            }
    }
}