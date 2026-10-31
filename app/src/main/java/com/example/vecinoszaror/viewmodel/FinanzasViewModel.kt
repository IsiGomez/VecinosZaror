package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.FinanzasUiState
import com.example.vecinoszaror.model.Gasto
import com.example.vecinoszaror.model.Pago
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** Maneja el resumen financiero: ingresos, gastos y saldo, con sus movimientos. */
class FinanzasViewModel(
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(FinanzasUiState())
    val estado: StateFlow<FinanzasUiState> = _estado.asStateFlow()

    // Tarea que escucha la base, se cancela al cambiar de usuario
    private var tareaEscucha: Job? = null

    /** Agrupa lo que entregan los cuatro flujos, para actualizar el estado de una sola vez. */
    private data class Datos(
        val pagos: List<Pago>,
        val gastos: List<Gasto>,
        val totalIngresos: Long,
        val totalGastos: Long
    )

    /** Empieza a observar las finanzas y deja anotado qué acciones permite el rol. */
    fun cargar(sesion: SesionUsuario) {
        tareaEscucha?.cancel()
        _estado.value = FinanzasUiState(
            cargando = true,
            puedeRegistrarPago = sesion.tienePermiso(Permisos.PAGO_REGISTRAR),
            puedeRegistrarGasto = sesion.tienePermiso(Permisos.GASTO_REGISTRAR)
        )

        tareaEscucha = viewModelScope.launch {
            /** combine junta varios flujos: emite cada vez que cualquiera de ellos cambia */
            combine(
                finanzasRepository.observarPagos(),
                finanzasRepository.observarGastos(),
                finanzasRepository.observarTotalIngresos(),
                finanzasRepository.observarTotalGastos()
            ) { pagos, gastos, totalIngresos, totalGastos ->
                Datos(pagos, gastos, totalIngresos, totalGastos)
            }.collect { datos ->
                _estado.value = _estado.value.copy(
                    pagos = datos.pagos,
                    gastos = datos.gastos,
                    totalIngresos = datos.totalIngresos,
                    totalGastos = datos.totalGastos,
                    cargando = false
                )
            }
        }
    }
}
