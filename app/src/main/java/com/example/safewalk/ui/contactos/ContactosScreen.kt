package com.example.safewalk.ui.contactos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.safewalk.data.local.Contacto
import com.example.safewalk.ui.SafeWalkViewModel

@Composable
fun ContactosScreen(vm: SafeWalkViewModel, padding: PaddingValues) {
    val contactos by vm.contactos.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Contacto?>(null) }
    var porEliminar by remember { mutableStateOf<Contacto?>(null) }

    Box(Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Contactos de confianza", style = MaterialTheme.typography.headlineMedium) }

            if (contactos.isEmpty()) {
                item {
                    Text(
                        "Aún no tienes contactos. Agrega a un familiar o amigo para asociarlo a tus trayectos.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            items(contactos, key = { it.id }) { contacto ->
                ContactoItem(
                    contacto = contacto,
                    onEditar = { editando = contacto; mostrarFormulario = true },
                    onFavorito = { vm.alternarFavorito(contacto) },
                    onEliminar = { porEliminar = contacto }
                )
            }
        }

        FloatingActionButton(
            onClick = { editando = null; mostrarFormulario = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar contacto")
        }
    }

    if (mostrarFormulario) {
        ContactoDialog(
            inicial = editando,
            onCancelar = { mostrarFormulario = false },
            onGuardar = { nombre, telefono, parentesco ->
                vm.guardarContacto(editando, nombre, telefono, parentesco)
                mostrarFormulario = false
            }
        )
    }

    porEliminar?.let { contacto ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("¿Eliminar a ${contacto.nombre}?") },
            text = { Text("Tus trayectos anteriores se conservarán.") },
            confirmButton = {
                TextButton(
                    onClick = { vm.eliminarContacto(contacto); porEliminar = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ContactoItem(
    contacto: Contacto,
    onEditar: () -> Unit,
    onFavorito: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onEditar),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contacto.nombre.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    contacto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val detalle = listOf(contacto.parentesco, contacto.telefono)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                Text(
                    detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onFavorito) {
                Icon(
                    imageVector = if (contacto.esFavorito) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (contacto.esFavorito) "Quitar de favoritos" else "Marcar como favorito",
                    tint = if (contacto.esFavorito) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar contacto",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ContactoDialog(
    inicial: Contacto?,
    onCancelar: () -> Unit,
    onGuardar: (nombre: String, telefono: String, parentesco: String) -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var telefono by remember { mutableStateOf(inicial?.telefono ?: "") }
    var parentesco by remember { mutableStateOf(inicial?.parentesco ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (inicial == null) "Nuevo contacto" else "Editar contacto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentesco,
                    onValueChange = { parentesco = it },
                    label = { Text("Parentesco (opcional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(nombre, telefono, parentesco) },
                enabled = nombre.isNotBlank() && telefono.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}