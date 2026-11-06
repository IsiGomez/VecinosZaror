package com.example.vecinoszaror.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.model.UsuarioFormularioErrores
import com.example.vecinoszaror.model.UsuarioFormularioUiState
import com.example.vecinoszaror.repository.RolPermisoRepository
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la ventana para crear o editar un usuario. */
class UsuarioFormularioViewModel(
    private val usuarioRepository: UsuarioRepository,
    rolPermisoRepository: RolPermisoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioFormularioUiState())
    val estado: StateFlow<UsuarioFormularioUiState> = _estado.asStateFlow()

    init {
        // Los roles se mantienen al día
        viewModelScope.launch {
            rolPermisoRepository.observarRoles().collect { roles ->
                _estado.update { it.copy(roles = roles) }
            }
        }
    }

    /** Abre la ventana vacía para crear un usuario. */
    fun nuevo() {
        _estado.update {
            UsuarioFormularioUiState(abierto = true, roles = it.roles, rolId = it.roles.firstOrNull()?.id)
        }
    }

    /** Abre la ventana con los datos del usuario para editarlo. */
    fun editar(usuario: Usuario, sesion: SesionUsuario) {
        _estado.update {
            UsuarioFormularioUiState(
                abierto = true,
                usuarioId = usuario.id,
                nombre = usuario.nombre,
                apellido = usuario.apellido,
                email = usuario.email,
                telefono = usuario.telefono.orEmpty(),
                rolId = usuario.rolId,
                activo = usuario.activo,
                esPropio = usuario.id == sesion.usuario.id,
                roles = it.roles
            )
        }
    }

    /** Cierra la ventana y deja el formulario vacío. */
    fun cerrar() {
        _estado.update { UsuarioFormularioUiState(roles = it.roles) }
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null, general = null)) }
    }

    fun onApellidoChange(valor: String) {
        _estado.update { it.copy(apellido = valor, errores = it.errores.copy(apellido = null, general = null)) }
    }

    fun onEmailChange(valor: String) {
        _estado.update { it.copy(email = valor, errores = it.errores.copy(email = null, general = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, general = null)) }
    }

    fun onRolChange(rolId: Long) {
        _estado.update { it.copy(rolId = rolId, errores = it.errores.copy(rol = null)) }
    }

    fun onActivoChange(activo: Boolean) {
        _estado.update { it.copy(activo = activo) }
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.USUARIO_GESTIONAR)) return
        val datos = _estado.value

        val clave = datos.clave
        val errores = UsuarioFormularioErrores(
            nombre = if (datos.nombre.isBlank()) "Escribe el nombre" else null,
            apellido = if (datos.apellido.isBlank()) "Escribe el apellido" else null,
            email = if (!Patterns.EMAIL_ADDRESS.matcher(datos.email.trim()).matches()) "Ingresa un correo válido" else null,
            // Al crear la clave es obligatoria / al editar se revisa si se escribió una nueva
            clave = if ((datos.esNuevo || clave.isNotEmpty()) && clave.length < 6) {
                "La clave debe tener al menos 6 caracteres"
            } else null,
            rol = if (datos.rolId == null) "Elige un rol" else null
        )
        _estado.update { it.copy(errores = errores) }
        val rolId = datos.rolId
        if (errores.hayErrores || rolId == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val telefono = datos.telefono.trim().takeIf { it.isNotEmpty() }
            val usuarioId = datos.usuarioId
            val guardado = if (usuarioId == null) {
                // Devuelve null si el correo ya existe
                usuarioRepository.registrar(
                    nombre = datos.nombre,
                    apellido = datos.apellido,
                    email = datos.email,
                    telefono = telefono,
                    clave = clave,
                    rolId = rolId
                ) != null
            } else {
                val original = usuarioRepository.obtenerPorId(usuarioId)
                if (original == null) {
                    false
                } else {
                    usuarioRepository.editar(
                        usuario = original.copy(
                            nombre = datos.nombre,
                            apellido = datos.apellido,
                            email = datos.email,
                            telefono = telefono,
                            // Para no quedarse sin acceso, nadie puede cambiar su propio rol ni desactivarse
                            rolId = if (datos.esPropio) original.rolId else rolId,
                            activo = if (datos.esPropio) original.activo else datos.activo
                        ),
                        nuevaClave = clave.takeIf { it.isNotEmpty() }
                    )
                }
            }

            if (guardado) {
                cerrar()
            } else {
                _estado.update {
                    it.copy(
                        enviando = false,
                        errores = it.errores.copy(general = "No se pudo guardar. Revisa que el correo no lo use otra persona.")
                    )
                }
            }
        }
    }
}
