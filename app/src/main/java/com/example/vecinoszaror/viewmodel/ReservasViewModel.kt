package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.ReservasUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.ReservaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la lista de reservas, los perfiles ven lo que sus permisos le permiten */
class ReservasViewModel (
    private val reservaRepository: ReservaRepository
) : ViewModel() {
    private val _estado = MutableStateFlow(ReservasUiState())
    val estado: StateFlow<ReservasUiState> = _estado.asStateFlow()

    /** Escucha la base, se cancela al cambiar de usuario */
    private var tareaEscucha: Job? = null

    /** Las reservas que corresponden a la sesión */
    fun cargar(sesion: SesionUsuario) {
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
}