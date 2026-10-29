package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.LoginUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.RolPermisoRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario de login y la sesión del usuario (quién es y qué puede hacer). */
class AuthViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val rolPermisoRepository: RolPermisoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    private val _sesion = MutableStateFlow<SesionUsuario?>(null)
    val sesion: StateFlow<SesionUsuario?> = _sesion.asStateFlow()

    /** Al escribir en un campo se actualiza su valor y se limpian sus errores */
    fun onEmailChange(valor: String) {
        _estado.update { it.copy(email = valor, errores = it.errores.copy(email = null, general = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, general = null)) }
    }

    fun iniciarSesion() {
        if (_estado.value.cargando || !validarFormulario()) return
        _estado.update { it.copy(cargando = true) }

        viewModelScope.launch {
            val datos = _estado.value
            val usuario = usuarioRepository.iniciarSesion(datos.email, datos.clave)
            if (usuario == null) {
                mostrarErrorGeneral("Correo o clave incorrectos")
                return@launch
            }
            val rolConPermisos = rolPermisoRepository.obtenerRolConPermisos(usuario.rolId)
            if (rolConPermisos == null) {
                mostrarErrorGeneral("No se pudo cargar el perfil del usuario")
                return@launch
            }
            _sesion.value = SesionUsuario(
                usuario = usuario,
                rol = rolConPermisos.rol,
                permisos = rolConPermisos.permisos.map { it.codigo }.toSet()
            )
            _estado.value = LoginUiState() // limpia el formulario
        }
    }

    fun cerrarSesion() {
        _sesion.value = null
        _estado.value = LoginUiState()
    }

    /** Valida los campos, si hay errores los deja en el estado y devuelve false. */
    private fun validarFormulario(): Boolean {
        val datos = _estado.value
        val errorEmail = when {
            datos.email.isBlank() -> "Ingresa tu correo"
            !EMAIL_REGEX.matches(datos.email.trim()) -> "El correo no es válido"
            else -> null
        }
        val errorClave = when {
            datos.clave.isBlank() -> "Ingresa tu clave"
            datos.clave.length < CLAVE_MINIMA -> "La clave debe tener al menos $CLAVE_MINIMA caracteres"
            else -> null
        }
        _estado.update { it.copy(errores = it.errores.copy(email = errorEmail, clave = errorClave)) }
        return errorEmail == null && errorClave == null
    }

    private fun mostrarErrorGeneral(mensaje: String) {
        _estado.update { it.copy(cargando = false, errores = it.errores.copy(general = mensaje)) }
    }

    private companion object {
        const val CLAVE_MINIMA = 6
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
