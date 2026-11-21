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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private fun fechaCorta(ms: Long): String =
    SimpleDateFormat("d MMM, h:mm a", Locale("es", "CO")).format(Date(ms))

private fun horaCompleta(ms: Long): String =
    SimpleDateFormat("h:mm:ss a", Locale("es", "CO")).format(Date(ms))

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
    padding: PaddingValues
) {
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
                puntos = recorrido.size,
                tienePermiso = tienePermiso,
                onPermitir = onPermitir
            )
            BotonLlegueBien(onClick = onLlegueBien)
        }
    }
}

@Composable
private fun InfoTrayecto(
    contacto: Contacto?,
    ubicacion: Location?,
    puntos: Int,
    tienePermiso: Boolean,
    onPermitir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (contacto != null) {
                Text("Contacto de confianza: ${contacto.nombre}", style = MaterialTheme.typography.bodyMedium)
            }
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
                    "Precisión ±${ubicacion.accuracy.roundToInt()} m · ${horaCompleta(ubicacion.time)} · $puntos puntos",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}