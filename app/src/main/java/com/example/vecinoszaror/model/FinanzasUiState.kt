package com.example.vecinoszaror.model

/** Estado de la pantalla de finanzas: totales, movimientos y qué acciones permite el rol. */
data class FinanzasUiState(
    val totalIngresos: Long = 0,
    val totalGastos: Long = 0,
    val pagos: List<Pago> = emptyList(),
    val gastos: List<Gasto> = emptyList(),
    val cargando: Boolean = true,
    val puedeRegistrarPago: Boolean = false,
    val puedeRegistrarGasto: Boolean = false
) {
    /** Lo que queda. */
    val saldo: Long
        get() = totalIngresos - totalGastos
}
