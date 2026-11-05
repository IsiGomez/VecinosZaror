package com.example.vecinoszaror.model

/** Estado de la pantalla de avisos. */
data class AvisosUiState(
    val avisos: List<Notificacion> = emptyList(),
    val cargando: Boolean = true
) {
    /** Cuántos avisos faltan por leer. */
    val noLeidos: Int
        get() = avisos.count { !it.leida }
}
