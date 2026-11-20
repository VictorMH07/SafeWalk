package com.example.safewalk.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.local.EstadoTrayecto
import com.example.safewalk.data.local.Usuario
import com.example.safewalk.ui.SafeWalkViewModel

@Composable
fun PerfilScreen(vm: SafeWalkViewModel, padding: PaddingValues) {
    val usuario by vm.usuario.collectAsState()
    val historial by vm.historial.collectAsState()
    val contactos by vm.contactos.collectAsState()

    val u = usuario
    if (u == null) {
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        FormularioPerfil(
            usuario = u,
            completados = historial.count { it.estado == EstadoTrayecto.COMPLETADO },
            totalContactos = contactos.size,
            padding = padding,
            onGuardar = vm::guardarPerfil
        )
    }
}

@Composable
private fun FormularioPerfil(
    usuario: Usuario,
    completados: Int,
    totalContactos: Int,
    padding: PaddingValues,
    onGuardar: (nombre: String, telefono: String, correo: String) -> Unit
) {
    var nombre by remember(usuario.id) { mutableStateOf(usuario.nombre) }
    var telefono by remember(usuario.id) { mutableStateOf(usuario.telefono) }
    var correo by remember(usuario.id) { mutableStateOf(usuario.correo) }
    var guardo by remember { mutableStateOf(false) }

    val hayCambios = nombre.trim() != usuario.nombre ||
            telefono.trim() != usuario.telefono ||
            correo.trim() != usuario.correo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(88.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EstadisticaCard("Trayectos completados", completados, Modifier.weight(1f))
            EstadisticaCard("Contactos de confianza", totalContactos, Modifier.weight(1f))
        }

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; guardo = false },
            label = { Text("Nombre") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it; guardo = false },
            label = { Text("Teléfono") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; guardo = false },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onGuardar(nombre, telefono, correo)
                guardo = true
            },
            enabled = hayCambios && nombre.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Guardar cambios", style = MaterialTheme.typography.labelLarge)
        }

        if (guardo && !hayCambios) {
            Text(
                "Cambios guardados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun EstadisticaCard(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}