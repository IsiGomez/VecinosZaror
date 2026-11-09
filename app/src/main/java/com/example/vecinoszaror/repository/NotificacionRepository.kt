package com.example.vecinoszaror.repository

import com.example.vecinoszaror.database.dao.NotificacionDao
import com.example.vecinoszaror.model.Notificacion
import com.example.vecinoszaror.model.TipoNotificacion
import kotlinx.coroutines.flow.Flow

class NotificacionRepository(private val notificacionDao: NotificacionDao) {

    fun observarPorUsuario(usuarioId: Long): Flow<List<Notificacion>> =
        notificacionDao.observarPorUsuario(usuarioId)

    fun observarNoLeidas(usuarioId: Long): Flow<Int> = notificacionDao.observarNoLeidas(usuarioId)

    /** Crea un aviso para cada usuario de la lista y devuelve cuántos envió. */
    suspend fun enviarAviso(titulo: String, mensaje: String, usuarioIds: List<Long>): Int {
        val avisos = usuarioIds.map { usuarioId ->
            Notificacion(
                usuarioId = usuarioId,
                tipo = TipoNotificacion.GENERAL,
                titulo = titulo,
                mensaje = mensaje
            )
        }
        notificacionDao.insertarTodas(avisos)
        return avisos.size
    }

    suspend fun marcarLeida(id: Long) = notificacionDao.marcarLeida(id)

    suspend fun marcarTodasLeidas(usuarioId: Long) = notificacionDao.marcarTodasLeidas(usuarioId)
}
