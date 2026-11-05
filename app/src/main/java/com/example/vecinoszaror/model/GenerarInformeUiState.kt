package com.example.vecinoszaror.model

/** Errores del formulario de informe (null = sin error). */
data class GenerarInformeErrores(
    val titulo: String? = null,
    val inicio: String? = null,
    val fin: String? = null
) {
    val hayErrores: Boolean
        get() = titulo != null || inicio != null || fin != null
}

/** Estado del formulario para generar un informe de un período. */
data class GenerarInformeUiState(
    val titulo: String = "",
    /** Como lo entrega el calendario (UTC), null = sin elegir. */
    val inicioUtc: Long? = null,
    /** Como lo entrega el calendario (UTC), null = sin elegir. */
    val finUtc: Long? = null,
    val observaciones: String = "",
    val enviando: Boolean = false,
    val errores: GenerarInformeErrores = GenerarInformeErrores()
)
