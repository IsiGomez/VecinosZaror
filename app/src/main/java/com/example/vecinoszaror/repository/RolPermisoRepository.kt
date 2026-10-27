package com.example.vecinoszaror.repository

import com.example.vecinoszaror.database.dao.PermisoDao
import com.example.vecinoszaror.database.dao.RolDao
import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.Rol
import com.example.vecinoszaror.model.RolConPermisos
import com.example.vecinoszaror.model.RolPermiso
import kotlinx.coroutines.flow.Flow

/** Roles, permisos y su relación. */
class RolPermisoRepository(
    private val rolDao: RolDao,
    private val permisoDao: PermisoDao
) {
    fun observarRoles(): Flow<List<Rol>> = rolDao.observarTodos()

    fun observarRolesConPermisos(): Flow<List<RolConPermisos>> = rolDao.observarTodosConPermisos()

    fun observarPermisos(): Flow<List<Permiso>> = permisoDao.observarTodos()

    fun observarPermisosDeRol(rolId: Long): Flow<List<Permiso>> = permisoDao.observarPermisosDeRol(rolId)

    suspend fun obtenerRolConPermisos(rolId: Long): RolConPermisos? = rolDao.obtenerConPermisos(rolId)

    /** ¿El rol tiene el permiso? Ej: puede(rolId, "PAGO_REGISTRAR"). */
    suspend fun puede(rolId: Long, codigoPermiso: String): Boolean =
        permisoDao.rolTienePermiso(rolId, codigoPermiso)

    suspend fun asignarPermiso(rolId: Long, permisoId: Long) =
        permisoDao.asignar(RolPermiso(rolId, permisoId))

    suspend fun quitarPermiso(rolId: Long, permisoId: Long) =
        permisoDao.quitar(rolId, permisoId)
}
