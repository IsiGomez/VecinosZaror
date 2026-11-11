package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.viewmodel.OlvidoClaveViewModel

/** Ventana para pedir una clave nueva a administradores de usuarios. */
@Composable
fun OlvidoClaveDialogo(
    viewModel: OlvidoClaveViewModel,
    onCerrar: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    if (estado.enviado) {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("Solicitud enviada") },
            text = {
                Text(
                    "Si el correo está registrado, avisamos a quienes administran los usuarios " +
                    "para que te asignen una clave nueva. Después podrás cambiarla desde tu perfil."
                )
            },
            confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido") } }
        )
        return
    }

    AlertDialog(
        onDismissRequest = { if (!estado.enviando) onCerrar() },
        title = { Text("¿Olvidaste tu contraseña?") },
        text = {
            Column {
                Text(
                    text = "Escribe tu correo y avisaremos a quienes administran los usuarios para que te asignen una clave nueva.",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = estado.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errorEmail != null,
                    supportingText = { estado.errorEmail?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::solicitar, enabled = !estado.enviando) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Solicitar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar, enabled = !estado.enviando) { Text("Cancelar") }
        }
    )
}
