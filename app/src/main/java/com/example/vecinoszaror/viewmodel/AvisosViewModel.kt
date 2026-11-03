package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.AvisosUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.NotificacionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Maneja la lista de avisos del usuario que inició sesión. */
class AvisosViewModel(
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(AvisosUiState())
    val estado: StateFlow<AvisosUiState> = _estado.asStateFlow()

    // Tarea que escucha la base, se cancela al cambiar de usuario
    private var tareaEscucha: Job? = null
    private var usuarioId: Long? = null

    /** Empieza a observar los avisos de este usuario. */
    fun cargar(sesion: SesionUsuario) {
        tareaEscucha?.cancel()
        usuarioId = sesion.usuario.id
        _estado.value = AvisosUiState(cargando = true)

        tareaEscucha = viewModelScope.launch {
            // Room vuelve a entregar la lista cada vez que cambia un aviso
            notificacionRepository.observarPorUsuario(sesion.usuario.id).collect { lista ->
                _estado.value = AvisosUiState(avisos = lista, cargando = false)
            }
        }
    }

    fun marcarLeido(id: Long) {
        viewModelScope.launch { notificacionRepository.marcarLeida(id) }
    }

    fun marcarTodosLeidos() {
        val id = usuarioId ?: return
        viewModelScope.launch { notificacionRepository.marcarTodasLeidas(id) }
    }
}
