package com.example.vecinoszaror.ui.screens.finanzas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.viewmodel.FinanzasViewModel

/** Resumen financiero con el detalle de los pagos recibidos y los gastos. */
@Composable
fun FinanzasScreen(
    viewModel: FinanzasViewModel,
    sesion: SesionUsuario,
    onRegistrarPago: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    /** Avisa al ViewModel quién es el usuario */
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    if (estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 700.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Finanzas", style = MaterialTheme.typography.headlineSmall) }

            item {
                TarjetaResumen(
                    totalIngresos = estado.totalIngresos,
                    totalGastos = estado.totalGastos,
                    saldo = estado.saldo
                )
            }

            if (estado.puedeRegistrarPago) {
                item { Button(onClick = onRegistrarPago) { Text("Registrar pago") } }
            }

            item { Text("Pagos recibidos", style = MaterialTheme.typography.titleMedium) }
            if (estado.pagos.isEmpty()) {
                item { TextoVacio("Aún no hay pagos registrados.") }
            } else {
                /** key: identifica cada tarjeta para que Compose no las redibuje */
                items(estado.pagos, key = { "pago-${it.id}" }) { pago ->
                    FilaPago(pago)
                    HorizontalDivider()
                }
            }

            item { Text("Gastos", style = MaterialTheme.typography.titleMedium) }
            if (estado.gastos.isEmpty()) {
                item { TextoVacio("Aún no hay gastos registrados.") }
            } else {
                items(estado.gastos, key = { "gasto-${it.id}" }) { gasto ->
                    FilaGasto(gasto)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun TextoVacio(mensaje: String) {
    Text(
        text = mensaje,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}