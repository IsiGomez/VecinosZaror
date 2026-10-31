package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.model.SolicitudErrores
import com.example.vecinoszaror.model.SolicitudReservaUiState
import com.example.vecinoszaror.repository.RecintoRepository
import com.example.vecinoszaror.repository.ReservaRepository
import com.example.vecinoszaror.repository.ResultadoSolicitud
import com.example.vecinoszaror.util.combinarFechaYHora
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Maneja el formulario para que un vecino solicite el arriendo de un recinto. */
class SolicitudReservaViewModel(
    private val reservaRepository: ReservaRepository,
    recintoRepository: RecintoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(SolicitudReservaUiState())
    val estado: StateFlow<SolicitudReservaUiState> = _estado.asStateFlow()

    /** Aviso de una sola vez */
    private val _reservaCreada = Channel<Unit>(Channel.BUFFERED)
    val reservaCreada: Flow<Unit> = _reservaCreada.receiveAsFlow()

    init {
        viewModelScope.launch {
            recintoRepository.observarActivos().collect { lista ->
                _estado.update { it.copy(recintos = lista) }
            }
        }
    }

    /** Al escribir en un campo se actualiza su valor y se limpian sus errores */
    fun onRecintoChange(id: Long) {
        _estado.update { it.copy(recintoId = id, errores = it.errores.copy(recinto = null, general = null)) }
    }

    fun onFechaChange(fecha: Long) {
        _estado.update { it.copy(fecha = fecha, errores = it.errores.copy(fecha = null, general = null)) }
    }

    fun onHoraInicioChange(minutos: Int) {
        _estado.update { it.copy(horaInicioMinutos = minutos, errores = it.errores.copy(horaInicio = null, general = null)) }
    }

    fun onHoraFinChange(minutos: Int) {
        _estado.update { it.copy(horaFinMinutos = minutos, errores = it.errores.copy(horaFin = null, general = null)) }
    }

    fun onMotivoChange(valor: String) {
        _estado.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null, general = null)) }
    }

    fun onAsistentesChange(valor: String) {
        val soloNumeros = valor.filter { it.isDigit() }
        _estado.update { it.copy(asistentes = soloNumeros, errores = it.errores.copy(asistentes = null, general = null)) }
    }

    fun reiniciar() {
        _estado.update { SolicitudReservaUiState(recintos = it.recintos) }
    }

    fun enviar(sesion: SesionUsuario) {
        if (_estado.value.enviando) return
        val datos = _estado.value

        val errores = validar(datos)
        _estado.update { it.copy(errores = errores) }

        val recintoId = datos.recintoId
        val fecha = datos.fecha
        val horaInicio = datos.horaInicioMinutos
        val horaFin = datos.horaFinMinutos
        val asistentes = datos.asistentes.toIntOrNull()
        if (errores.hayErrores || recintoId == null || fecha == null ||
            horaInicio == null || horaFin == null || asistentes == null
        ) return

        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            val resultado = reservaRepository.solicitar(
                usuarioId = sesion.usuario.id,
                recintoId = recintoId,
                inicio = combinarFechaYHora(fecha, horaInicio),
                fin = combinarFechaYHora(fecha, horaFin),
                motivo = datos.motivo,
                numeroAsistentes = asistentes
            )
            when (resultado) {
                is ResultadoSolicitud.Creada -> {
                    reiniciar()
                    _reservaCreada.send(Unit)
                }
                ResultadoSolicitud.DatosInvalidos -> mostrarErrorGeneral("Revisa los datos ingresados")
                ResultadoSolicitud.RecintoNoDisponible -> mostrarErrorGeneral("El recinto no está disponible")
                ResultadoSolicitud.HorarioOcupado -> mostrarErrorGeneral("El recinto ya está reservado en ese horario")
                is ResultadoSolicitud.CapacidadExcedida -> _estado.update {
                    it.copy(
                        enviando = false,
                        errores = it.errores.copy(asistentes = "Capacidad máxima: ${resultado.capacidad} personas")
                    )
                }
            }
        }
    }

    /** Revisa cada campo y devuelve los mensajes de error (vacío si todo está bien). */
    private fun validar(datos: SolicitudReservaUiState): SolicitudErrores {
        val recinto = datos.recintos.firstOrNull { it.id == datos.recintoId }
        val numeroAsistentes = datos.asistentes.toIntOrNull()
        val fecha = datos.fecha
        val horaInicio = datos.horaInicioMinutos
        val horaFin = datos.horaFinMinutos

        val errorRecinto = if (recinto == null) "Elige un recinto" else null
        val errorFecha = when {
            fecha == null -> "Elige una fecha"
            horaInicio != null && combinarFechaYHora(fecha, horaInicio) < System.currentTimeMillis() ->
                "La reserva debe ser en una fecha futura"
            else -> null
        }
        val errorHoraInicio = if (horaInicio == null) "Elige la hora de inicio" else null
        val errorHoraFin = when {
            horaFin == null -> "Elige la hora de término"
            horaInicio != null && horaFin <= horaInicio -> "Debe ser posterior a la hora de inicio"
            else -> null
        }

        val errorMotivo = if (datos.motivo.isBlank()) "Indica el motivo del arriendo" else null
        val errorAsistentes = when {
            numeroAsistentes == null || numeroAsistentes <= 0 -> "Ingresa un número de asistentes"
            recinto != null && numeroAsistentes > recinto.capacidadPersonas ->
                "Capacidad máxima: ${recinto.capacidadPersonas} personas"
            else -> null
        }

        return SolicitudErrores(
            recinto = errorRecinto,
            fecha = errorFecha,
            horaInicio = errorHoraInicio,
            horaFin = errorHoraFin,
            motivo = errorMotivo,
            asistentes = errorAsistentes
        )
    }

    private fun mostrarErrorGeneral(mensaje: String) {
        _estado.update { it.copy(enviando = false, errores = it.errores.copy(general = mensaje)) }
    }
}
