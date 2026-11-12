package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vecinoszaror.model.Usuario
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

    /** Usuarios activos cuyo rol (nombre) está en la lista. */
    @Query(
        "SELECT usuario.* FROM usuario INNER JOIN rol ON usuario.rolId = rol.id " +
        "WHERE rol.nombre IN (:nombresRol) AND usuario.activo = 1"
    )
    suspend fun obtenerActivosPorRoles(nombresRol: List<String>): List<Usuario>

    /** Usuarios activos cuyo rol tiene el permiso con ese código. */
    @Query(
        "SELECT usuario.* FROM usuario " +
                "INNER JOIN rol_permiso ON usuario.rolId = rol_permiso.rolId " +
                "INNER JOIN permiso ON rol_permiso.permisoId = permiso.id " +
                "WHERE permiso.codigo = :codigoPermiso AND usuario.activo = 1"
    )
    suspend fun obtenerActivosConPermiso(codigoPermiso: String): List<Usuario>

    @Query("SELECT * FROM usuario ORDER BY apellido, nombre")
    fun observarTodos(): Flow<List<Usuario>>
}
