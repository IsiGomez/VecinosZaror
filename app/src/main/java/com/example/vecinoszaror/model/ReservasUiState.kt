package com.example.vecinoszaror.model

/** Estado de la lista de reservas */
data class ReservasUiState(
    val reservas: List<ReservaConDetalle> = emptyList(),
    val cargando: Boolean = true,
    val puedeVerTodas: Boolean = false
)
