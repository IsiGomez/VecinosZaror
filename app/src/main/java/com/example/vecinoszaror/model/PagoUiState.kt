package com.example.vecinoszaror.model

import android.net.Uri

data class PagoErrores(
    val reserva: String? = null,
    val monto: String? = null,
    val general: String? = null
) {
    /** true si algún campo tiene error (el error general no cuenta). */
    val hayErrores: Boolean
        get() = reserva != null || monto != null
}

/** Estado del formulario para registrar el pago de una reserva. */
data class PagoUiState(
    /** Reservas que se les puede registrar un pago (pendientes o confirmadas). */
    val reservas: List<ReservaConDetalle> = emptyList(),
    val reservaId: Long? = null,
    /** Se convierte a número al validar. */
    val monto: String = "",
    val metodo: MetodoPago = MetodoPago.EFECTIVO,
    val observacion: String = "",
    /** Dirección de la foto del comprobante */
    val comprobante: Uri? = null,
    val enviando: Boolean = false,
    val errores: PagoErrores = PagoErrores()
)
