package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.uistate.GenerarInformeErrores
import com.example.vecinoszaror.model.uistate.GenerarInformeUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import com.example.vecinoszaror.util.finDelDia
import com.example.vecinoszaror.util.inicioDelDia
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el se genera el informe de un período. */
class GenerarInformeViewModel(
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(GenerarInformeUiState())
    val estado: StateFlow<GenerarInformeUiState> = _estado.asStateFlow()

    // Aviso de una sola vez: la pantalla lo usa para volver a la lista de informes
    private val _informeGenerado = Channel<Unit>(Channel.BUFFERED)
    val informeGenerado: Flow<Unit> = _informeGenerado.receiveAsFlow()

    fun onTituloChange(valor: String) {
        _estado.update { it.copy(titulo = valor, errores = it.errores.copy(titulo = null)) }
    }

    fun onInicioChange(fechaUtc: Long) {
        _estado.update { it.copy(inicioUtc = fechaUtc, errores = it.errores.copy(inicio = null, fin = null)) }
    }

    fun onFinChange(fechaUtc: Long) {
        _estado.update { it.copy(finUtc = fechaUtc, errores = it.errores.copy(fin = null)) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor) }
    }

    fun reiniciar() {
        _estado.value = GenerarInformeUiState()
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.INFORME_GENERAR)) return
        val datos = _estado.value

        // Copias locales para que el compilador pueda asegurar que no son null
        val inicioUtc = datos.inicioUtc
        val finUtc = datos.finUtc
        val errores = GenerarInformeErrores(
            titulo = if (datos.titulo.isBlank()) "Escribe un título para el informe" else null,
            inicio = if (inicioUtc == null) "Elige el día de inicio" else null,
            fin = when {
                finUtc == null -> "Elige el día de término"
                inicioUtc != null && finUtc < inicioUtc -> "El término no puede ser antes del inicio"
                else -> null
            }
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores || inicioUtc == null || finUtc == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            finanzasRepository.generarInforme(
                titulo = datos.titulo,
                periodoInicio = inicioDelDia(inicioUtc),
                periodoFin = finDelDia(finUtc),
                generadoPorId = sesion.usuario.id,
                observaciones = datos.observaciones.trim().takeIf { it.isNotEmpty() }
            )
            _estado.update { it.copy(enviando = false) }
            _informeGenerado.send(Unit)
        }
    }
}
