package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.DetalleReservaUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import com.example.vecinoszaror.repository.ReservaRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** Detalle de una reserva: sus datos, cuánto se ha pagado y quién la gestionó. */
class DetalleReservaViewModel(
    private val reservaRepository: ReservaRepository,
    private val finanzasRepository: FinanzasRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(DetalleReservaUiState())
    val estado: StateFlow<DetalleReservaUiState> = _estado.asStateFlow()

    /** Escucha la base, se cancela al abrir otra reserva */
    private var tareaEscucha: Job? = null

    /** Se llama al tocar una reserva de la lista, antes de navegar al detalle. */
    fun cargar(reservaId: Long, sesion: SesionUsuario) {
        tareaEscucha?.cancel()
        _estado.value = DetalleReservaUiState(cargando = true)
        val puedeVerTodas = sesion.tienePermiso(Permisos.RESERVA_VER_TODAS)

        tareaEscucha = viewModelScope.launch {
            // Se actualiza sola si la reserva cambia de estado o se registra un pago
            combine(
                reservaRepository.observarDetalle(reservaId),
                finanzasRepository.observarPagosDeReserva(reservaId)
            ) { detalle, pagos -> detalle to pagos.sumOf { it.monto } }
                .collect { (detalle, pagado) ->
                    val visible = detalle?.takeIf {
                        puedeVerTodas || it.reserva.usuarioId == sesion.usuario.id
                    }
                    val gestionadaPor = visible?.reserva?.gestionadaPorId
                        ?.let { usuarioRepository.obtenerPorId(it) }
                        ?.let { "${it.nombre} ${it.apellido}" }

                    _estado.value = DetalleReservaUiState(
                        detalle = visible,
                        pagado = pagado,
                        gestionadaPor = gestionadaPor,
                        cargando = false
                    )
                }
        }
    }
}
