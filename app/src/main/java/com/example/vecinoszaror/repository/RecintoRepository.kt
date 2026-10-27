package com.example.vecinoszaror.repository

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

    suspend fun actualizar(recinto: Recinto) = recintoDao.actualizar(recinto)
}
