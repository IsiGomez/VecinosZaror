package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vecinoszaror.model.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(notificacion: Notificacion): Long

    @Query("SELECT * FROM notificacion WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<Notificacion>>

    @Query("SELECT COUNT(*) FROM notificacion WHERE usuarioId = :usuarioId AND leida = 0")
    fun observarNoLeidas(usuarioId: Long): Flow<Int>

    @Query("UPDATE notificacion SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Long)

    @Query("UPDATE notificacion SET leida = 1 WHERE usuarioId = :usuarioId")
    suspend fun marcarTodasLeidas(usuarioId: Long)
}