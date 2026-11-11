package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.OlvidoClaveUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.repository.NotificacionRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Avisa a quienes administran usuarios que le asignen una nueva desde Gestión. */
class OlvidoClaveViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(OlvidoClaveUiState())
    val estado: StateFlow<OlvidoClaveUiState> = _estado.asStateFlow()

    fun onEmailChange(valor: String) {
        _estado.update { it.copy(email = valor, errorEmail = null) }
    }

    fun reiniciar() {
        _estado.value = OlvidoClaveUiState()
    }

    fun solicitar() {
        if (_estado.value.enviando) return
        val email = _estado.value.email.trim()

        val error = when {
            email.isEmpty() -> "Ingresa tu correo"
            !EMAIL_REGEX.matches(email) -> "El correo no es válido"
            else -> null
        }
        if (error != null) {
            _estado.update { it.copy(errorEmail = error) }
            return
        }

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerPorEmail(email)
            if (usuario != null && usuario.activo) {
                val administradores = usuarioRepository.obtenerActivosConPermiso(Permisos.USUARIO_GESTIONAR)
                    .filter { it.id != usuario.id }
                if (administradores.isNotEmpty()) {
                    notificacionRepository.enviarAviso(
                        titulo = "Solicitud de nueva contraseña",
                        mensaje = "${usuario.nombre} ${usuario.apellido} (${usuario.email}) pidió una " +
                                "contraseña nueva. Asígnale una desde Gestión → Usuarios.",
                        usuarioIds = administradores.map { it.id }
                    )
                }
            }
            _estado.update { it.copy(enviando = false, enviado = true) }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
