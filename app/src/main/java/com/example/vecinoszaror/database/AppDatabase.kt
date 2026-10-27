package com.example.vecinoszaror.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vecinoszaror.database.dao.GastoDao
import com.example.vecinoszaror.database.dao.InformeFinancieroDao
import com.example.vecinoszaror.database.dao.NotificacionDao
import com.example.vecinoszaror.database.dao.PagoDao
import com.example.vecinoszaror.database.dao.PermisoDao
import com.example.vecinoszaror.database.dao.RecintoDao
import com.example.vecinoszaror.database.dao.ReservaDao
import com.example.vecinoszaror.database.dao.RolDao
import com.example.vecinoszaror.database.dao.UsuarioDao
import com.example.vecinoszaror.model.Gasto
import com.example.vecinoszaror.model.InformeFinanciero
import com.example.vecinoszaror.model.Notificacion
import com.example.vecinoszaror.model.Pago
import com.example.vecinoszaror.model.Permiso
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.model.Reserva
import com.example.vecinoszaror.model.Rol
import com.example.vecinoszaror.model.RolPermiso
import com.example.vecinoszaror.model.Usuario

/** Base de datos local (Room/SQLite). */
@Database(
    entities = [
        Rol::class,
        Permiso::class,
        RolPermiso::class,
        Usuario::class,
        Recinto::class,
        Reserva::class,
        Pago::class,
        Gasto::class,
        InformeFinanciero::class,
        Notificacion::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rolDao(): RolDao
    abstract fun permisoDao(): PermisoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun recintoDao(): RecintoDao
    abstract fun reservaDao(): ReservaDao
    abstract fun pagoDao(): PagoDao
    abstract fun gastoDao(): GastoDao
    abstract fun informeFinancieroDao(): InformeFinancieroDao
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        private const val NOMBRE_BD = "vecinos_zaror.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOMBRE_BD
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            DatabaseInitial.poblar(db)
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
