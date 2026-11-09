package com.example.vecinoszaror.model

/** Estado de la pantalla con el detalle de una reserva. */
data class DetalleReservaUiState(
    /** null: la reserva no existe o la sesión no puede verla. */
    val detalle: ReservaConDetalle? = null,
    /** Suma de los pagos registrados para esta reserva. */
    val pagado: Long = 0,
    /** Nombre de quien confirmó/rechazó/canceló la reserva. */
    val gestionadaPor: String? = null,
    /** El rol puede cancelarla y su estado lo permite. */
    val puedeCancelar: Boolean = false,
    val cargando: Boolean = true
) {
    val noEncontrada: Boolean
        get() = !cargando && detalle == null

    /** Lo que falta por pagar. */
    val pendiente: Long
        get() = ((detalle?.reserva?.montoTotal ?: 0L) - pagado).coerceAtLeast(0)

    val estadoPago: EstadoPago
        get() {
            val reserva = detalle?.reserva ?: return EstadoPago.NO_APLICA
            return when {
                reserva.estado == EstadoReserva.RECHAZADA ||
                        reserva.estado == EstadoReserva.CANCELADA -> EstadoPago.NO_APLICA
                reserva.montoTotal == 0L -> EstadoPago.SIN_COSTO
                pagado >= reserva.montoTotal -> EstadoPago.PAGADO
                pagado > 0 -> EstadoPago.ABONADO
                else -> EstadoPago.PENDIENTE
            }
        }
}
