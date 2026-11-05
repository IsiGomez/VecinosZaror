package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.viewmodel.RecintoFormularioViewModel

/** Ventana para crear o editar un recinto. No dibuja nada mientras está cerrada. */
@Composable
fun RecintoFormularioDialog(
    viewModel: RecintoFormularioViewModel,
    sesion: SesionUsuario
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    if (!estado.abierto) return

    AlertDialog(
        // No se puede cerrar tocando afuera mientras se está guardando
        onDismissRequest = { if (!estado.enviando) viewModel.cerrar() },
        title = { Text(if (estado.esNuevo) "Nuevo recinto" else "Editar recinto") },
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
                    value = estado.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = { Text("Descripción (opcional)") },
                    enabled = !estado.enviando,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.capacidad,
                    onValueChange = viewModel::onCapacidadChange,
                    label = { Text("Capacidad (personas)") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errores.capacidad != null,
                    supportingText = { estado.errores.capacidad?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = estado.tarifa,
                    onValueChange = viewModel::onTarifaChange,
                    label = { Text("Tarifa base (pesos)") },
                    singleLine = true,
                    enabled = !estado.enviando,
                    isError = estado.errores.tarifa != null,
                    supportingText = {estado.errores.tarifa?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                // Un recinto se puede inactivar al editar (al crear siempre queda activo)
                if (!estado.esNuevo) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Disponible para reservar")
                        Switch(
                            checked = estado.activo,
                            onCheckedChange = viewModel::onActivoChange,
                            enabled = !estado.enviando
                        )
                    }
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
