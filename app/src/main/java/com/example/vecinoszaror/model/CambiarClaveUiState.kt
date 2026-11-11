package com.example.vecinoszaror.model

data class CambiarClaveErrores(
    val actual: String? = null,
    val nueva: String? = null,
    val confirmacion: String? = null
) {
    val hayErrores: Boolean
        get() = actual != null || nueva != null || confirmacion != null
}

/** Estado del formulario para cambiar la clave propia. */
data class CambiarClaveUiState(
    val actual: String = "",
    val nueva: String = "",
    val confirmacion: String = "",
    val guardando: Boolean = false,
    val cambiada: Boolean = false,
    val errores: CambiarClaveErrores = CambiarClaveErrores()
) {
    companion object {
        const val CLAVE_MINIMA = 8
    }
}
