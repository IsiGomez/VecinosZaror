package com.example.vecinoszaror.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.vecinoszaror.navigation.OpcionMenu

@Composable
fun BarraLateral(
    opciones: List<OpcionMenu>,
    rutaActual: String?,
    onSeleccionar: (OpcionMenu) -> Unit
) {
    NavigationRail {
        opciones.forEach { opcion ->
            NavigationRailItem(
                selected = rutaActual == opcion.pantalla.route,
                onClick = { onSeleccionar(opcion) },
                icon = { Icon(imageVector = opcion.icono, contentDescription = opcion.etiqueta) },
                label = { Text(opcion.etiqueta) }
            )
        }
    }
}