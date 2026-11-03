package com.example.vecinoszaror.ui.screens.finanzas

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.SelectorComprobante
import com.example.vecinoszaror.ui.screens.reservas.TextoError
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.GastoViewModel

/** Formulario para que Tesorería registre un gasto, con foto. */
@Composable
fun RegistrarGastoScreen(
    viewModel: GastoViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Cuando el ViewModel avisa que el gasto se registró, se vuelve a Finanzas
    LaunchedEffect(Unit) {
        viewModel.gastoRegistrado.collect { onVolver() }
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
            Text("Registrar gasto", style = MaterialTheme.typography.headlineSmall)

            Text("Categoría", style = MaterialTheme.typography.titleSmall)
            CategoriaGasto.entries.forEach { categoria ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = estado.categoria == categoria,
                            onClick = { viewModel.onCategoriaChange(categoria) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = estado.categoria == categoria, onClick = null)
                    Text(categoria.etiqueta(), modifier = Modifier.padding(start = 8.dp))
                }
            }

            OutlinedTextField(
                value = estado.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                enabled = !estado.enviando,
                isError = estado.errores.descripcion != null,
                supportingText = { estado.errores.descripcion?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.monto,
                onValueChange = viewModel::onMontoChange,
                label = { Text("Monto (pesos)") },
                singleLine = true,
                enabled = !estado.enviando,
                isError = estado.errores.monto != null,
                supportingText = { estado.errores.monto?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Boleta o factura (opcional)", style = MaterialTheme.typography.titleSmall)
            SelectorComprobante(
                uri = estado.comprobante,
                onUriChange = viewModel::onComprobanteChange,
                enabled = !estado.enviando,
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
                    Text("Registrar gasto")
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

