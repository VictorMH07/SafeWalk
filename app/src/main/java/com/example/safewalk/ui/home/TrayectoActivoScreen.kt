package com.example.safewalk.ui.home

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.local.Contacto
import com.example.safewalk.data.local.EstadoTrayecto
import com.example.safewalk.data.local.Trayecto
import com.example.safewalk.data.location.Punto
import com.example.safewalk.ui.components.BotonLlegueBien
import com.example.safewalk.ui.components.MapaRecorrido
import com.example.safewalk.ui.components.TrayectoCard
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private fun fechaCorta(ms: Long): String =
    SimpleDateFormat("d MMM, h:mm a", Locale("es", "CO")).format(Date(ms))

/** Suma la distancia entre puntos consecutivos; ignora saltos menores a 3 m (ruido del GPS). */
private fun distanciaTotal(puntos: List<Punto>): Float {
    var total = 0f
    val resultado = FloatArray(1)
    for (i in 1 until puntos.size) {
        val a = puntos[i - 1]
        val b = puntos[i]
        Location.distanceBetween(a.latitud, a.longitud, b.latitud, b.longitud, resultado)
        if (resultado[0] >= 3f) total += resultado[0]
    }
    return total
}

private fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) "${metros.roundToInt()} m"
    else String.format(Locale("es", "CO"), "%.1f km", metros / 1000f)

private fun formatearDuracion(ms: Long): String {
    val minutos = (ms / 60_000).coerceAtLeast(0)
    return when {
        minutos < 1 -> "menos de 1 min"
        minutos < 60 -> "$minutos min"
        else -> "${minutos / 60} h ${minutos % 60} min"
    }
}

/** Pantalla del trayecto en curso: mapa a pantalla completa y tarjetas abajo. */
@Composable
fun TrayectoActivoScreen(
    trayecto: Trayecto,
    contacto: Contacto?,
    ubicacion: Location?,
    recorrido: List<Punto>,
    tienePermiso: Boolean,
    onPermitir: () -> Unit,
    onLlegueBien: () -> Unit,
    onCancelar: () -> Unit,
    padding: PaddingValues
) {
    var confirmando by remember { mutableStateOf(false) }
    // Reloj que se actualiza cada 15 segundos para mostrar el tiempo transcurrido.
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            ahora = System.currentTimeMillis()
        }
    }

    val metros = remember(recorrido) { distanciaTotal(recorrido) }

    Box(Modifier.fillMaxSize().padding(padding)) {
        MapaRecorrido(recorrido = recorrido, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TrayectoCard(
                origen = trayecto.origen,
                destino = trayecto.destino,
                fechaHora = fechaCorta(trayecto.inicio),
                estado = EstadoTrayecto.ACTIVO
            )
            InfoTrayecto(
                contacto = contacto,
                ubicacion = ubicacion,
                duracion = formatearDuracion(ahora - trayecto.inicio),
                distancia = if (recorrido.size > 1) formatearDistancia(metros) else null,
                tienePermiso = tienePermiso,
                onPermitir = onPermitir
            )
            BotonLlegueBien(onClick = onLlegueBien)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                TextButton(
                    onClick = { confirmando = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Cancelar trayecto") }
            }
        }
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("¿Cancelar el trayecto?") },
            text = { Text("Quedará en el historial como cancelado.") },
            confirmButton = {
                TextButton(
                    onClick = { confirmando = false; onCancelar() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) { Text("Seguir en el trayecto") }
            }
        )
    }
}

@Composable
private fun InfoTrayecto(
    contacto: Contacto?,
    ubicacion: Location?,
    duracion: String,
    distancia: String?,
    tienePermiso: Boolean,
    onPermitir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (contacto != null) {
                Text("Contacto de confianza: ${contacto.nombre}", style = MaterialTheme.typography.bodyMedium)
            }

            val progreso = if (distancia != null) "Lleva $duracion · $distancia recorridos" else "Lleva $duracion"
            Text(progreso, style = MaterialTheme.typography.titleMedium)

            if (!tienePermiso) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Activa la ubicación para ver tu recorrido.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onPermitir) { Text("Permitir") }
                }
            } else if (ubicacion == null) {
                Text("Obteniendo ubicación…", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    "Precisión del GPS: ±${ubicacion.accuracy.roundToInt()} m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}