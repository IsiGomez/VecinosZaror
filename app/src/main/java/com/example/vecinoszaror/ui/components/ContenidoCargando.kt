package com.example.vecinoszaror.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Muestra un indicador de carga mientras [cargando] es true y, cuando termina,
 * pasa al [contenido] con un cambio suave (fundido) en vez de un salto brusco.
 */
@Composable
fun ContenidoCargando(
    cargando: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    Crossfade(targetState = cargando, modifier = modifier, label = "cargando") { estaCargando ->
        if (estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            contenido()
        }
    }
}
