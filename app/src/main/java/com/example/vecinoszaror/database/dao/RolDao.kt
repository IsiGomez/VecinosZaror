package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.vecinoszaror.model.Rol
import com.example.vecinoszaror.model.RolConPermisos
import kotlinx.coroutines.flow.Flow

@Dao
interface RolDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(rol: Rol): Long

    @Query("SELECT * FROM rol ORDER BY id")
    fun observarTodos(): Flow<List<Rol>>

    @Query("SELECT * FROM rol WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Rol?

    @Query("SELECT * FROM rol WHERE nombre = :nombre")
    suspend fun obtenerPorNombre(nombre: String): Rol?

    @Transaction
    @Query("SELECT * FROM rol WHERE id = :id")
    suspend fun obtenerConPermisos(id: Long): RolConPermisos?

    @Transaction
    @Query("SELECT * FROM rol ORDER BY id")
    fun observarTodosConPermisos(): Flow<List<RolConPermisos>>
}