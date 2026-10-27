package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** `comprobanteUri` guarda la ruta/URI de la foto. */
@Entity(
    tableName = "pago",
    foreignKeys = [
        ForeignKey(
            entity = Reserva::class,
            parentColumns = ["id"],
            childColumns = ["reservaId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["registradoPorId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("reservaId"),
        Index("registradoPorId"),
        Index("fechaPago")
    ]
)

data class Pago(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reservaId: Long,
    val monto: Long,
    val metodo: MetodoPago = MetodoPago.EFECTIVO,
    val fechaPago: Long = System.currentTimeMillis(),
    val comprobanteUri: String? = null,
    val observacion: String? = null,
    val registradoPorId: Long
)
