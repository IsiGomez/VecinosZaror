package com.example.vecinoszaror.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Fila para filtrar una lista: "Todos" más una opción por cada elemento de [opciones].
 * Sirve para cualquier tipo [T] (recintos, categorías...): [textoDe] dice cómo escribir cada opción.
 * Avisa con [onSeleccionar]; null significa "Todos".
 */
@Composable
fun <T> FilaFiltros(
    opciones: List<T>,
    seleccionada: T?,
    textoDe: (T) -> String,
    onSeleccionar: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = seleccionada == null,
            onClick = { onSeleccionar(null) },
            label = { Text("Todos") }
        )
        opciones.forEach { opcion ->
            FilterChip(
                selected = seleccionada == opcion,
                onClick = { onSeleccionar(opcion) },
                label = { Text(textoDe(opcion)) }
            )
        }
    }
}
