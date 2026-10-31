package com.example.vecinoszaror.repository

import androidx.room.withTransaction
import com.example.vecinoszaror.database.AppDatabase
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.Notificacion
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.ReservaConDetalle
import com.example.vecinoszaror.model.TipoNotificacion
import kotlinx.coroutines.flow.Flow

class ReservaRepository(private val db: AppDatabase) {

    private val reservaDao = db.reservaDao()
    private val recintoDao = db.recintoDao()
    private val notificacionDao = db.notificacionDao()

    fun observarTodas(): Flow<List<ReservaConDetalle>> = reservaDao.observarTodas()

    fun observarPorUsuario(usuarioId: Long): Flow<List<ReservaConDetalle>> =
        reservaDao.observarPorUsuario(usuarioId)

    fun observarPorEstado(estado: EstadoReserva): Flow<List<ReservaConDetalle>> =
        reservaDao.observarPorEstado(estado)

    fun observarOcupacion(recintoId: Long, desde: Long, hasta: Long): Flow<List<ReservaConDetalle>> =
        reservaDao.observarOcupacion(recintoId, desde, hasta)

    suspend fun estaDisponible(recintoId: Long, inicio: Long, fin: Long): Boolean =
        reservaDao.contarConflictos(recintoId, inicio, fin) == 0

    /**
     * Crea una solicitud PENDIENTE si los datos son válidos, el recinto está activo,
     * cabe la cantidad de asistentes y el horario está libre.
     */
    suspend fun solicitar(
        usuarioId: Long,
        recintoId: Long,
        inicio: Long,
        fin: Long,
        motivo: String,
        numeroAsistentes: Int
    ): ResultadoSolicitud {
        if (fin <= inicio || numeroAsistentes <= 0) return ResultadoSolicitud.DatosInvalidos
        val recinto = recintoDao.obtenerPorId(recintoId)?.takeIf { it.activo }
            ?: return ResultadoSolicitud.RecintoNoDisponible
        if (numeroAsistentes > recinto.capacidadPersonas) {
            return ResultadoSolicitud.CapacidadExcedida(recinto.capacidadPersonas)
        }
        val reserva = Reserva(
            usuarioId = usuarioId,
            recintoId = recintoId,
            fechaHoraInicio = inicio,
            fechaHoraFin = fin,
            motivo = motivo.trim(),
            numeroAsistentes = numeroAsistentes,
            montoTotal = recinto.tarifaBase
        )
        val id = reservaDao.insertarSiDisponible(reserva) ?: return ResultadoSolicitud.HorarioOcupado
        return ResultadoSolicitud.Creada(id)
    }

    /**
     * Confirma o rechaza una reserva PENDIENTE (Secretaría/Directiva) y avisa al vecino.
     * Devuelve false si no existe o ya fue gestionada
     */
    suspend fun responder(
        reservaId: Long,
        gestionadaPorId: Long,
        aceptar: Boolean,
        observacion: String? = null
    ): Boolean = db.withTransaction {
        val reserva = reservaDao.obtenerPorId(reservaId) ?: return@withTransaction false
        if (reserva.estado != EstadoReserva.PENDIENTE) return@withTransaction false
        val nuevoEstado = if (aceptar) EstadoReserva.CONFIRMADA else EstadoReserva.RECHAZADA
        reservaDao.actualizarEstado(reservaId, nuevoEstado, gestionadaPorId, observacion)
        notificacionDao.insertar(
            Notificacion(
                usuarioId = reserva.usuarioId,
                tipo = if (aceptar) TipoNotificacion.RESERVA_CONFIRMADA else TipoNotificacion.RESERVA_RECHAZADA,
                titulo = if (aceptar) "Reserva confirmada" else "Reserva rechazada",
                mensaje = observacion ?: if (aceptar) "Tu solicitud fue aprobada." else "Tu solicitud no pudo ser aprobada.",
                reservaId = reservaId
            )
        )
        true
    }

    /** Cancela una reserva PENDIENTE o CONFIRMADA / false si no existe o ya terminó. */
    suspend fun cancelar(
        reservaId: Long,
        gestionadaPorId: Long?,
        observacion: String? = null
    ): Boolean = db.withTransaction {
        val reserva = reservaDao.obtenerPorId(reservaId) ?: return@withTransaction false
        if (reserva.estado != EstadoReserva.PENDIENTE && reserva.estado != EstadoReserva.CONFIRMADA) {
            return@withTransaction false
        }
        reservaDao.actualizarEstado(reservaId, EstadoReserva.CANCELADA, gestionadaPorId, observacion)
        /** Si cancela la directiva, se le avisa al vecino */
        if (gestionadaPorId != null && gestionadaPorId != reserva.usuarioId) {
            notificacionDao.insertar(
                Notificacion(
                    usuarioId = reserva.usuarioId,
                    tipo = TipoNotificacion.GENERAL,
                    titulo = "Reserva cancelada",
                    mensaje = observacion ?: "Tu reserva fue cancelada por la Directiva.",
                    reservaId = reservaId
                )
            )
        }
        true
    }
}
