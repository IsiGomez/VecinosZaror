package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Junta de Vecinos (consumos básicos, mantención, etc.).
 * `comprobanteUri` guarda la foto de la boleta/factura como respaldo.
 */
@Entity(
    tableName = "gasto",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["registradoPorId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("registradoPorId"),
        Index("fechaGasto")
    ]
)

data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoria: CategoriaGasto,
    val descripcion: String,
    val monto: Long,
    val fechaGasto: Long = System.currentTimeMillis(),
    val comprobanteUri: String? = null,
    val registradoPorId: Long
)
