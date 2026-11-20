package com.example.safewalk.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoTrayecto { ACTIVO, COMPLETADO, CANCELADO }

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val telefono: String = "",
    val correo: String = ""
)

@Entity(
    tableName = "contactos",
    foreignKeys = [ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("usuarioId")]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val nombre: String,
    val telefono: String,
    val parentesco: String = "",
    val esFavorito: Boolean = false
)

@Entity(
    tableName = "trayectos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Contacto::class,
            parentColumns = ["id"],
            childColumns = ["contactoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("usuarioId"), Index("contactoId")]
)
data class Trayecto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val contactoId: Long? = null,
    val origen: String,
    val destino: String,
    val inicio: Long = System.currentTimeMillis(),
    val fin: Long? = null,
    val estado: EstadoTrayecto = EstadoTrayecto.ACTIVO
)