package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vecinoszaror.model.Recinto
import kotlinx.coroutines.flow.Flow

@Dao
interface RecintoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(recinto: Recinto): Long

    @Update
    suspend fun actualizar(recinto: Recinto)

    @Query("SELECT * FROM recinto WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Recinto?

    @Query("SELECT * FROM recinto WHERE activo = 1 ORDER BY nombre")
    fun observarActivos(): Flow<List<Recinto>>

    @Query("SELECT * FROM recinto ORDER BY nombre")
    fun observarTodos(): Flow<List<Recinto>>
}
