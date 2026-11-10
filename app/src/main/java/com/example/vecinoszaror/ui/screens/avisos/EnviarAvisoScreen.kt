package com.example.vecinoszaror.ui.screens.avisos

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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.Destinatario
import com.example.vecinoszaror.model.EnviarAvisoUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.EnviarAvisoViewModel

/** Formulario para enviar un aviso. */
@Composable
fun EnviarAvisoScreen(
    viewModel: EnviarAvisoViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.reiniciar()
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
            Text("Enviar aviso", style = MaterialTheme.typography.headlineSmall)

            val enviadoA = estado.enviadoA
            if (enviadoA != null) {
                // Ya se envió: se confirma en vez de mostrar el formulario
                ConfirmacionEnvio(
                    enviadoA = enviadoA,
                    onEnviarOtro = viewModel::reiniciar,
                    onVolver = onVolver
                )
            } else {
                Formulario(estado = estado, viewModel = viewModel, sesion = sesion, onVolver = onVolver)
            }
        }
    }
}

@Composable
private fun Formulario(
    estado: EnviarAvisoUiState,
    viewModel: EnviarAvisoViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    Text("¿A quién se envía?", style = MaterialTheme.typography.titleSmall)
    Destinatario.entries.forEach { destinatario ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = estado.destinatario == destinatario,
                    onClick = { viewModel.onDestinatarioChange(destinatario) },
                    enabled = !estado.enviando,
                    role = Role.RadioButton
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = estado.destinatario == destinatario, onClick = null)
            Text(destinatario.etiqueta(), modifier = Modifier.padding(start = 8.dp))
        }
    }

    OutlinedTextField(
        value = estado.titulo,
        onValueChange = viewModel::onTituloChange,
        label = { Text("Título") },
        singleLine = true,
        enabled = !estado.enviando,
        isError = estado.errores.titulo != null,
        supportingText = {
            Text(estado.errores.titulo ?: "${estado.titulo.length}/${EnviarAvisoUiState.MAX_TITULO}")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = estado.mensaje,
        onValueChange = viewModel::onMensajeChange,
        label = { Text("Mensaje") },
        minLines = 4,
        enabled = !estado.enviando,
        isError = estado.errores.mensaje != null,
        supportingText = {
            Text(estado.errores.mensaje ?: "${estado.mensaje.length}/${EnviarAvisoUiState.MAX_MENSAJE}")
        },
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
            Text("Enviar aviso")
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

/** Mensaje que reemplaza al formulario cuando el aviso ya salió. */
@Composable
private fun ConfirmacionEnvio(enviadoA: Int, onEnviarOtro: () -> Unit, onVolver: () -> Unit) {
    Tarjeta(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Aviso enviado", style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (enviadoA == 1) "Lo recibió 1 persona." else "Lo recibieron $enviadoA personas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
        Text("Volver a avisos")
    }
    OutlinedButton(onClick = onEnviarOtro, modifier = Modifier.fillMaxWidth()) {
        Text("Enviar otro aviso")
    }
}
