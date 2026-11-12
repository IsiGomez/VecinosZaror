package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.uistate.RolesUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.RolPermisoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja los roles y permisos: qué puede hacer cada rol. */
class RolesViewModel(
    private val rolPermisoRepository: RolPermisoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RolesUiState())
    val estado: StateFlow<RolesUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // Cuando se asigna o quita un permiso, Room vuelve a entregar los roles con sus permisos
            combine(
                rolPermisoRepository.observarRolesConPermisos(),
                rolPermisoRepository.observarPermisos()
            ) { roles, permisos ->
                roles to permisos
            }.collect { (roles, permisos) ->
                _estado.update { it.copy(roles = roles, permisos = permisos, cargando = false) }
            }
        }
    }

    fun onRolChange(rolId: Long) {
        _estado.update { it.copy(rolElegidoId = rolId) }
    }

    /** true si este cambio dejaría a la persona sin poder volver a administrar los permisos. */
    fun esPermisoProtegido(permiso: Permiso, sesion: SesionUsuario): Boolean =
        _estado.value.rolElegido?.rol?.id == sesion.rol.id &&
                permiso.codigo == Permisos.ROL_PERMISO_GESTIONAR

    /** Da o quita un permiso al rol que se está viendo. */
    fun cambiarPermiso(permiso: Permiso, asignado: Boolean, sesion: SesionUsuario) {
        val rolId = _estado.value.rolElegido?.rol?.id ?: return
        if (!sesion.tienePermiso(Permisos.ROL_PERMISO_GESTIONAR)) return
        // Nadie puede quitarle a su propio rol el permiso de administrar permisos
        if (!asignado && esPermisoProtegido(permiso, sesion)) return

        viewModelScope.launch {
            if (asignado) {
                rolPermisoRepository.asignarPermiso(rolId, permiso.id)
            } else {
                rolPermisoRepository.quitarPermiso(rolId, permiso.id)
            }
        }
    }
}
