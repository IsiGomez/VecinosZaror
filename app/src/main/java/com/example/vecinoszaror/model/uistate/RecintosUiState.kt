package com.example.vecinoszaror.model.uistate

import com.example.vecinoszaror.model.Recinto

/** Estado de la lista de recintos de la pantalla de administración (incluye los inactivos). */
data class RecintosUiState(
    val recintos: List<Recinto> = emptyList(),
    val cargando: Boolean = true
)
