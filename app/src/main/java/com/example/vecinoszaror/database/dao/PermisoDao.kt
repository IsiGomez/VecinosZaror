package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.RolPermiso
import kotlinx.coroutines.flow.Flow

@Dao
interface PermisoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(permiso: Permiso): Long

    @Query("SELECT * FROM permiso ORDER BY modulo, codigo")
    fun observarTodos(): Flow<List<Permiso>>

    // --- Asignación de permisos a roles ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun asignar(rolPermiso: RolPermiso)

    @Query("DELETE FROM rol_permiso WHERE rolId = :rolId AND permisoId = :permisoId")
    suspend fun quitar(rolId: Long, permisoId: Long)

    @Query(
        """
        SELECT p.* FROM permiso p
        INNER JOIN rol_permiso rp ON rp.permisoId = p.id
        WHERE rp.rolId = :rolId
        ORDER BY p.modulo, p.codigo
        """
    )
    fun observarPermisosDeRol(rolId: Long): Flow<List<Permiso>>

    /** true si el rol tiene el permiso indicado. */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM rol_permiso rp
        INNER JOIN permiso p ON p.id = rp.permisoId
        WHERE rp.rolId = :rolId AND p.codigo = :codigo
        """
    )
    suspend fun rolTienePermiso(rolId: Long, codigo: String): Boolean
}
