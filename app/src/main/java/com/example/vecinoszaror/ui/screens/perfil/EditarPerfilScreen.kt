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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.viewmodel.EditarPerfilViewModel

/** Formulario para que cada persona edite su nombre, apellido y teléfono. */
@Composable
fun EditarPerfilScreen(
    viewModel: EditarPerfilViewModel,
    sesion: SesionUsuario,
    onActualizado: (Usuario) -> Unit,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    // Cuando se guarda: se actualiza la sesión con el usuario y se vuelve al perfil
    LaunchedEffect(Unit) {
        viewModel.actualizado.collect { usuario ->
            onActualizado(usuario)
            onVolver()
        }
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
            Text("Editar perfil", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                enabled = !estado.guardando,
                isError = estado.errores.nombre != null,
                supportingText = { estado.errores.nombre?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.apellido,
                onValueChange = viewModel::onApellidoChange,
                label = { Text("Apellido") },
                singleLine = true,
                enabled = !estado.guardando,
                isError = estado.errores.apellido != null,
                supportingText = { estado.errores.apellido?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estado.telefono,
                onValueChange = viewModel::onTelefonoChange,
                label = { Text("Teléfono (opcional)") },
                singleLine = true,
                enabled = !estado.guardando,
                isError = estado.errores.telefono != null,
                supportingText = { estado.errores.telefono?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.guardar(sesion)
                }),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = sesion.usuario.email,
                onValueChange = {},
                label = { Text("Correo") },
                singleLine = true,
                enabled = false,
                supportingText = { Text("Para cambiar tu correo, pídelo a la directiva") },
                modifier = Modifier.fillMaxWidth()
            )

            TextoError(estado.errores.general)

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.guardar(sesion)
                },
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar cambios")
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
