package com.example.safewalk.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    val historial: StateFlow<List<Trayecto>> = usuarioActual
        .flatMapLatest { db.trayectoDao().observarHistorial(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activo: StateFlow<Trayecto?> = usuarioActual
        .flatMapLatest { db.trayectoDao().observarActivo(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun iniciarTrayecto(origen: String, destino: String) {
        viewModelScope.launch {
            val usuario = usuarioActual.first()
            db.trayectoDao().insertar(
                Trayecto(usuarioId = usuario.id, origen = origen.trim(), destino = destino.trim())
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
}