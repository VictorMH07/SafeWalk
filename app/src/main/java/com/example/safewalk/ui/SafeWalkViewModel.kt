package com.example.safewalk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safewalk.data.local.Contacto
import com.example.safewalk.data.local.EstadoTrayecto
import com.example.safewalk.data.local.SafeWalkDatabase
import com.example.safewalk.data.local.Trayecto
import com.example.safewalk.data.local.Usuario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SafeWalkViewModel(app: Application) : AndroidViewModel(app) {

    private val db = SafeWalkDatabase.obtener(app)
    private val usuarioActual = db.usuarioDao().observarUsuario().filterNotNull()

    init {
        // La primera vez que se abre la app se crea un usuario por defecto.
        viewModelScope.launch {
            if (db.usuarioDao().contar() == 0) {
                db.usuarioDao().insertar(Usuario(nombre = "Usuario"))
            }
        }
    }

    // ---------- Trayectos ----------

    val historial: StateFlow<List<Trayecto>> = usuarioActual
        .flatMapLatest { db.trayectoDao().observarHistorial(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activo: StateFlow<Trayecto?> = usuarioActual
        .flatMapLatest { db.trayectoDao().observarActivo(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun iniciarTrayecto(origen: String, destino: String, contactoId: Long?) {
        viewModelScope.launch {
            val usuario = usuarioActual.first()
            db.trayectoDao().insertar(
                Trayecto(
                    usuarioId = usuario.id,
                    contactoId = contactoId,
                    origen = origen.trim(),
                    destino = destino.trim()
                )
            )
        }
    }

    fun llegueBien(trayecto: Trayecto) {
        viewModelScope.launch {
            db.trayectoDao().actualizar(
                trayecto.copy(fin = System.currentTimeMillis(), estado = EstadoTrayecto.COMPLETADO)
            )
        }
    }

    // ---------- Contactos de confianza ----------

    val contactos: StateFlow<List<Contacto>> = usuarioActual
        .flatMapLatest { db.contactoDao().observarContactos(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Si [existente] es null crea un contacto nuevo; si no, lo actualiza. */
    fun guardarContacto(existente: Contacto?, nombre: String, telefono: String, parentesco: String) {
        viewModelScope.launch {
            if (existente == null) {
                val usuario = usuarioActual.first()
                db.contactoDao().insertar(
                    Contacto(
                        usuarioId = usuario.id,
                        nombre = nombre.trim(),
                        telefono = telefono.trim(),
                        parentesco = parentesco.trim()
                    )
                )
            } else {
                db.contactoDao().actualizar(
                    existente.copy(
                        nombre = nombre.trim(),
                        telefono = telefono.trim(),
                        parentesco = parentesco.trim()
                    )
                )
            }
        }
    }

    fun alternarFavorito(contacto: Contacto) {
        viewModelScope.launch {
            db.contactoDao().actualizar(contacto.copy(esFavorito = !contacto.esFavorito))
        }
    }

    fun eliminarContacto(contacto: Contacto) {
        viewModelScope.launch { db.contactoDao().eliminar(contacto) }
    }

    // ---------- Perfil ----------

    val usuario: StateFlow<Usuario?> = db.usuarioDao().observarUsuario()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun guardarPerfil(nombre: String, telefono: String, correo: String) {
        viewModelScope.launch {
            val actual = usuarioActual.first()
            db.usuarioDao().actualizar(
                actual.copy(nombre = nombre.trim(), telefono = telefono.trim(), correo = correo.trim())
            )
        }
    }
}