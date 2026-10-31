package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.DisponibilidadUiState
import com.example.vecinoszaror.repository.RecintoRepository
import com.example.vecinoszaror.repository.ReservaRepository
import com.example.vecinoszaror.util.combinarFechaYHora
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Permite a Secretaría ver qué horarios de un recinto están ocupados en un día. */
class DisponibilidadViewModel(
    private val reservaRepository: ReservaRepository,
    recintoRepository: RecintoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(DisponibilidadUiState())
    val estado: StateFlow<DisponibilidadUiState> = _estado.asStateFlow()

    // Tarea que escucha la base; se cancela cada vez que cambia el recinto o la fecha
    private var tareaEscucha: Job? = null

    init {
        viewModelScope.launch {
            recintoRepository.observarActivos().collect { lista ->
                _estado.update { it.copy(recintos = lista) }
            }
        }
    }

    fun onRecintoChange(id: Long) {
        _estado.update { it.copy(recintoId = id) }
        consultar()
    }

    fun onFechaChange(fecha: Long) {
        _estado.update { it.copy(fecha = fecha) }
        consultar()
    }

    /** Si ya hay recinto y fecha, empieza a observar las reservas de ese día. */
    private fun consultar() {
        tareaEscucha?.cancel()
        val recintoId = _estado.value.recintoId
        val fecha = _estado.value.fecha
        if (recintoId == null || fecha == null) return

        // El día va desde la medianoche de la fecha elegida hasta la del día siguiente
        val desde = combinarFechaYHora(fecha, 0)
        val hasta = combinarFechaYHora(fecha + UN_DIA_EN_MILISEGUNDOS, 0)

        _estado.update { it.copy(cargando = true, ocupacion = emptyList()) }
        tareaEscucha = viewModelScope.launch {
            reservaRepository.observarOcupacion(recintoId, desde, hasta).collect { lista ->
                _estado.update { it.copy(ocupacion = lista, cargando = false) }
            }
        }
    }

    private companion object {
        const val UN_DIA_EN_MILISEGUNDOS = 24 * 60 * 60 * 1000L
    }
}
