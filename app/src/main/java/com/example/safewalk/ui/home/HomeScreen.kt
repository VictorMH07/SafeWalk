package com.example.safewalk.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.safewalk.data.local.Contacto
import com.example.safewalk.data.local.EstadoTrayecto
import com.example.safewalk.data.local.Trayecto
import com.example.safewalk.ui.SafeWalkViewModel
import com.example.safewalk.ui.components.TrayectoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PERMISOS_UBICACION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private fun formatear(ms: Long): String =
    SimpleDateFormat("d MMM, h:mm a", Locale("es", "CO")).format(Date(ms))

private fun formatearHora(ms: Long): String =
    SimpleDateFormat("h:mm a", Locale("es", "CO")).format(Date(ms))

/** "21 nov, 2:28 a. m. – 2:45 a. m." (si termina otro día, incluye también la fecha de fin). */
private fun rangoHorario(t: Trayecto): String {
    val inicio = formatear(t.inicio)
    val fin = t.fin ?: return inicio
    val dia = SimpleDateFormat("yyyyMMdd", Locale.US)
    val mismoDia = dia.format(Date(t.inicio)) == dia.format(Date(fin))
    return "$inicio – ${if (mismoDia) formatearHora(fin) else formatear(fin)}"
}

@Composable
fun HomeScreen(vm: SafeWalkViewModel, padding: PaddingValues) {
    val historial by vm.historial.collectAsState()
    val activo by vm.activo.collectAsState()
    val contactos by vm.contactos.collectAsState()
    val ubicacion by vm.ubicacion.collectAsState()
    val recorrido by vm.recorrido.collectAsState()

    val context = LocalContext.current
    var tienePermiso by remember {
        mutableStateOf(
            PERMISOS_UBICACION.any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado -> tienePermiso = resultado.values.any { it } }

    // Mientras haya un trayecto activo y permiso, se sigue la ubicación; si no, se detiene.
    val hayActivo = activo != null
    LaunchedEffect(hayActivo, tienePermiso) {
        if (hayActivo && tienePermiso) vm.iniciarSeguimiento() else vm.detenerSeguimiento()
    }

    val trayectoActivo = activo
    if (trayectoActivo != null) {
        TrayectoActivoScreen(
            trayecto = trayectoActivo,
            contacto = contactos.firstOrNull { it.id == trayectoActivo.contactoId },
            ubicacion = ubicacion,
            recorrido = recorrido,
            tienePermiso = tienePermiso,
            onPermitir = { pedirPermiso.launch(PERMISOS_UBICACION) },
            onLlegueBien = { vm.llegueBien(trayectoActivo) },
            onCancelar = { vm.cancelarTrayecto(trayectoActivo) },
            padding = padding
        )
        return
    }

    LazyColumn(
        modifier = Modifier.padding(padding).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("SafeWalk", style = MaterialTheme.typography.headlineMedium) }

        item {
            NuevoTrayecto(
                contactos = contactos,
                onIniciar = { origen, destino, contactoId ->
                    vm.iniciarTrayecto(origen, destino, contactoId)
                    if (!tienePermiso) pedirPermiso.launch(PERMISOS_UBICACION)
                }
            )
        }

        item { Text("Historial", style = MaterialTheme.typography.titleLarge) }

        val anteriores = historial.filter { it.estado != EstadoTrayecto.ACTIVO }
        if (anteriores.isEmpty()) {
            item {
                Text(
                    "Aún no tienes trayectos finalizados.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        items(anteriores, key = { it.id }) { t: Trayecto ->
            TrayectoCard(
                origen = t.origen,
                destino = t.destino,
                fechaHora = rangoHorario(t),
                estado = t.estado
            )
        }
    }
}

@Composable
private fun NuevoTrayecto(
    contactos: List<Contacto>,
    onIniciar: (origen: String, destino: String, contactoId: Long?) -> Unit
) {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var elegido by remember { mutableStateOf<Long?>(null) }

    val iguales = origen.isNotBlank() && origen.trim().equals(destino.trim(), ignoreCase = true)
    val puedeIniciar = origen.isNotBlank() && destino.isNotBlank() && !iguales

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Nuevo trayecto", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = origen,
                onValueChange = { origen = it },
                label = { Text("Origen") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it },
                label = { Text("Destino") },
                singleLine = true,
                isError = iguales,
                supportingText = {
                    if (iguales) Text("El origen y el destino no pueden ser iguales")
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Contacto de confianza (opcional)", style = MaterialTheme.typography.labelMedium)
            if (contactos.isEmpty()) {
                Text(
                    "Agrega contactos en la pestaña Contactos para asociarlos a tus trayectos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(contactos, key = { it.id }) { contacto ->
                        FilterChip(
                            selected = elegido == contacto.id,
                            onClick = { elegido = if (elegido == contacto.id) null else contacto.id },
                            label = { Text(contacto.nombre) }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onIniciar(origen, destino, elegido)
                    origen = ""
                    destino = ""
                    elegido = null
                },
                enabled = puedeIniciar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Iniciar trayecto", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}