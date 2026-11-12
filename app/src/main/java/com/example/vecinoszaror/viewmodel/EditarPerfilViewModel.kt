package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.uistate.EditarPerfilErrores
import com.example.vecinoszaror.model.uistate.EditarPerfilUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.repository.UsuarioRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario con el que cada persona edita su nombre, apellido y teléfono. */
class EditarPerfilViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(EditarPerfilUiState())
    val estado: StateFlow<EditarPerfilUiState> = _estado.asStateFlow()

    /** Avisa con el usuario ya guardado, para que la pantalla actualice la sesión. */
    private val _actualizado = Channel<Usuario>(Channel.BUFFERED)
    val actualizado: Flow<Usuario> = _actualizado.receiveAsFlow()

    /** Antes de abrir la pantalla, llena el formulario con los datos actuales. */
    fun cargar(sesion: SesionUsuario) {
        val usuario = sesion.usuario
        _estado.value = EditarPerfilUiState(
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            telefono = usuario.telefono.orEmpty()
        )
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null, general = null)) }
    }

    fun onApellidoChange(valor: String) {
        _estado.update { it.copy(apellido = valor, errores = it.errores.copy(apellido = null, general = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update {
            it.copy(
                telefono = valor.take(EditarPerfilUiState.MAX_TELEFONO),
                errores = it.errores.copy(telefono = null, general = null)
            )
        }
    }

    fun guardar(sesion: SesionUsuario) {
        if (_estado.value.guardando) return
        val datos = _estado.value

        val telefono = datos.telefono.trim()
        val errores = EditarPerfilErrores(
            nombre = if (datos.nombre.isBlank()) "Escribe tu nombre" else null,
            apellido = if (datos.apellido.isBlank()) "Escribe tu apellido" else null,
            telefono = if (telefono.isNotEmpty() && !telefonoValido(telefono)) "El teléfono no es válido" else null
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores) return

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val usuario = usuarioRepository.actualizarPerfil(
                usuarioId = sesion.usuario.id,
                nombre = datos.nombre,
                apellido = datos.apellido,
                telefono = telefono
            )
            if (usuario == null) {
                _estado.update {
                    it.copy(guardando = false, errores = it.errores.copy(general = "No se pudo guardar tu perfil"))
                }
            } else {
                _estado.update { it.copy(guardando = false) }
                _actualizado.send(usuario)
            }
        }
    }

    private fun telefonoValido(telefono: String): Boolean {
        val soloCaracteresValidos = telefono.all { it.isDigit() || it in "+-() " }
        return soloCaracteresValidos && telefono.count { it.isDigit() } in 8..11
    }
}
