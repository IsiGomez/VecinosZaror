package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.Gasto
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Insert
    suspend fun insertar(gasto: Gasto): Long

    @Query("SELECT * FROM gasto WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Gasto?

    @Query("SELECT * FROM gasto ORDER BY fechaGasto DESC")
    fun observarTodos(): Flow<List<Gasto>>

    @Query("SELECT * FROM gasto WHERE categoria = :categoria ORDER BY fechaGasto DESC")
    fun observarPorCategoria(categoria: CategoriaGasto): Flow<List<Gasto>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gasto WHERE fechaGasto BETWEEN :desde AND :hasta")
    suspend fun totalGastos(desde: Long, hasta: Long): Long

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gasto")
    fun observarTotalGastos(): Flow<Long>
}
