package com.example.vecinoszaror.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.uistate.GastoErrores
import com.example.vecinoszaror.model.uistate.GastoUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que Tesorería registra un gasto (luz, agua, mantención...). */
class GastoViewModel(
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(GastoUiState())
    val estado: StateFlow<GastoUiState> = _estado.asStateFlow()

    // Aviso de una sola vez: la pantalla lo usa para volver a Finanzas
    private val _gastoRegistrado = Channel<Unit>(Channel.BUFFERED)
    val gastoRegistrado: Flow<Unit> = _gastoRegistrado.receiveAsFlow()

    fun onCategoriaChange(categoria: CategoriaGasto) {
        _estado.update { it.copy(categoria = categoria) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null, general = null)) }
    }

    fun onMontoChange(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(monto = soloNumeros, errores = it.errores.copy(monto = null, general = null)) }
    }

    fun onComprobanteChange(uri: Uri?) {
        _estado.update { it.copy(comprobante = uri) }
    }

    fun reiniciar() {
        _estado.value = GastoUiState()
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.GASTO_REGISTRAR)) return
        val datos = _estado.value

        val monto = datos.monto.toLongOrNull()
        val errores = GastoErrores(
            descripcion = if (datos.descripcion.isBlank()) "Describe el gasto" else null,
            monto = if (monto == null || monto <= 0) "Ingresa un monto mayor a 0" else null
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores || monto == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val id = finanzasRepository.registrarGasto(
                categoria = datos.categoria,
                descripcion = datos.descripcion,
                monto = monto,
                registradoPorId = sesion.usuario.id,
                comprobanteUri = datos.comprobante?.toString()
            )
            if (id == null) {
                _estado.update {
                    it.copy(enviando = false, errores = it.errores.copy(general = "No se pudo registrar el gasto."))
                }
            } else {
                _estado.update { it.copy(enviando = false) }
                _gastoRegistrado.send(Unit)
            }
        }
    }
}
