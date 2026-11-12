package com.example.vecinoszaror.model.uistate

data class EditarPerfilErrores(
    val nombre: String? = null,
    val apellido: String? = null,
    val telefono: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = nombre != null || apellido != null || telefono != null
}

/** Estado del formulario para editar los datos personales del perfil. */
data class EditarPerfilUiState(
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val guardando: Boolean = false,
    val errores: EditarPerfilErrores = EditarPerfilErrores()
) {
    companion object {
        const val MAX_TELEFONO = 18
    }
}
