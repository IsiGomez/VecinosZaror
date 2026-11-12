package com.example.vecinoszaror.model.uistate

import com.example.vecinoszaror.model.Rol
import com.example.vecinoszaror.model.Usuario

/** Estado de la lista de usuarios de la pantalla de administración. */
data class UsuariosUiState(
    val usuarios: List<Usuario> = emptyList(),
    val roles: List<Rol> = emptyList(),
    /** null = todos. */
    val filtroRolId: Long? = null,
    val cargando: Boolean = true
) {
    /** Muestra usuarios según el filtro. */
    val usuariosVisibles: List<Usuario>
        get() = if (filtroRolId == null) usuarios else usuarios.filter { it.rolId == filtroRolId }

    /** Rol elegido en el filtro, null = todos. */
    val rolFiltrado: Rol?
        get() = roles.firstOrNull { it.id == filtroRolId }

    /** Busca el rol de un usuario a partir de su rolId. */
    fun rolDe(usuario: Usuario): Rol? = roles.firstOrNull { it.id == usuario.rolId }
}
