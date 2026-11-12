package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.model.uistate.RecintoFormularioErrores
import com.example.vecinoszaror.model.uistate.RecintoFormularioUiState
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.RecintoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja la ventana para crear o editar un recinto. */
class RecintoFormularioViewModel(
    private val recintoRepository: RecintoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RecintoFormularioUiState())
    val estado: StateFlow<RecintoFormularioUiState> = _estado.asStateFlow()

    /** Abre la ventana vacía para crear un recinto. */
    fun nuevo() {
        _estado.value = RecintoFormularioUiState(abierto = true)
    }

    /** Abre la ventana con los datos del recinto para editarlo. */
    fun editar(recinto: Recinto) {
        _estado.value = RecintoFormularioUiState(
            abierto = true,
            recintoId = recinto.id,
            nombre = recinto.nombre,
            descripcion = recinto.descripcion,
            capacidad = recinto.capacidadPersonas.toString(),
            tarifa = recinto.tarifaBase.toString(),
            activo = recinto.activo
        )
    }

    /** Cierra la ventana y deja el formulario vacío. */
    fun cerrar() {
        _estado.value = RecintoFormularioUiState()
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null, general = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor) }
    }

    fun onCapacidadChange(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(capacidad = soloNumeros, errores = it.errores.copy(capacidad = null)) }
    }

    fun onTarifaChange(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(tarifa = soloNumeros, errores = it.errores.copy(tarifa = null)) }
    }

    fun onActivoChange(activo: Boolean) {
        _estado.update { it.copy(activo = activo) }
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando || !sesion.tienePermiso(Permisos.RECINTO_GESTIONAR)) return
        val datos = _estado.value

        // Copias locales para que el compilador pueda asegurar que no son null
        val capacidad = datos.capacidad.toIntOrNull()
        val tarifa = datos.tarifa.toLongOrNull()
        val errores = RecintoFormularioErrores(
            nombre = if (datos.nombre.isBlank()) "Escribe el nombre del recinto" else null,
            capacidad = if (capacidad == null || capacidad <= 0) "Ingresa una capacidad mayor a 0" else null,
            tarifa = if (tarifa == null) "Ingresa la tarifa (0 si es gratis)" else null
        )
        _estado.update { it.copy(errores = errores) }
        if (errores.hayErrores || capacidad == null || tarifa == null) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val recintoId = datos.recintoId
            val guardado = if (recintoId == null) {
                // Devuelve null si el nombre del recinto ya existe
                recintoRepository.crear(
                    Recinto(
                        nombre = datos.nombre.trim(),
                        descripcion = datos.descripcion.trim(),
                        capacidadPersonas = capacidad,
                        tarifaBase = tarifa
                    )
                ) != null
            } else {
                recintoRepository.editar(
                    Recinto(
                        id = recintoId,
                        nombre = datos.nombre.trim(),
                        descripcion = datos.descripcion.trim(),
                        capacidadPersonas = capacidad,
                        tarifaBase = tarifa,
                        activo = datos.activo
                    )
                )
            }

            if (guardado) {
                cerrar()
            } else {
                _estado.update {
                    it.copy(
                        enviando = false,
                        errores = it.errores.copy(general = "No se pudo guardar. Revisa que no exista otro recinto con ese nombre.")
                    )
                }
            }
        }
    }
}
