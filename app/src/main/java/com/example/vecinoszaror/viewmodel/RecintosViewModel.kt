package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.uistate.RecintosUiState
import com.example.vecinoszaror.repository.RecintoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la lista de recintos, activos e inactivos. */
class RecintosViewModel(
    recintoRepository: RecintoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RecintosUiState())
    val estado: StateFlow<RecintosUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // observarTodos incluye los inactivos, las pantallas de reservas usan solo los activos
            recintoRepository.observarTodos().collect { lista ->
                _estado.update { it.copy(recintos = lista, cargando = false) }
            }
        }
    }
}
