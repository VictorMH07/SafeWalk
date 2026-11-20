package com.example.safewalk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.local.EstadoTrayecto



/** Etiqueta de estado para historial y pantalla de trayecto. */
@Composable
fun EstadoChip(estado: EstadoTrayecto, modifier: Modifier = Modifier) {
    val (texto, fondo, contenido) = when (estado) {
        EstadoTrayecto.ACTIVO -> Triple("Activo", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        EstadoTrayecto.COMPLETADO -> Triple("Completado", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface)
        EstadoTrayecto.CANCELADO -> Triple("Cancelado", MaterialTheme.colorScheme.error.copy(alpha = 0.12f), MaterialTheme.colorScheme.error)
    }
    Surface(modifier = modifier, shape = CircleShape, color = fondo, contentColor = contenido) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Botón grande de "Llegué bien": fácil de tocar caminando. */
@Composable
fun BotonLlegueBien(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(60.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Llegué bien", style = MaterialTheme.typography.labelLarge)
    }
}

/** Tarjeta de trayecto con origen, destino y estado (historial / trayecto activo). */
@Composable
fun TrayectoCard(
    origen: String,
    destino: String,
    fechaHora: String,
    estado: EstadoTrayecto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(fechaHora, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f))
                EstadoChip(estado)
            }
            FilaLugar(Icons.Filled.TripOrigin, origen)
            FilaLugar(Icons.Filled.LocationOn, destino)
        }
    }
}

@Composable
private fun FilaLugar(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}