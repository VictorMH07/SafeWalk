package com.example.safewalk.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Query("SELECT * FROM usuarios LIMIT 1")
    fun observarUsuario(): Flow<Usuario?>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}

@Dao
interface ContactoDao {
    @Insert
    suspend fun insertar(contacto: Contacto): Long

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Delete
    suspend fun eliminar(contacto: Contacto)

    @Query("SELECT * FROM contactos WHERE usuarioId = :usuarioId ORDER BY esFavorito DESC, nombre")
    fun observarContactos(usuarioId: Long): Flow<List<Contacto>>
}

@Dao
interface TrayectoDao {
    @Insert
    suspend fun insertar(trayecto: Trayecto): Long

    @Update
    suspend fun actualizar(trayecto: Trayecto)

    @Query("SELECT * FROM trayectos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Trayecto?

    @Query("SELECT * FROM trayectos WHERE usuarioId = :usuarioId AND estado = 'ACTIVO' LIMIT 1")
    fun observarActivo(usuarioId: Long): Flow<Trayecto?>

    @Query("SELECT * FROM trayectos WHERE usuarioId = :usuarioId ORDER BY inicio DESC")
    fun observarHistorial(usuarioId: Long): Flow<List<Trayecto>>
}