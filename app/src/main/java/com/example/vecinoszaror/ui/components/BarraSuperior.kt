package com.example.vecinoszaror.ui.components

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    nombre: String,
    rol: String,
    onVerPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val vista = LocalView.current
    val temaOscuro = isSystemInDarkTheme()
    DisposableEffect(vista, temaOscuro) {
        val controlador = WindowCompat.getInsetsController((vista.context as Activity).window, vista)
        controlador.isAppearanceLightStatusBars = false
        onDispose { controlador.isAppearanceLightStatusBars = !temaOscuro }
    }

    val colores = MaterialTheme.colorScheme

    TopAppBar(
        title = {
            Column {
                Text(text = "Hola, $nombre", style = MaterialTheme.typography.titleMedium)
                Text(text = rol, style = MaterialTheme.typography.bodySmall)
            }
        },
        actions = {
            IconButton(
                onClick = onVerPerfil,
                colors = IconButtonDefaults.iconButtonColors(contentColor = colores.onSecondary)
            ) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Mi perfil")
            }
            TextButton(
                onClick = onCerrarSesion,
                colors = ButtonDefaults.textButtonColors(contentColor = colores.onSecondary)
            ) {
                Text("Cerrar sesión")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colores.secondary,
            titleContentColor = colores.onSecondary
        )
    )
}
