package com.example.vecinoszaror.model

data class RecintoFormularioErrores(
    val nombre: String? = null,
    val capacidad: String? = null,
    val tarifa: String? = null,
    /** ejem: "el nombre ya existe". */
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = nombre != null || capacidad != null || tarifa != null
}


/** Estado del formulario (ventana) para crear o editar un recinto. */
data class RecintoFormularioUiState(
    /** true mientras la ventana está abierta. */
    val abierto: Boolean = false,
    /** Id del recinto que se edita / null = se está creando uno nuevo. */
    val recintoId: Long? = null,
    val nombre: String = "",
    val descripcion: String = "",
    /** Se convierte a número al validar. */
    val capacidad: String = "",
    val tarifa: String = "",
    val activo: Boolean = true,
    val enviando: Boolean = false,
    val errores: RecintoFormularioErrores = RecintoFormularioErrores()
) {
    val esNuevo: Boolean
        get() = recintoId == null
}
