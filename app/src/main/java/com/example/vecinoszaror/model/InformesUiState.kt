package com.example.vecinoszaror.model

/** Estado de la pantalla de informes. */
data class InformesUiState(
    val informes: List<InformeFinanciero> = emptyList(),
    val cargando: Boolean = true,
    /** true ve los borradores. */
    val veBorradores: Boolean = false,
    /** true puede publicar o quitar la publicación de un informe. */
    val puedePublicar: Boolean = false
)
