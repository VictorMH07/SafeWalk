package com.example.safewalk.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.location.Punto
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

// Posición inicial del mapa mientras llega la primera ubicación (Ipiales, Nariño).
private val POSICION_INICIAL = LatLng(0.8303, -77.6447)

/** Mapa de Google con la ruta recorrida y un marcador en la posición actual. */
@Composable
fun MapaRecorrido(recorrido: List<Punto>, modifier: Modifier = Modifier) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(POSICION_INICIAL, 13f)
    }
    val scope = rememberCoroutineScope()
    val puntos = recorrido.map { LatLng(it.latitud, it.longitud) }
    val ultimo = puntos.lastOrNull()
    var centrado by remember { mutableStateOf(false) }

    // La primera ubicación salta directo al punto con zoom de calle; después el mapa lo sigue.
    LaunchedEffect(ultimo) {
        val actual = ultimo ?: return@LaunchedEffect
        if (!centrado) {
            centrado = true
            camara.move(CameraUpdateFactory.newLatLngZoom(actual, 17f))
        } else {
            camara.animate(CameraUpdateFactory.newLatLng(actual))
        }
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camara,
            uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
        ) {
            if (puntos.size > 1) {
                Polyline(points = puntos, color = MaterialTheme.colorScheme.primary, width = 14f)
            }
            if (ultimo != null) {
                Marker(
                    state = remember(ultimo) { MarkerState(position = ultimo) },
                    title = "Tú"
                )
            }
        }

        SmallFloatingActionButton(
            onClick = {
                ultimo?.let {
                    scope.launch { camara.animate(CameraUpdateFactory.newLatLngZoom(it, 17f)) }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "Centrar en mi ubicación")
        }
    }
}