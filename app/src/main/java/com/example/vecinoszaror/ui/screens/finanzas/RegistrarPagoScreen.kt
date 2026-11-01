package com.example.vecinoszaror.ui.screens.finanzas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.MetodoPago
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.SelectorComprobante
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.ui.utils.formatearFechaHora
import com.example.vecinoszaror.ui.utils.formatearMonto
import com.example.vecinoszaror.viewmodel.PagoViewModel

/** Formulario para registrar pago de una reserva, con foto del comprobante. */
@Composable
fun RegistrarPagoScreen(
    viewModel: PagoViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Cuando el ViewModel avisa que el pago se registró, se vuelve a Finanzas
    LaunchedEffect(Unit) {
        viewModel.pagoRegistrado.collect { onVolver() }
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
            Text("Registrar pago", style = MaterialTheme.typography.headlineSmall)

            Text("Reserva", style = MaterialTheme.typography.titleSmall)
            if (estado.reservas.isEmpty()) {
                Text(
                    text = "No hay reservas vigentes para registrar pagos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            estado.reservas.forEach { detalle ->
                val reserva = detalle.reserva
                val seleccionada = estado.reservaId == reserva.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = seleccionada,
                            onClick = { viewModel.onReservaChange(reserva.id) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = seleccionada, onClick = null)
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "${detalle.recinto.nombre} · ${detalle.solicitante.nombre} ${detalle.solicitante.apellido}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${formatearFechaHora(reserva.fechaHoraInicio)} · ${formatearMonto(reserva.montoTotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            TextoError(estado.errores.reserva)

            OutlinedTextField(
                value = estado.monto,
                onValueChange = viewModel::onMontoChange,
                label = { Text("Monto (pesos)") },
                singleLine = true,
                enabled = !estado.enviando,
                isError = estado.errores.monto != null,
                supportingText = { estado.errores.monto?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Método de pago", style = MaterialTheme.typography.titleSmall)
            MetodoPago.entries.forEach { metodo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = estado.metodo == metodo,
                            onClick = { viewModel.onMetodoChange(metodo) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = estado.metodo == metodo, onClick = null)
                    Text(metodo.etiqueta(), modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(
                value = estado.observacion,
                onValueChange = viewModel::onObservacionChange,
                label = { Text("Observación (opcional)") },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Comprobante (opcional)", style = MaterialTheme.typography.titleSmall)
            SelectorComprobante(
                uri = estado.comprobante,
                onUriChange = viewModel::onComprobanteChange,
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            )

            TextoError(estado.errores.general)

            Button(
                onClick = { viewModel.enviar(sesion) },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Registrar pago")
                }
            }
            TextButton(
                onClick = {
                    viewModel.reiniciar()
                    onVolver()
                },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
