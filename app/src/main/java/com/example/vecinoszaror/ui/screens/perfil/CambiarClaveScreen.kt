package com.example.vecinoszaror.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.uistate.CambiarClaveUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.viewmodel.CambiarClaveViewModel

/** Formulario para que cada persona cambie su propia clave. */
@Composable
fun CambiarClaveScreen(
    viewModel: CambiarClaveViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

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
            Text("Cambiar contraseña", style = MaterialTheme.typography.headlineSmall)

            if (estado.cambiada) {
                Tarjeta(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Contraseña actualizada", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "La próxima vez que inicies sesión usa tu clave nueva.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al perfil")
                }
            } else {
                CampoClave(
                    valor = estado.actual,
                    onCambio = viewModel::onActualChange,
                    etiqueta = "Clave actual",
                    error = estado.errores.actual,
                    habilitado = !estado.guardando,
                    accion = ImeAction.Next,
                    onSiguiente = { focusManager.moveFocus(FocusDirection.Down) }
                )
                CampoClave(
                    valor = estado.nueva,
                    onCambio = viewModel::onNuevaChange,
                    etiqueta = "Clave nueva",
                    error = estado.errores.nueva,
                    ayuda = "Mínimo ${CambiarClaveUiState.CLAVE_MINIMA} caracteres",
                    habilitado = !estado.guardando,
                    accion = ImeAction.Next,
                    onSiguiente = { focusManager.moveFocus(FocusDirection.Down) }
                )
                CampoClave(
                    valor = estado.confirmacion,
                    onCambio = viewModel::onConfirmacionChange,
                    etiqueta = "Repite la clave nueva",
                    error = estado.errores.confirmacion,
                    habilitado = !estado.guardando,
                    accion = ImeAction.Done,
                    onSiguiente = {
                        focusManager.clearFocus()
                        viewModel.cambiar(sesion)
                    }
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.cambiar(sesion)
                    },
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estado.guardando) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Cambiar contraseña")
                    }
                }
                TextButton(
                    onClick = onVolver,
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/** Oculta lo escrito y muestra el error, si no una ayuda. */
@Composable
private fun CampoClave(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    habilitado: Boolean,
    accion: ImeAction,
    onSiguiente: () -> Unit,
    ayuda: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        enabled = habilitado,
        isError = error != null,
        supportingText = { (error ?: ayuda)?.let { Text(it) } },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = accion),
        keyboardActions = KeyboardActions(onNext = { onSiguiente() }, onDone = { onSiguiente() }),
        modifier = Modifier.fillMaxWidth()
    )
}
