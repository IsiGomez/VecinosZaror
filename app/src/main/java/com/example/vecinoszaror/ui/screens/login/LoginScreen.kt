package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.viewmodel.AuthViewModel

/**
 * Pantalla de inicio de sesión.
 * Dibuja el estado de [AuthViewModel] y le avisa lo que el usuario escribe.
 * La validación y la consulta a la base están en el ViewModel.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginExitoso: () -> Unit
) {
    val estado by authViewModel.estado.collectAsStateWithLifecycle()
    val sesion by authViewModel.sesion.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    /** Cuando el login resulta bien, la sesión deja de ser null y se avisa para navegar */
    LaunchedEffect(sesion) {
        if (sesion != null) onLoginExitoso()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /** Encabezado */
        Text(
            text = "Junta de Vecinos Población Zaror",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gestión de arriendos y transparencia financiera",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        /** Correo */
        OutlinedTextField(
            value = estado.email,
            onValueChange = authViewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = !estado.cargando,
            isError = estado.errores.email != null,
            supportingText = { estado.errores.email?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        /** Clave */
        OutlinedTextField(
            value = estado.clave,
            onValueChange = authViewModel::onClaveChange,
            label = { Text("Clave") },
            singleLine = true,
            enabled = !estado.cargando,
            isError = estado.errores.clave != null,
            supportingText = { estado.errores.clave?.let { Text(it) } },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    authViewModel.iniciarSesion()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        /** Error que no pertenece a un campo */
        estado.errores.general?.let { mensaje ->
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        /** Muestra un indicador mientras consulta la base de datos */
        Button(
            onClick = {
                focusManager.clearFocus()
                authViewModel.iniciarSesion()
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

        /** Ingresar con los usuarios de prueba */
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Usuarios de prueba (clave demo1234):\n" +
                    "vecino@zaror.test · secretaria@zaror.test\n" +
                    "tesoreria@zaror.test · directiva@zaror.test",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
