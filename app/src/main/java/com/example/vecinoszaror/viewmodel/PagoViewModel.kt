package com.example.vecinoszaror.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.MetodoPago
import com.example.vecinoszaror.model.PagoErrores
import com.example.vecinoszaror.model.PagoUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import com.example.vecinoszaror.repository.ReservaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que Tesorería registra el pago de una reserva. */
class PagoViewModel(
    reservaRepository: ReservaRepository,
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PagoUiState())
    val estado: StateFlow<PagoUiState> = _estado.asStateFlow()

    /** Aviso de una sola vez */
    private val _pagoRegistrado = Channel<Unit>(Channel.BUFFERED)
    val pagoRegistrado: Flow<Unit> = _pagoRegistrado.receiveAsFlow()

    init {
        viewModelScope.launch {
            reservaRepository.observarTodas()
                .map { lista ->
                    lista.filter {
                        it.reserva.estado == EstadoReserva.PENDIENTE || it.reserva.estado == EstadoReserva.CONFIRMADA
                    }
                }
                .collect { vigentes -> _estado.update { it.copy(reservas = vigentes) } }
        }
    }

    /** Al cambiar un campo se actualiza su valor y se limpian sus errores */
    fun onReservaChange(id: Long) {
        _estado.update { actual ->
            val sugerido = actual.reservas.firstOrNull { it.reserva.id == id }?.reserva?.montoTotal
            actual.copy(
                reservaId = id,
                monto = sugerido?.toString() ?: actual.monto,
                errores = actual.errores.copy(reserva = null, monto = null, general = null)
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
        _estado.update { PagoUiState(reservas = it.reservas) }
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.PAGO_REGISTRAR)) return
        val datos = _estado.value

        val monto = datos.monto.toLongOrNull()
        val errores = PagoErrores(
            reserva = if (datos.reservaId == null) "Elige la reserva que se está pagando" else null,
            monto = if (monto == null || monto <= 0) "Ingresa un monto mayor a 0" else null
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores || datos.reservaId == null || monto == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val id = finanzasRepository.registrarPago(
                reservaId = datos.reservaId,
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
                reiniciar()
                _pagoRegistrado.send(Unit)
            }
        }
    }
}
