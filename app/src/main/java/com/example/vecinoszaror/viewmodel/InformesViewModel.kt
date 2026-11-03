package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.InformeFinanciero
import com.example.vecinoszaror.model.InformesUiState
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.repository.FinanzasRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Maneja la lista de informes. */
class InformesViewModel(
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(InformesUiState())
    val estado: StateFlow<InformesUiState> = _estado.asStateFlow()

    // Tarea que escucha la base, se cancela al cambiar de usuario
    private var tareaEscucha: Job? = null

    /** Empieza a observar los informes que le corresponden al rol. */
    fun cargar(sesion: SesionUsuario) {
        tareaEscucha?.cancel()
        val puedePublicar = sesion.tienePermiso(Permisos.INFORME_PUBLICAR)
        val puedeGenerar = sesion.tienePermiso(Permisos.INFORME_GENERAR)
        val veBorradores = puedePublicar || puedeGenerar
        _estado.value = InformesUiState(
            veBorradores = veBorradores,
            puedePublicar = puedePublicar,
            puedeGenerar = puedeGenerar
        )

        tareaEscucha = viewModelScope.launch {
            val informes = if (veBorradores) {
                finanzasRepository.observarInformes()
            } else {
                finanzasRepository.observarInformesPublicados()
            }
            informes.collect { lista ->
                _estado.value = _estado.value.copy(informes = lista, cargando = false)
            }
        }
    }

    /** Publica el informe si estaba en borrador, o quita la publicación si ya estaba publicado. */
    fun cambiarPublicacion(informe: InformeFinanciero) {
        if (!_estado.value.puedePublicar) return
        viewModelScope.launch {
            finanzasRepository.cambiarPublicacion(informe.id, !informe.publicado)
        }
    }
}
