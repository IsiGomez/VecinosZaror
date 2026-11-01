package com.example.vecinoszaror.ui.screens.finanzas

import androidx.core.net.toUri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.Gasto
import com.example.vecinoszaror.model.Pago
import com.example.vecinoszaror.ui.components.ImagenInteligente
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.ui.utils.formatearFechaHora
import com.example.vecinoszaror.ui.utils.formatearMonto

/** Un pago recibido: reserva, fecha, metodo de pago y el monto. */
@Composable
fun FilaPago(pago: Pago) {
    val comprobante = pago.comprobanteUri
    ListItem(
        headlineContent = { Text("Pago de la reserva #${pago.reservaId}") },
        supportingContent = { Text("${formatearFechaHora(pago.fechaPago)} · ${pago.metodo.etiqueta()}") },
        leadingContent = if (comprobante != null) {
            { MiniaturaComprobante(comprobante) }
        } else null,
        trailingContent = { Text(formatearMonto(pago.monto), style = MaterialTheme.typography.titleSmall) }
    )
}

/** Un gasto: descripcion, categoria, fecha y monto. */
@Composable
fun FilaGasto(gasto: Gasto) {
    val comprobante = gasto.comprobanteUri
    ListItem(
        headlineContent = { Text(gasto.descripcion) },
        supportingContent = { Text("${gasto.categoria.etiqueta()} · ${formatearFechaHora(gasto.fechaGasto)}") },
        leadingContent = if (comprobante != null) {
            { MiniaturaComprobante(comprobante) }
        } else null,
        trailingContent = { Text(formatearMonto(gasto.monto), style = MaterialTheme.typography.titleSmall) }
    )
}

/** La base guarda la dirección como texto, aquí se convierte a Uri. */
@Composable
private fun MiniaturaComprobante(direccion: String) {
    ImagenInteligente(
        uri = direccion.toUri(),
        modifier = Modifier.size(48.dp),
        forma = RoundedCornerShape(8.dp),
        descripcion = "Comprobante"
    )
}
