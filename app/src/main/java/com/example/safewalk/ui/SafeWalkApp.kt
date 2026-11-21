package com.example.safewalk.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.safewalk.ui.contactos.ContactosScreen
import com.example.safewalk.ui.home.HomeScreen
import com.example.safewalk.ui.perfil.PerfilScreen

private enum class Seccion(val titulo: String, val icono: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    CONTACTOS("Contactos", Icons.Filled.People),
    PERFIL("Perfil", Icons.Filled.Person)
}

@Composable
fun SafeWalkApp(vm: SafeWalkViewModel) {
    var seccion by rememberSaveable { mutableStateOf(Seccion.INICIO) }

    Scaffold(
        bottomBar = {
            NavigationBar (containerColor = MaterialTheme.colorScheme.surface) {
                Seccion.entries.forEach { item ->
                    NavigationBarItem(
                        selected = seccion == item,
                        onClick = { seccion = item },
                        icon = { Icon(item.icono, contentDescription = item.titulo) },
                        label = { Text(item.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (seccion) {
            Seccion.INICIO -> HomeScreen(vm, padding)
            Seccion.CONTACTOS -> ContactosScreen(vm, padding)
            Seccion.PERFIL -> PerfilScreen(vm, padding)
        }
    }
}