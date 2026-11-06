package com.example.vecinoszaror.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.vecinoszaror.database.dao.RecintoDao
import com.example.vecinoszaror.model.Recinto
import kotlinx.coroutines.flow.Flow

class RecintoRepository(private val recintoDao: RecintoDao) {

    fun observarActivos(): Flow<List<Recinto>> = recintoDao.observarActivos()

    fun observarTodos(): Flow<List<Recinto>> = recintoDao.observarTodos()

    suspend fun obtenerPorId(id: Long): Recinto? = recintoDao.obtenerPorId(id)

    /** Devuelve el id, o null si ya existe un recinto con ese nombre. */
    suspend fun crear(recinto: Recinto): Long? =
        recintoDao.insertar(recinto).takeIf { it != -1L }

    /** Guarda los cambios de un recinto. Devuelve false si el nombre ya lo usa otro recinto. */
    suspend fun editar(recinto: Recinto): Boolean = try {
        recintoDao.actualizar(recinto)
        true
    } catch (e: SQLiteConstraintException) {
        // El nombre es único
        false
    }

}
