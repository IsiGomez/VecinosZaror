package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reserva",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Recinto::class,
            parentColumns = ["id"],
            childColumns = ["recintoId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["gestionadaPorId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("usuarioId"),
        Index("gestionadaPorId"),
        Index(value = ["recintoId", "fechaHoraInicio", "fechaHoraFin"])
    ]
)

data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Vecino que solicita. */
    val usuarioId: Long,
    val recintoId: Long,
    val fechaHoraInicio: Long,
    val fechaHoraFin: Long,
    val motivo: String,
    val numeroAsistentes: Int,
    val estado: EstadoReserva = EstadoReserva.PENDIENTE,
    val montoTotal: Long = 0,

    /** Secretaría/Directiva que confirmó o rechazó la solicitud. */
    val gestionadaPorId: Long? = null,
    val observacion: String? = null,
    val fechaSolicitud: Long = System.currentTimeMillis()
)
