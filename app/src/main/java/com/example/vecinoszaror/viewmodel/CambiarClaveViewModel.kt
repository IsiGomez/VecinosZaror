package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.uistate.CambiarClaveErrores
import com.example.vecinoszaror.model.uistate.CambiarClaveUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que cada persona cambia su propia clave. */
class CambiarClaveViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(CambiarClaveUiState())
    val estado: StateFlow<CambiarClaveUiState> = _estado.asStateFlow()

    fun onActualChange(valor: String) {
        _estado.update { it.copy(actual = valor, errores = it.errores.copy(actual = null)) }
    }

    fun onNuevaChange(valor: String) {
        _estado.update { it.copy(nueva = valor, errores = it.errores.copy(nueva = null)) }
    }

    fun onConfirmacionChange(valor: String) {
        _estado.update { it.copy(confirmacion = valor, errores = it.errores.copy(confirmacion = null)) }
    }

    fun reiniciar() {
        _estado.value = CambiarClaveUiState()
    }

    fun cambiar(sesion: SesionUsuario) {
        if (_estado.value.guardando) return
        val datos = _estado.value

        val locales = CambiarClaveErrores(
            actual = if (datos.actual.isEmpty()) "Ingresa tu clave actual" else null,
            nueva = when {
                datos.nueva.length < CambiarClaveUiState.CLAVE_MINIMA ->
                    "Debe tener al menos ${CambiarClaveUiState.CLAVE_MINIMA} caracteres"
                datos.nueva == datos.actual -> "Debe ser distinta a la actual"
                else -> null
            },
            confirmacion = if (datos.confirmacion != datos.nueva) "Las claves no coinciden" else null
        )

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val actualIncorrecta = datos.actual.isNotEmpty() &&
                    !usuarioRepository.claveCorrecta(sesion.usuario.id, datos.actual)
            val errores = if (actualIncorrecta) {
                locales.copy(actual = "La clave actual no es correcta")
            } else {
                locales
            }

            if (errores.hayErrores) {
                _estado.update { it.copy(guardando = false, errores = errores) }
                return@launch
            }

            usuarioRepository.cambiarClave(sesion.usuario.id, datos.actual, datos.nueva)
            _estado.value = CambiarClaveUiState(cambiada = true)
        }
    }
}
