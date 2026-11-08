package com.example.vecinoszaror.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Etiqueta redonda. */
@Composable
fun EtiquetaActivo(activo: Boolean, modifier: Modifier = Modifier) {
    val fondo = if (activo) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.errorContainer
    val texto = if (activo) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
    Surface(shape = CircleShape, color = fondo, modifier = modifier) {
        Text(
            text = if (activo) "Activo" else "Inactivo",
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}