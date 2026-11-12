package com.example.vecinoszaror.ui.screens.informes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.screens.reservas.CampoSelector
import com.example.vecinoszaror.ui.screens.reservas.DialogoFecha
import com.example.vecinoszaror.ui.utils.formatearFechaUtc
import com.example.vecinoszaror.viewmodel.GenerarInformeViewModel

/** Formulario para generar el informe de un período. Queda como borrador. */
@Composable
fun GenerarInformeScreen(
    viewModel: GenerarInformeViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Qué calendario está abierto, rememberSaveable evita que se cierre al rotar
    var eligiendoInicio by rememberSaveable { mutableStateOf(false) }
    var eligiendoFin by rememberSaveable { mutableStateOf(false) }

    // Cuando el ViewModel avisa que el informe se generó, se vuelve a la lista
    LaunchedEffect(Unit) {
        viewModel.informeGenerado.collect { onVolver() }
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
            Text("Generar informe", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Los ingresos, los gastos y el saldo se calculan solos con los movimientos del período.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = estado.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título (ej: Rendición de octubre)") },
                singleLine = true,
                enabled = !estado.enviando,
                isError = estado.errores.titulo != null,
                supportingText = { estado.errores.titulo?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // El calendario entrega el día en UTC, por eso se muestra con formatearFechaUtc
            CampoSelector(
                etiqueta = "Desde",
                valor = estado.inicioUtc?.let { formatearFechaUtc(it) },
                error = estado.errores.inicio,
                onClick = { eligiendoInicio = true }
            )
            CampoSelector(
                etiqueta = "Hasta",
                valor = estado.finUtc?.let { formatearFechaUtc(it) },
                error = estado.errores.fin,
                onClick = { eligiendoFin = true }
            )

            OutlinedTextField(
                value = estado.observaciones,
                onValueChange = viewModel::onObservacionesChange,
                label = { Text("Observaciones (opcional)") },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.enviar(sesion) },
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Generar informe")
                }
            }
            TextButton(
                onClick = onVolver,
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }

    if (eligiendoInicio) {
        DialogoFecha(
            onAceptar = {
                viewModel.onInicioChange(it)
                eligiendoInicio = false
            },
            onCancelar = { eligiendoInicio = false }
        )
    }
    if (eligiendoFin) {
        DialogoFecha(
            onAceptar = {
                viewModel.onFinChange(it)
                eligiendoFin = false
            },
            onCancelar = { eligiendoFin = false }
        )
    }
}
