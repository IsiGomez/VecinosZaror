package com.example.vecinoszaror.model.uistate

import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.RolConPermisos

/** Estado de roles: cada rol con sus permisos y la lista completa de permisos. */
data class RolesUiState(
    val roles: List<RolConPermisos> = emptyList(),
    val permisos: List<Permiso> = emptyList(),
    /** null = todavía no se eligió (se usa el primero). */
    val rolElegidoId: Long? = null,
    val cargando: Boolean = true
) {
    /** null = todavía no se eligió (se usa el primero). */
    val rolElegido: RolConPermisos?
        get() = roles.firstOrNull { it.rol.id == rolElegidoId } ?: roles.firstOrNull()

    /** Los permisos agrupados por módulo (Reservas, Finanzas...), en el orden en que llegan. */
    val permisosPorModulo: Map<String, List<Permiso>>
        get() = permisos.groupBy { it.modulo }

    /** true si el rol elegido tiene este permiso. */
    fun tiene(permiso: Permiso): Boolean =
        rolElegido?.permisos?.any { it.id == permiso.id } == true
}
