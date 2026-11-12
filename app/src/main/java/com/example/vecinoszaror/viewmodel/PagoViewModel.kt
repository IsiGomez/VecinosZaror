package com.example.vecinoszaror.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.MetodoPago
import com.example.vecinoszaror.model.PagoErrores
import com.example.vecinoszaror.model.PagoUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.ReservaPorPagar
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import com.example.vecinoszaror.repository.ReservaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que Tesorería registra pagos (o abonos) de una reserva. */
class PagoViewModel(
    reservaRepository: ReservaRepository,
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PagoUiState())
    val estado: StateFlow<PagoUiState> = _estado.asStateFlow()

    // Aviso de una sola vez: la pantalla lo usa para volver a Finanzas
    private val _pagoRegistrado = Channel<Unit>(Channel.BUFFERED)
    val pagoRegistrado: Flow<Unit> = _pagoRegistrado.receiveAsFlow()

    init {
        viewModelScope.launch {
            // combine vuelve a calcular la lista cuando cambian las reservas o los pagos
            combine(
                reservaRepository.observarTodas(),
                finanzasRepository.observarPagos()
            ) { reservas, pagos ->
                val pagadoPorReserva = pagos
                    .groupBy { it.reservaId }
                    .mapValues { (_, lista) -> lista.sumOf { it.monto } }

                reservas
                    .filter {
                        it.reserva.estado == EstadoReserva.PENDIENTE || it.reserva.estado == EstadoReserva.CONFIRMADA
                    }
                    .map { ReservaPorPagar(detalle = it, pagado = pagadoPorReserva[it.reserva.id] ?: 0L) }
                    .filter { it.pendiente > 0 }
            }.collect { porPagar ->
                _estado.update { actual ->
                    // Si el recinto filtrado ya se pagaron todas, se quita el filtro
                    val filtroVigente = actual.filtroRecintoId?.takeIf { id ->
                        porPagar.any { it.detalle.recinto.id == id }
                    }
                    // Si ya no está en la lista, se deselecciona
                    val reservaVigente = actual.reservaId?.takeIf { id ->
                        porPagar.any { it.detalle.reserva.id == id }
                    }
                    actual.copy(reservas = porPagar, filtroRecintoId = filtroVigente, reservaId = reservaVigente)
                }
            }
        }
    }

    /** Al cambiar un campo se actualiza su valor y se limpian sus errores */
    fun onReservaChange(id: Long) {
        _estado.update { actual ->
            val elegida = actual.reservas.firstOrNull { it.detalle.reserva.id == id }
            actual.copy(
                reservaId = id,
                monto = elegida?.pendiente?.toString() ?: actual.monto,
                errores = actual.errores.copy(reserva = null, monto = null, general = null)
            )
        }
    }

    /** Filtra las reservas por recinto (null = todos). */
    fun onFiltroRecintoChange(recintoId: Long?) {
        _estado.update { actual ->
            // Si la reserva elegida no pertenece al recinto nuevo, se deselecciona
            val sigueVisible = recintoId == null ||
                    actual.reservaSeleccionada?.detalle?.recinto?.id == recintoId
            actual.copy(
                filtroRecintoId = recintoId,
                reservaId = if (sigueVisible) actual.reservaId else null
            )
        }
    }

    fun onMontoChange(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(monto = soloNumeros, errores = it.errores.copy(monto = null, general = null)) }
    }

    fun onMetodoChange(metodo: MetodoPago) {
        _estado.update { it.copy(metodo = metodo) }
    }

    fun onObservacionChange(valor: String) {
        _estado.update { it.copy(observacion = valor) }
    }

    fun onComprobanteChange(uri: Uri?) {
        _estado.update { it.copy(comprobante = uri) }
    }

    fun reiniciar() {
        _estado.update { PagoUiState(reservas = it.reservas, filtroRecintoId = it.filtroRecintoId) }
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.PAGO_REGISTRAR)) return
        val datos = _estado.value

        val elegida = datos.reservaSeleccionada
        val monto = datos.monto.toLongOrNull()
        val mensajeMonto = if (monto == null || monto <= 0) {
            "Ingresa un monto mayor a 0"
        } else if (elegida != null && monto > elegida.pendiente) {
            "El monto supera lo que falta por pagar"
        } else {
            null
        }
        val errores = PagoErrores(
            reserva = if (elegida == null) "Elige la reserva que se está pagando" else null,
            monto = mensajeMonto
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores || elegida == null || monto == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val id = finanzasRepository.registrarPago(
                reservaId = elegida.detalle.reserva.id,
                monto = monto,
                metodo = datos.metodo,
                registradoPorId = sesion.usuario.id,
                comprobanteUri = datos.comprobante?.toString(),
                observacion = datos.observacion.trim().takeIf { it.isNotEmpty() }
            )
            if (id == null) {
                /** null: reserva no existe o ya no está vigente */
                _estado.update {
                    it.copy(
                        enviando = false,
                        errores = it.errores.copy(general = "No se pudo registrar el pago. Revisa que la reserva siga vigente.")
                    )
                }
            } else {
                _estado.update { it.copy(enviando = false) }
                _pagoRegistrado.send(Unit)
            }
        }
    }
}
