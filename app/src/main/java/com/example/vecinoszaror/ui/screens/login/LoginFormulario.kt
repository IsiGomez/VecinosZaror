package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.LoginUiState


/** Compo y botón del login */
@Composable
fun LoginFormulario(
    estado: LoginUiState,
    onEmailChange: (String) -> Unit,
    onCLaveChange: (String) -> Unit,
    onIngresar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        /** Correo */
        OutlinedTextField(
            value = estado.email,
            onValueChange = onEmailChange,
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = !estado.cargando,
            isError = estado.errores.email != null,
            supportingText = { estado.errores.email?.let { Text(it) }},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {focusManager.moveFocus(FocusDirection.Down)}
            ),
            modifier = Modifier.fillMaxWidth()
        )

        /** Clave */
        OutlinedTextField(
            value = estado.clave,
            onValueChange = onCLaveChange,
            label = { Text("Clave")},
            singleLine = true,
            enabled = !estado.cargando,
            isError = estado.errores.clave != null,
            supportingText = { estado.errores.clave?.let { Text(it) }},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onIngresar()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        /** Error que no es de un campo */
        estado.errores.general?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        /** Boton */
        Button(
            onClick = {
                focusManager.clearFocus()
                onIngresar()
            },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Ingresar")
            }
        }

        /** Muestra a los usuarios de prueba */
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Usuarios de prueba (clave demo 1234): \n" +
                    "vecino@zaror.test · secretaria@zaror.test\n" +
                    "tesoreria@zaror.test · directiva@zaror.test",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}