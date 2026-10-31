package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.ui.utils.formatearMonto

/** La usan el formulario y la disponibilidad. */
@Composable
fun SelectorRecinto(
    recintos: List<Recinto>,
    recintoIdSeleccionado: Long?,
    onSeleccionar: (Long) -> Unit
) {
    recintos.forEach { recinto ->
        val seleccionado = recintoIdSeleccionado == recinto.id
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = seleccionado,
                    onClick = { onSeleccionar(recinto.id) },
                    role = Role.RadioButton
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = seleccionado, onClick = null)
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(recinto.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Capacidad ${recinto.capacidadPersonas} personas · ${formatearMonto(recinto.tarifaBase)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
