package com.example.vecinoszaror

import android.app.Application
import com.example.vecinoszaror.database.AppDatabase
import com.example.vecinoszaror.repository.FinanzasRepository
import com.example.vecinoszaror.repository.NotificacionRepository
import com.example.vecinoszaror.repository.RecintoRepository
import com.example.vecinoszaror.repository.ReservaRepository
import com.example.vecinoszaror.repository.RolPermisoRepository
import com.example.vecinoszaror.repository.UsuarioRepository

/** Se crean la base de datos y los repositories. */
class VecinosZarorApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val rolPermisoRepository by lazy { RolPermisoRepository(database.rolDao(), database.permisoDao()) }
    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
    val recintoRepository by lazy { RecintoRepository(database.recintoDao()) }
    val reservaRepository by lazy { ReservaRepository(database) }
    val finanzasRepository by lazy { FinanzasRepository(database) }
    val notificacionRepository by lazy { NotificacionRepository(database.notificacionDao()) }
}
