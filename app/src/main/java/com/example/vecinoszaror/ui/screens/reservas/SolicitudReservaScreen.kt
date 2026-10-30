package com.example.vecinoszaror.ui.screens.reservas

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.utils.formatearFechaUtc
import com.example.vecinoszaror.ui.utils.formatearHora
import com.example.vecinoszaror.ui.utils.formatearMonto
import com.example.vecinoszaror.viewmodel.SolicitudReservaViewModel

/** Ventana emergente está abierta (calendario o reloj). */
private enum class Ventana { NINGUNA, FECHA, HORA_INICIO, HORA_FIN }

/**
 * Formulario para solicitar el arriendo de un recinto.
 * Las validaciones y el envío están en [SolicitudReservaViewModel].
 */
@Composable
fun SolicitudReservaScreen(
    viewModel: SolicitudReservaViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var ventana by rememberSaveable { mutableStateOf(Ventana.NINGUNA) }

    /** Cuando el ViewModel avisa que la reserva se creó, se vuelve a la lista */
    LaunchedEffect(Unit) {
        viewModel.reservaCreada.collect { onVolver() }
    }

    when (ventana) {
        Ventana.FECHA -> DialogoFecha(
            onAceptar = { fecha ->
                viewModel.onFechaChange(fecha)
                ventana = Ventana.NINGUNA
            },
            onCancelar = { ventana = Ventana.NINGUNA }
        )
        Ventana.HORA_INICIO -> DialogoHora(
            titulo = "Hora de inicio",
            onAceptar = { minutos ->
                viewModel.onHoraInicioChange(minutos)
                ventana = Ventana.NINGUNA
            },
            onCancelar = { ventana = Ventana.NINGUNA }
        )
        Ventana.HORA_FIN -> DialogoHora(
            titulo = "Hora de término",
            onAceptar = { minutos ->
                viewModel.onHoraFinChange(minutos)
                ventana = Ventana.NINGUNA
            },
            onCancelar = { ventana = Ventana.NINGUNA }
        )
        Ventana.NINGUNA -> Unit
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
            Text("Solicitar reserva", style = MaterialTheme.typography.headlineSmall)

            Text("Recinto", style = MaterialTheme.typography.titleSmall)
            estado.recintos.forEach { recinto ->
                val seleccionado = estado.recintoId == recinto.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = seleccionado,
                            onClick = { viewModel.onRecintoChange(recinto.id) },
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
            TextoError(estado.errores.recinto)

            CampoSelector(
                etiqueta = "Fecha",
                valor = estado.fecha?.let { formatearFechaUtc(it) },
                error = estado.errores.fecha,
                onClick = { ventana = Ventana.FECHA }
            )
            CampoSelector(
                etiqueta = "Hora de inicio",
                valor = estado.horaInicioMinutos?.let { formatearHora(it) },
                error = estado.errores.horaInicio,
                onClick = { ventana = Ventana.HORA_INICIO }
            )
            CampoSelector(
                etiqueta = "Hora de término",
                valor = estado.horaFinMinutos?.let { formatearHora(it) },
                error = estado.errores.horaFin,
                onClick = { ventana = Ventana.HORA_FIN }
            )

            OutlinedTextField(
                value = estado.motivo,
                onValueChange = viewModel::onMotivoChange,
                label = { Text("Motivo del arriendo") },
                enabled = !estado.enviando,
                isError = estado.errores.motivo != null,
                supportingText = { estado.errores.motivo?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.asistentes,
                onValueChange = viewModel::onAsistentesChange,
                label = { Text("Número de asistentes") },
                singleLine = true,
                enabled = !estado.enviando,
                isError = estado.errores.asistentes != null,
                supportingText = { estado.errores.asistentes?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            estado.errores.general?.let { TextoError(it) }

            Button(
                onClick = { viewModel.enviar(sesion) },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Solicitar reserva")
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

/** Botón que muestra el valor elegido (o "Elegir") y abre una ventana. */
@Composable
private fun CampoSelector(
    etiqueta: String,
    valor: String?,
    error: String?,
    onClick: () -> Unit
) {
    Column {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text("$etiqueta: ${valor ?: "Elegir"}")
        }
        TextoError(error)
    }
}

@Composable
private fun TextoError(mensaje: String?) {
    if (mensaje != null) {
        Text(
            text = mensaje,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFecha(
    onAceptar: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    val calendario = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = { calendario.selectedDateMillis?.let(onAceptar) ?: onCancelar() }
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    ) {
        DatePicker(state = calendario)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoHora(
    titulo: String,
    onAceptar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    val reloj = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { TimePicker(state = reloj) },
        confirmButton = {
            // Se guarda como minutos desde la medianoche
            TextButton(onClick = { onAceptar(reloj.hour * 60 + reloj.minute) }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}