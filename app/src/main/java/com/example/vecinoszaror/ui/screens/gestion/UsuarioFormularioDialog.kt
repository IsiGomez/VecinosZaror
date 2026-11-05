package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.UsuarioFormularioViewModel

/** Ventana para crear o editar un usuario. No dibuja nada mientras está cerrada. */
@Composable
fun UsuarioFormularioDialog(
    viewModel: UsuarioFormularioViewModel,
    sesion: SesionUsuario
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    if (!estado.abierto) return

    AlertDialog(
        // No se puede cerrar tocando afuera mientras se está guardando
        onDismissRequest = { if (!estado.enviando) viewModel.cerrar() },
        title = { Text(if (estado.esNuevo) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errores.nombre != null,
                    supportingText = { estado.errores.nombre?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.apellido,
                    onValueChange = viewModel::onApellidoChange,
                    label = { Text("Apellido") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errores.apellido != null,
                    supportingText = { estado.errores.apellido?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Correo") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errores.email != null,
                    supportingText = { estado.errores.email?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.telefono,
                    onValueChange = viewModel::onTelefonoChange,
                    label = { Text("Teléfono (opcional)") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text(if (estado.esNuevo) "Clave" else "Nueva clave (vacío = no cambiar)") },
                    singleLine = true,
                    // La clave se muestra con puntos
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !estado.enviando,
                    isError = estado.errores.clave != null,
                    supportingText = { estado.errores.clave?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                // Una opción por cada rol
                Text("Rol", style = MaterialTheme.typography.titleSmall)
                estado.roles.forEach { rol ->
                    val puedeCambiar = !estado.enviando && !estado.esPropio
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = estado.rolId == rol.id,
                                enabled = puedeCambiar,
                                onClick = { viewModel.onRolChange(rol.id) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = estado.rolId == rol.id, onClick = null, enabled = puedeCambiar)
                        Text(rol.etiqueta(), modifier = Modifier.padding(start = 8.dp))
                    }
                }
                TextoError(estado.errores.rol)

                // La cuenta se puede desactivar al editar (no al crear)
                if (!estado.esNuevo) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cuenta activa")
                        Switch(
                            checked = estado.activo,
                            onCheckedChange = viewModel::onActivoChange,
                            enabled = !estado.enviando && !estado.esPropio
                        )
                    }
                }
                if (estado.esPropio) {
                    Text(
                        text = "Es tu propia cuenta: no puedes cambiar tu rol ni desactivarla.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextoError(estado.errores.general)
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.enviar(sesion) }, enabled = !estado.enviando) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = viewModel::cerrar, enabled = !estado.enviando) { Text("Cancelar") }
        }
    )
}
