package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.uistate.ReservasUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.ReservaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la lista de reservas, los perfiles ven lo que sus permisos le permiten */
class ReservasViewModel (
    private val reservaRepository: ReservaRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(ReservasUiState())
    val estado: StateFlow<ReservasUiState> = _estado.asStateFlow()

    /** Mensajes de una sola vez para la pantalla. */
    private val _mensajes = Channel<String>(Channel.BUFFERED)
    val mensajes: Flow<String> = _mensajes.receiveAsFlow()

    /** Escucha la base, se cancela al cambiar de usuario */
    private var tareaEscucha: Job? = null

    // Sesión ya cargada: evita reiniciar la pantalla cada vez que se vuelve a ella
    private var sesionCargada: SesionUsuario? = null

    /** Se llama al iniciar sesión */
    fun reiniciar() {
        tareaEscucha?.cancel()
        sesionCargada = null
        _estado.value = ReservasUiState()
    }

    /** Las reservas que corresponden a la sesión */
    fun cargar(sesion: SesionUsuario) {
        if (sesion == sesionCargada) return
        sesionCargada = sesion
        tareaEscucha?.cancel()
        val puedeVerTodas = sesion.tienePermiso(Permisos.RESERVA_VER_TODAS)
        _estado.value = ReservasUiState(cargando = true, puedeVerTodas = puedeVerTodas)

        val reservas = if (puedeVerTodas) {
            reservaRepository.observarTodas()
        } else {
            reservaRepository.observarPorUsuario(sesion.usuario.id)
        }

        tareaEscucha = viewModelScope.launch {
            reservas.collect { lista ->
                _estado.update { it.copy(reservas = lista, cargando = false) }
            }
        }
    }

    /** Confirma o rechaza una reserva pendiente. */
    fun responder(reserva: Reserva, aceptar: Boolean, observacion: String?, sesion: SesionUsuario) {
        if (!sesion.tienePermiso(Permisos.RESERVA_GESTIONAR)) return
        viewModelScope.launch {
            val exito = reservaRepository.responder(
                reservaId = reserva.id,
                gestionadaPorId = sesion.usuario.id,
                aceptar = aceptar,
                observacion = observacion?.trim()?.takeIf { it.isNotEmpty() }
            )
            _mensajes.send(
                when {
                    !exito -> "No se pudo actualizar: la reserva ya fue gestionada"
                    aceptar -> "Reserva confirmada"
                    else -> "Reserva rechazada"
                }
            )
        }
    }

    fun cancelar(reserva: Reserva, observacion: String?, sesion: SesionUsuario) {
        if (!sesion.puedeCancelar(reserva)) return
        val esPropia = reserva.usuarioId == sesion.usuario.id
        viewModelScope.launch {
            val exito = reservaRepository.cancelar(
                reservaId = reserva.id,
                gestionadaPorId = if (esPropia) null else sesion.usuario.id,
                observacion = observacion?.trim()?.takeIf { it.isNotEmpty() }
            )
            _mensajes.send(if (exito) "Reserva cancelada" else "No se pudo cancelar: la reserva ya terminó")
        }
    }
}