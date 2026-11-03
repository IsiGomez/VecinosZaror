package com.example.vecinoszaror.model

/** Estado de la pantalla de finanzas: totales, movimientos, filtros y qué acciones permite el rol. */
data class FinanzasUiState(
    val totalIngresos: Long = 0,
    val totalGastos: Long = 0,
    val pagos: List<Pago> = emptyList(),
    val gastos: List<Gasto> = emptyList(),
    /** A qué recinto corresponde cada reserva (un pago solo guarda el id de su reserva). */
    val recintoPorReserva: Map<Long, Recinto> = emptyMap(),
    /** Recinto elegido para filtrar los pagos; null = todos. */
    val filtroRecintoId: Long? = null,
    /** Categoría elegida para filtrar los gastos; null = todas. */
    val filtroCategoria: CategoriaGasto? = null,
    val cargando: Boolean = true,
    val puedeRegistrarPago: Boolean = false,
    val puedeRegistrarGasto: Boolean = false
) {
    /** Lo que queda. */
    val saldo: Long
        get() = totalIngresos - totalGastos

    val recintosConPagos: List<Recinto>
        get() = pagos.mapNotNull { recintoPorReserva[it.reservaId] }.distinctBy { it.id }

    val pagosVisibles: List<Pago>
        get() = if (filtroRecintoId == null) pagos
        else pagos.filter { recintoPorReserva[it.reservaId]?.id == filtroRecintoId }

    /** Orden del enum. */
    val categoriasConGastos: List<CategoriaGasto>
        get() = gastos.map { it.categoria }.distinct().sortedBy { it.ordinal }

    val gastosVisibles: List<Gasto>
        get() = if (filtroCategoria == null) gastos else gastos.filter { it.categoria == filtroCategoria }
}
