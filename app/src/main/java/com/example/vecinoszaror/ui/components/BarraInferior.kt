package com.example.vecinoszaror.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.vecinoszaror.navigation.OpcionMenu

@Composable
fun BarraInferior(
    /** Secciones disponibles para el rol del usuario. */
    opciones: List<OpcionMenu>,
    rutaActual: String?,
    onSeleccionar: (OpcionMenu) -> Unit
) {
    NavigationBar {
        opciones.forEach { opcion ->
            NavigationBarItem(
                selected = rutaActual == opcion.pantalla.route,
                onClick = { onSeleccionar(opcion) },
                icon = { Icon(imageVector = opcion.icono, contentDescription = opcion.etiqueta) },
                label = { Text(opcion.etiqueta) }
            )
        }
    }
}
