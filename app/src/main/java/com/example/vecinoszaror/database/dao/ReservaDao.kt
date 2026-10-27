package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.ReservaConDetalle
import kotlinx.coroutines.flow.Flow

/** Verifica disponibilidad e insertar sin que otra reserva se cuele en medio. */
@Dao
abstract class ReservaDao {

    @Insert
    abstract suspend fun insertar(reserva: Reserva): Long

    @Query("UPDATE reserva SET estado = :estado, gestionadaPorId = :gestionadaPorId, observacion = :observacion WHERE id = :id")
    abstract suspend fun actualizarEstado(
        id: Long,
        estado: EstadoReserva,
        gestionadaPorId: Long?,
        observacion: String?
    )

    @Query("SELECT * FROM reserva WHERE id = :id")
    abstract suspend fun obtenerPorId(id: Long): Reserva?

    @Query(
        """
        SELECT COUNT(*) FROM reserva
        WHERE recintoId = :recintoId
          AND estado IN ('PENDIENTE', 'CONFIRMADA')
          AND fechaHoraInicio < :fin
          AND fechaHoraFin > :inicio
        """
    )
    abstract suspend fun contarConflictos(recintoId: Long, inicio: Long, fin: Long): Int

    /** Inserta solo si el recinto está libre en ese horario. */
    @Transaction
    open suspend fun insertarSiDisponible(reserva: Reserva): Long? {
        val conflictos = contarConflictos(reserva.recintoId, reserva.fechaHoraInicio, reserva.fechaHoraFin)
        return if (conflictos == 0) insertar(reserva) else null
    }

    @Transaction
    @Query("SELECT * FROM reserva WHERE usuarioId = :usuarioId ORDER BY fechaHoraInicio DESC")
    abstract fun observarPorUsuario(usuarioId: Long): Flow<List<ReservaConDetalle>>

    @Transaction
    @Query("SELECT * FROM reserva ORDER BY fechaHoraInicio DESC")
    abstract fun observarTodas(): Flow<List<ReservaConDetalle>>

    @Transaction
    @Query("SELECT * FROM reserva WHERE estado = :estado ORDER BY fechaHoraInicio")
    abstract fun observarPorEstado(estado: EstadoReserva): Flow<List<ReservaConDetalle>>

    /** Reservas que tocan el rango [desde, hasta). */
    @Transaction
    @Query(
        """
        SELECT * FROM reserva
        WHERE recintoId = :recintoId
          AND estado IN ('PENDIENTE', 'CONFIRMADA')
          AND fechaHoraInicio < :hasta
          AND fechaHoraFin > :desde
        ORDER BY fechaHoraInicio
        """
    )
    abstract fun observarOcupacion(recintoId: Long, desde: Long, hasta: Long): Flow<List<ReservaConDetalle>>
}
