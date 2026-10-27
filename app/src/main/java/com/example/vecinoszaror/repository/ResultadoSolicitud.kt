package com.example.vecinoszaror.repository

/** Resultado de solicitar una reserva: el ViewModel decide qué mensaje mostrar según el caso. */
sealed interface ResultadoSolicitud {
    data class Creada(val reservaId: Long) : ResultadoSolicitud
    data object DatosInvalidos : ResultadoSolicitud
    data object RecintoNoDisponible : ResultadoSolicitud
    data class CapacidadExcedida(val capacidad: Int) : ResultadoSolicitud
    data object HorarioOcupado : ResultadoSolicitud
}
