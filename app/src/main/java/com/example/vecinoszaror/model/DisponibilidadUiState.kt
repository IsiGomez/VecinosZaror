package com.example.vecinoszaror.model

/** Estado de la consulta de disponibilidad de un recinto en un día. */
data class DisponibilidadUiState(
    val recintos: List<Recinto> = emptyList(),
    val recintoId: Long? = null,
    /** Milisegundos. */
    val fecha: Long? = null,
    /** Reservas pendientes o confirmadas de ese recinto ese día. */
    val ocupacion: List<ReservaConDetalle> = emptyList(),
    val cargando: Boolean = false
) {
    /** true cuando ya se eligió recinto y fecha. */
    val hayConsulta: Boolean
        get() = recintoId != null && fecha != null
}
