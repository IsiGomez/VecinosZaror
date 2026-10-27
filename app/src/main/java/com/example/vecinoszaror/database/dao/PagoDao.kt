package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vecinoszaror.model.Pago
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {

    @Insert
    suspend fun insertar(pago: Pago): Long

    @Query("SELECT * FROM pago WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Pago?

    @Query("SELECT * FROM pago ORDER BY fechaPago DESC")
    fun observarTodos(): Flow<List<Pago>>

    @Query("SELECT * FROM pago WHERE reservaId = :reservaId ORDER BY fechaPago DESC")
    fun observarPorReserva(reservaId: Long): Flow<List<Pago>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM pago WHERE reservaId = :reservaId")
    suspend fun totalPagadoDeReserva(reservaId: Long): Long

    @Query("SELECT COALESCE(SUM(monto), 0) FROM pago WHERE fechaPago BETWEEN :desde AND :hasta")
    suspend fun totalIngresos(desde: Long, hasta: Long): Long

    @Query("SELECT COALESCE(SUM(monto), 0) FROM pago")
    fun observarTotalIngresos(): Flow<Long>
}
