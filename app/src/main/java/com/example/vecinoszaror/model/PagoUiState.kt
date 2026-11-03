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


/** Una reserva con lo que ya se ha pagado de ella. */
data class ReservaPorPagar(
    val detalle: ReservaConDetalle,
    val pagado: Long
) {
    /** Lo que falta por pagar. */
    val pendiente: Long
        get() = (detalle.reserva.montoTotal - pagado).coerceAtLeast(0)
}


/** Estado del formulario para registrar el pago de una reserva. */
data class PagoUiState(
    /** Reservas que tienen un saldo pendiente. */
    val reservas: List<ReservaPorPagar> = emptyList(),
    /** null = todos. */
    val filtroRecintoId: Long? = null,
    val reservaId: Long? = null,
    /** Se convierte a número al validar. */
    val monto: String = "",
    val metodo: MetodoPago = MetodoPago.EFECTIVO,
    val observacion: String = "",
    /** Dirección de la foto del comprobante */
    val comprobante: Uri? = null,
    val enviando: Boolean = false,
    val errores: PagoErrores = PagoErrores()
) {
    val recintos: List<Recinto>
        get() = reservas.map { it.detalle.recinto }.distinctBy { it.id }

    val reservasVisibles: List<ReservaPorPagar>
        get() = if (filtroRecintoId == null) reservas else reservas.filter { it.detalle.recinto.id == filtroRecintoId }

    /** La reserva que el usuario eligió, null si no hay ninguna. */
    val reservaSeleccionada: ReservaPorPagar?
        get() = reservas.firstOrNull { it.detalle.reserva.id == reservaId }
}
