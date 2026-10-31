package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.viewmodel.ReservasViewModel

/** Acciones que piden confirmación antes de ejecutarse. */
private enum class TipoAccion { RECHAZAR, CANCELAR }

/** La acción que el usuario eligió y la reserva sobre la que se aplica. */
private data class AccionPendiente(val tipo: TipoAccion, val reserva: Reserva)

/**
 * Lista de reservas.
 * Qué reservas cargar lo decide el [ReservasViewModel] según la sesión.
 */
@Composable
fun ReservasScreen(
    viewModel: ReservasViewModel,
    sesion: SesionUsuario,
    onSolicitarReserva: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var accionPendiente by remember { mutableStateOf<AccionPendiente?>(null) }

    val puedeGestionar = sesion.tienePermiso(Permisos.RESERVA_GESTIONAR)
    val puedeCancelarPropias = sesion.tienePermiso(Permisos.RESERVA_CANCELAR_PROPIA)

    /** Avisa al ViewModel quién es el usuario */
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    /** Muestra los mensajes de una sola vez que manda el ViewModel */
    LaunchedEffect(Unit) {
        viewModel.mensajes.collect { mensaje -> snackbarHostState.showSnackbar(mensaje) }
    }

    /** Ventana de confirmación (solo se dibuja si hay una acción pendiente) */
    accionPendiente?.let { pendiente ->
        DialogoConfirmacion(
            accion = pendiente,
            onConfirmar = { motivo ->
                when (pendiente.tipo) {
                    TipoAccion.RECHAZAR -> viewModel.responder(pendiente.reserva, false, motivo, sesion)
                    TipoAccion.CANCELAR -> viewModel.cancelar(pendiente.reserva, motivo, sesion)
                }
                accionPendiente = null
            },
            onCerrar = { accionPendiente = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (estado.puedeVerTodas) "Todas las reservas" else "Mis reservas",
                    style = MaterialTheme.typography.headlineSmall
                )
                if (sesion.tienePermiso(Permisos.RESERVA_SOLICITAR)) {
                    Button(onClick = onSolicitarReserva) { Text("Solicitar") }
                }
            }

            when {
                estado.cargando -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                estado.reservas.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (estado.puedeVerTodas) "Aún no hay reservas registradas."
                               else "Aún no has solicitado reservas.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    /** key: identifica cada tarjeta para que Compose no las redibuje */
                    items(estado.reservas, key = { it.reserva.id }) { detalle ->
                        val reserva = detalle.reserva
                        val puedeCancelar = puedeGestionar ||
                                (puedeCancelarPropias && reserva.usuarioId == sesion.usuario.id)

                        /** Si el rol no puede hacer la acción se pasa null y el botón no aparece */
                        TarjetaReserva(
                            detalle = detalle,
                            mostrarSolicitante = estado.puedeVerTodas,
                            onConfirmar = if (puedeGestionar) {
                                { viewModel.responder(reserva, true, null, sesion) }
                            } else null,
                            onRechazar = if (puedeGestionar) {
                                { accionPendiente = AccionPendiente(TipoAccion.RECHAZAR, reserva) }
                            } else null,
                            onCancelar = if (puedeCancelar) {
                                { accionPendiente = AccionPendiente(TipoAccion.CANCELAR, reserva) }
                            } else null
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

/** Pregunta si de verdad se quiere rechazar o cancelar, y deja escribir un motivo. */
@Composable
private fun DialogoConfirmacion(
    accion: AccionPendiente,
    onConfirmar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    val rechazando = accion.tipo == TipoAccion.RECHAZAR

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (rechazando) "¿Rechazar la solicitud?" else "¿Cancelar la reserva?") },
        text = {
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(motivo) }) {
                Text(if (rechazando) "Rechazar" else "Cancelar reserva")
            }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Volver") } }
    )
}