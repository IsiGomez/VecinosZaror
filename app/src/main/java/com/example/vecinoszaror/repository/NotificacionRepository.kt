package com.example.vecinoszaror.repository

import com.example.vecinoszaror.database.dao.NotificacionDao
import com.example.vecinoszaror.model.Notificacion
import kotlinx.coroutines.flow.Flow

class NotificacionRepository(private val notificacionDao: NotificacionDao) {

    fun observarPorUsuario(usuarioId: Long): Flow<List<Notificacion>> =
        notificacionDao.observarPorUsuario(usuarioId)

    fun observarNoLeidas(usuarioId: Long): Flow<Int> = notificacionDao.observarNoLeidas(usuarioId)

    suspend fun marcarLeida(id: Long) = notificacionDao.marcarLeida(id)

    suspend fun marcarTodasLeidas(usuarioId: Long) = notificacionDao.marcarTodasLeidas(usuarioId)
}
