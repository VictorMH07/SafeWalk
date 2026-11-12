package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safewalk.ui.components.BotonLlegueBien
import com.example.safewalk.ui.components.EstadoTrayecto
import com.example.safewalk.ui.components.TrayectoCard
import com.example.safewalk.ui.theme.SafeWalkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeWalkTheme {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier.padding(padding).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Mis trayectos", style = MaterialTheme.typography.headlineMedium)
                        TrayectoCard("Universidad de Nariño", "Casa", "Hoy, 6:30 p. m.", EstadoTrayecto.ACTIVO)
                        TrayectoCard("Casa", "Universidad de Nariño", "Ayer, 7:00 a. m.", EstadoTrayecto.COMPLETADO)
                        Spacer(Modifier.weight(1f))
                        BotonLlegueBien(onClick = {})
                    }
                }
            }
        }
    }
}