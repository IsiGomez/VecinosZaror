package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Destinatario
import com.example.vecinoszaror.model.uistate.EnviarAvisoErrores
import com.example.vecinoszaror.model.uistate.EnviarAvisoUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.NotificacionRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que se envía un aviso. */
class EnviarAvisoViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EnviarAvisoUiState())
    val estado: StateFlow<EnviarAvisoUiState> = _estado.asStateFlow()

    fun onDestinatarioChange(destinatario: Destinatario) {
        _estado.update { it.copy(destinatario = destinatario, errores = it.errores.copy(general = null)) }
    }

    fun onTituloChange(valor: String) {
        _estado.update {
            it.copy(
                titulo = valor.take(EnviarAvisoUiState.MAX_TITULO),
                errores = it.errores.copy(titulo = null, general = null)
            )
        }
    }

    fun onMensajeChange(valor: String) {
        _estado.update {
            it.copy(
                mensaje = valor.take(EnviarAvisoUiState.MAX_MENSAJE),
                errores = it.errores.copy(mensaje = null, general = null)
            )
        }
    }

    fun reiniciar() {
        _estado.value = EnviarAvisoUiState()
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.AVISO_ENVIAR)) return
        val datos = _estado.value

        val errores = EnviarAvisoErrores(
            titulo = if (datos.titulo.isBlank()) "Escribe un título" else null,
            mensaje = if (datos.mensaje.isBlank()) "Escribe el mensaje" else null
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val destinatarios = usuarioRepository.obtenerActivosPorRoles(datos.destinatario.roles)
                .filter { it.id != sesion.usuario.id }

            if (destinatarios.isEmpty()) {
                _estado.update {
                    it.copy(enviando = false, errores = it.errores.copy(general = "No hay usuarios activos que reciban este aviso."))
                }
                return@launch
            }

            val enviados = notificacionRepository.enviarAviso(
                titulo = datos.titulo.trim(),
                mensaje = datos.mensaje.trim(),
                usuarioIds = destinatarios.map { it.id }
            )
            _estado.update { it.copy(enviando = false, enviadoA = enviados) }
        }
    }
}
