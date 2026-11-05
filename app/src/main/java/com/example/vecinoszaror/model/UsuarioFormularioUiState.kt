package com.example.vecinoszaror.model

data class UsuarioFormularioErrores(
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val clave: String? = null,
    val rol: String? = null,
    /** ejem: "el correo ya está en uso". */
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = nombre != null || apellido != null || email != null || clave != null || rol != null
}

/** Estado del formulario (ventana) para crear o editar un usuario. */
data class UsuarioFormularioUiState(
    /** true mientras la ventana está abierta. */
    val abierto: Boolean = false,
    /** Id del usuario que se edita / null = se está creando uno nuevo. */
    val usuarioId: Long? = null,
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    /** Al crear es obligatoria / al editar, vacía no cambia. */
    val clave: String = "",
    val rolId: Long? = null,
    val activo: Boolean = true,
    /** true si se está editando a la misma persona que inició sesión. */
    val esPropio: Boolean = false,
    val roles: List<Rol> = emptyList(),
    val enviando: Boolean = false,
    val errores: UsuarioFormularioErrores = UsuarioFormularioErrores()
) {
    val esNuevo: Boolean
        get() = usuarioId == null
}
