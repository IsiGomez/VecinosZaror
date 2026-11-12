package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.uistate.UsuariosUiState
import com.example.vecinoszaror.repository.RolPermisoRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la lista de usuarios con su rol. */
class UsuariosViewModel(
    usuarioRepository: UsuarioRepository,
    rolPermisoRepository: RolPermisoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(UsuariosUiState())
    val estado: StateFlow<UsuariosUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // combine vuelve a calcular la lista cuando cambian los usuarios o los roles
            combine(
                usuarioRepository.observarUsuarios(),
                rolPermisoRepository.observarRoles()
            ) { usuarios, roles ->
                usuarios to roles
            }.collect { (usuarios, roles) ->
                _estado.update { it.copy(usuarios = usuarios, roles = roles, cargando = false) }
            }
        }
    }


    /** Filtra los usuarios por rol (null = todos). */
    fun onFiltroRolChange(rolId: Long?) {
        _estado.update { it.copy(filtroRolId = rolId) }
    }
}
