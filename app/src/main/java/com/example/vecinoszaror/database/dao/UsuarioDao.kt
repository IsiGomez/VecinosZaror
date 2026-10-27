package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.model.UsuarioConRol
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    /** Devuelve el id nuevo / -1 si el email ya existe. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Usuario?

    @Query("SELECT * FROM usuario WHERE email = :email")
    suspend fun obtenerPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuario WHERE email = :email AND claveHash = :claveHash AND activo = 1")
    suspend fun autenticar(email: String, claveHash: String): Usuario?

    @Transaction
    @Query("SELECT * FROM usuario WHERE id = :id")
    suspend fun obtenerConRol(id: Long): UsuarioConRol?

    @Query("SELECT * FROM usuario ORDER BY apellido, nombre")
    fun observarTodos(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE rolId = :rolId ORDER BY apellido, nombre")
    fun observarPorRol(rolId: Long): Flow<List<Usuario>>
}
