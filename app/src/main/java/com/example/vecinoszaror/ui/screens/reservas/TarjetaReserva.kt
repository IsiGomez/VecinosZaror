package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.ReservaConDetalle
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.style.VecinosZarorTheme
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.ui.utils.formatearFechaHora
import com.example.vecinoszaror.ui.utils.formatearMonto

/** Solo dibuja, no sabe de ViewModels */
@Composable
fun TarjetaReserva(
    detalle: ReservaConDetalle,
    mostrarSolicitante: Boolean,
    modifier: Modifier = Modifier
) {
    val reserva = detalle.reserva

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = detalle.recinto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstado(reserva.estado)
            }
            Text(
                text = "${formatearFechaHora(reserva.fechaHoraInicio)} / ${formatearFechaHora(reserva.fechaHoraFin)}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (mostrarSolicitante) {
                Text(
                    text = "Solicitante: ${detalle.solicitante.nombre} ${detalle.solicitante.apellido}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(text = reserva.motivo, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${reserva.numeroAsistentes} asistentes · ${formatearMonto(reserva.montoTotal)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/** Color segun estado */
@Composable
private fun EtiquetaEstado(estado: EstadoReserva) {
    val (fondo, texto) = when (estado) {
        EstadoReserva.PENDIENTE ->
            MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstadoReserva.CONFIRMADA ->
            MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EstadoReserva.RECHAZADA ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        EstadoReserva.CANCELADA ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        EstadoReserva.FINALIZADA ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(shape = RoundedCornerShape(50), color = fondo) {
        Text(
            text = estado.etiqueta(),
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaReservaPreview() {
    val ahora = System.currentTimeMillis()
    val detalle = ReservaConDetalle(
        reserva = Reserva(
            usuarioId = 1, recintoId = 1,
            fechaHoraInicio = ahora, fechaHoraFin = ahora + 4 * 3_600_000L,
            motivo = "Cumpleaños familiar", numeroAsistentes = 40,
            estado = EstadoReserva.PENDIENTE, montoTotal = 45000
        ),
        solicitante = Usuario(
            nombre = "Ana", apellido = "Pérez", email = "ana@zaror.test", claveHash = "", rolId = 1
        ),
        recinto = Recinto(
            nombre = "Salón Multiuso", descripcion = "", capacidadPersonas = 80, tarifaBase = 45000
        )
    )
    VecinosZarorTheme { TarjetaReserva(detalle, mostrarSolicitante = true) }
}