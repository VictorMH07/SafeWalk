package com.example.safewalk.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.local.EstadoTrayecto
import com.example.safewalk.data.local.Trayecto
import com.example.safewalk.ui.SafeWalkViewModel
import com.example.safewalk.ui.components.BotonLlegueBien
import com.example.safewalk.ui.components.TrayectoCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatear(ms: Long): String =
    SimpleDateFormat("d MMM, h:mm a", Locale("es", "CO")).format(Date(ms))

@Composable
fun HomeScreen(vm: SafeWalkViewModel) {
    val historial by vm.historial.collectAsState()
    val activo by vm.activo.collectAsState()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("SafeWalk", style = MaterialTheme.typography.headlineMedium) }

            item {
                val trayecto = activo
                if (trayecto == null) {
                    NuevoTrayecto(onIniciar = vm::iniciarTrayecto)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Trayecto en curso", style = MaterialTheme.typography.titleLarge)
                        TrayectoCard(
                            origen = trayecto.origen,
                            destino = trayecto.destino,
                            fechaHora = formatear(trayecto.inicio),
                            estado = EstadoTrayecto.ACTIVO
                        )
                        BotonLlegueBien(onClick = { vm.llegueBien(trayecto) })
                    }
                }
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
                    fechaHora = formatear(t.inicio),
                    estado = t.estado
                )
            }
        }
    }
}

@Composable
private fun NuevoTrayecto(onIniciar: (String, String) -> Unit) {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }

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
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it },
                label = { Text("Destino") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onIniciar(origen, destino)
                    origen = ""
                    destino = ""
                },
                enabled = origen.isNotBlank() && destino.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Iniciar trayecto", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}