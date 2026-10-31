package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.ui.utils.formatearFechaUtc
import com.example.vecinoszaror.viewmodel.DisponibilidadViewModel

/**
 * Consulta de disponibilidad
 * Se elige recinto y día, y se ven los horarios ocupados.
 */
@Composable
fun DisponibilidadScreen(
    viewModel: DisponibilidadViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var mostrarCalendario by remember { mutableStateOf(false) }

    if (mostrarCalendario) {
        DialogoFecha(
            onAceptar = { fecha ->
                viewModel.onFechaChange(fecha)
                mostrarCalendario = false
            },
            onCancelar = { mostrarCalendario = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Disponibilidad de recintos", style = MaterialTheme.typography.headlineSmall)

            Text("Recinto", style = MaterialTheme.typography.titleSmall)
            SelectorRecinto(
                recintos = estado.recintos,
                recintoIdSeleccionado = estado.recintoId,
                onSeleccionar = viewModel::onRecintoChange
            )

            CampoSelector(
                etiqueta = "Fecha",
                valor = estado.fecha?.let { formatearFechaUtc(it) },
                error = null,
                onClick = { mostrarCalendario = true }
            )

            when {
                !estado.hayConsulta -> Text(
                    text = "Elige un recinto y una fecha para ver su disponibilidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                estado.cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

                estado.ocupacion.isEmpty() -> Text(
                    text = "Libre todo el día.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                else -> {
                    Text(
                        text = "Horarios ocupados (${estado.ocupacion.size}):",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    estado.ocupacion.forEach { detalle ->
                        TarjetaReserva(detalle, mostrarSolicitante = true)
                    }
                }
            }

            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
