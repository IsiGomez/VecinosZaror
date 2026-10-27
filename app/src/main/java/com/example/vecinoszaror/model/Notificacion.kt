package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** (confirmación de reserva, pago, recordatorio, informe). */
@Entity(
    tableName = "notificacion",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Reserva::class,
            parentColumns = ["id"],
            childColumns = ["reservaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("usuarioId"),
        Index("reservaId")
    ]
)

data class Notificacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
    val reservaId: Long? = null,
    val leida: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)