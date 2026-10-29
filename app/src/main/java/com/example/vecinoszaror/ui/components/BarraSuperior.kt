package com.example.vecinoszaror.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    nombre: String,
    rol: String,
    onCerrarSesion: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(text = "Hola, $nombre", style = MaterialTheme.typography.titleMedium)
                Text(text = rol, style = MaterialTheme.typography.bodySmall)
            }
        },
        actions = {
            TextButton(onClick = onCerrarSesion) {
                Text("Cerrar sesión")
            }
        }
    )
}
