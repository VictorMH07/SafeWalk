package com.example.safewalk.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Un punto del recorrido. Más adelante se usará para dibujar la ruta en el mapa. */
data class Punto(val latitud: Double, val longitud: Double, val tiempo: Long)

class UbicacionClient(context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Emite la ubicación del dispositivo cada [intervaloMs] milisegundos.
     * Quien la llame debe haber verificado antes el permiso de ubicación.
     */
    @SuppressLint("MissingPermission")
    fun actualizaciones(intervaloMs: Long = 5_000L): Flow<Location> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervaloMs)
            .setMinUpdateIntervalMillis(intervaloMs / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { trySend(it) }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }
}