package com.example.vecinoszaror.model

data class SolicitudErrores(
    val recinto: String? = null,
    val fecha: String? = null,
    val horaInicio: String? = null,
    val horaFin: String? = null,
    val motivo: String? = null,
    val asistentes: String? = null,
    val general: String? = null
) {
    /** true si algún campo tiene error (el error general no cuenta). */
    val hayErrores: Boolean
        get() = listOfNotNull(recinto, fecha, horaInicio, horaFin, motivo, asistentes).isNotEmpty()
}

/** Estado del formulario para solicitar una reserva. */
data class SolicitudReservaUiState(
    val recintos: List<Recinto> = emptyList(),
    val recintoId: Long? = null,
    /** Milisegundos. */
    val fecha: Long? = null,
    /** Desde la medianoche */
    val horaInicioMinutos: Int? = null,
    val horaFinMinutos: Int? = null,
    val motivo: String = "",
    /** Se convierte a número al validar. */
    val asistentes: String = "",
    val enviando: Boolean = false,
    val errores: SolicitudErrores = SolicitudErrores()
)
