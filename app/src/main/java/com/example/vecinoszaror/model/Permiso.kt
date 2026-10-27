package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Acción que puede realizarse en la app (ej. RESERVA_CONFIRMAR, PAGO_REGISTRAR). */
@Entity(
    tableName = "permiso",
    indices = [Index(value = ["codigo"], unique = true)]
)

data class Permiso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigo: String,
    val descripcion: String,
    /** Agrupa permisos por módulo: RESERVAS, FINANZAS, INFORMES, ADMINISTRACION. */
    val modulo: String
)


/** Define qué puede hacer cada rol. */
@Entity(
    tableName = "rol_permiso",
    primaryKeys = ["rolId", "permisoId"],
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["id"],
            childColumns = ["rolId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Permiso::class,
            parentColumns = ["id"],
            childColumns = ["permisoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("permisoId")]
)

data class RolPermiso(
    val rolId: Long,
    val permisoId: Long
)
