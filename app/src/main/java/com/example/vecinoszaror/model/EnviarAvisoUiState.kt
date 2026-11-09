package com.example.vecinoszaror.model

data class EnviarAvisoErrores(
    val titulo: String? = null,
    val mensaje: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = titulo != null || mensaje != null
}

/** Estado del formulario para enviar un aviso. */
data class EnviarAvisoUiState(
    val destinatario: Destinatario = Destinatario.TODOS,
    val titulo: String = "",
    val mensaje: String = "",
    val enviando: Boolean = false,
    /** Cuantas personas lo recibieron. */
    val enviadoA: Int? = null,
    val errores: EnviarAvisoErrores = EnviarAvisoErrores()
) {
    companion object {
        const val MAX_TITULO = 60
        const val MAX_MENSAJE = 300
    }
}
