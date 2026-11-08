package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.DetalleReservaUiState
import com.example.vecinoszaror.model.EstadoPago
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.EstadoVacio
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.ui.utils.formatearFechaHora
import com.example.vecinoszaror.ui.utils.formatearMonto
import com.example.vecinoszaror.viewmodel.DetalleReservaViewModel

/** Un paso del seguimiento de la reserva. */
private data class PasoSeguimiento(
    val titulo: String,
    val detalle: String?,
    val realizado: Boolean
)

/**
 * Detalle de una reserva: datos, pago y seguimiento.
 * Dibuja el estado que entrega el [DetalleReservaViewModel].
 */
@Composable
fun DetalleReservaScreen(
    viewModel: DetalleReservaViewModel,
    sesion: SesionUsuario,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val mostrarSolicitante = sesion.tienePermiso(Permisos.RESERVA_VER_TODAS)

    ContenidoCargando(cargando = estado.cargando, modifier = Modifier.fillMaxSize()) {
        val detalle = estado.detalle
        if (detalle == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EstadoVacio(
                    icono = Icons.Filled.DateRange,
                    titulo = "Reserva no encontrada",
                    mensaje = "La reserva no existe o no tienes permiso para verla."
                )
                TextButton(onClick = onVolver) { Text("Volver") }
            }
        } else {
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
                    Text("Detalle de la reserva", style = MaterialTheme.typography.headlineSmall)
                    TarjetaDatos(estado, mostrarSolicitante)
                    TarjetaPago(estado)
                    TarjetaSeguimiento(pasosDeSeguimiento(estado))
                    TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

/** Recinto, estado, horario y datos de la solicitud. */
@Composable
private fun TarjetaDatos(estado: DetalleReservaUiState, mostrarSolicitante: Boolean) {
    val detalle = estado.detalle ?: return
    val reserva = detalle.reserva
    Tarjeta(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = detalle.recinto.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                EtiquetaEstado(reserva.estado)
            }

            FilaDato("Desde", formatearFechaHora(reserva.fechaHoraInicio))
            FilaDato("Hasta", formatearFechaHora(reserva.fechaHoraFin))
            if (mostrarSolicitante) {
                FilaDato("Solicitante", "${detalle.solicitante.nombre} ${detalle.solicitante.apellido}")
            }
            FilaDato("Motivo", reserva.motivo)
            FilaDato("Asistentes", reserva.numeroAsistentes.toString())
        }
    }
}

/** Monto total, lo pagado y lo que falta. */
@Composable
private fun TarjetaPago(estado: DetalleReservaUiState) {
    val total = estado.detalle?.reserva?.montoTotal ?: 0L
    Tarjeta(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Pago", style = MaterialTheme.typography.titleMedium)
            FilaDato("Estado", estado.estadoPago.etiqueta())
            if (estado.estadoPago != EstadoPago.NO_APLICA) {
                FilaDato("Total", formatearMonto(total))
                if (estado.estadoPago != EstadoPago.SIN_COSTO) {
                    FilaDato("Pagado", formatearMonto(estado.pagado))
                    FilaDato("Pendiente", formatearMonto(estado.pendiente))
                }
            }
        }
    }
}

/** Línea de tiempo con lo que ha pasado y lo que falta. */
@Composable
private fun TarjetaSeguimiento(pasos: List<PasoSeguimiento>) {
    Tarjeta(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Seguimiento", style = MaterialTheme.typography.titleMedium)
            pasos.forEach { paso ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(12.dp)
                            .background(
                                color = if (paso.realizado) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                    )
                    Column {
                        Text(paso.titulo, style = MaterialTheme.typography.bodyLarge)
                        paso.detalle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            textAlign = TextAlign.End
        )
    }
}


/** Arma los pasos según el estado de la reserva y de su pago. */
private fun pasosDeSeguimiento(estado: DetalleReservaUiState): List<PasoSeguimiento> {
    val reserva = estado.detalle?.reserva ?: return emptyList()
    val gestor = estado.gestionadaPor
    val pasos = mutableListOf(
        PasoSeguimiento("Solicitada", formatearFechaHora(reserva.fechaSolicitud), true)
    )

    pasos += when (reserva.estado) {
        EstadoReserva.PENDIENTE ->
            PasoSeguimiento("En revisión", "Esperando respuesta de la directiva", false)
        EstadoReserva.CONFIRMADA, EstadoReserva.FINALIZADA ->
            PasoSeguimiento("Confirmada", gestor?.let { "Por $it" }, true)
        EstadoReserva.RECHAZADA ->
            PasoSeguimiento("Rechazada", listOfNotNull(gestor?.let { "Por $it" }, reserva.observacion)
                .joinToString(" · ").ifEmpty { null }, true)
        EstadoReserva.CANCELADA ->
            PasoSeguimiento("Cancelada", listOfNotNull(gestor?.let { "Por $it" } ?: "Por el solicitante",
                reserva.observacion).joinToString(" · "), true)
    }

    // El pago solo se muestra mientras la reserva siga su curso
    when (estado.estadoPago) {
        EstadoPago.PENDIENTE, EstadoPago.ABONADO -> pasos += PasoSeguimiento(
            "Pago pendiente", "Faltan ${formatearMonto(estado.pendiente)}", false
        )
        EstadoPago.PAGADO -> pasos += PasoSeguimiento("Pago completo", null, true)
        else -> Unit
    }
    if (reserva.estado == EstadoReserva.FINALIZADA) {
        pasos += PasoSeguimiento("Finalizada", null, true)
    }
    return pasos
}
