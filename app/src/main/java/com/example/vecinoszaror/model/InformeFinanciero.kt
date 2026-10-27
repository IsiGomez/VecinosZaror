package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Informe de cuentas, preparado por Tesorería.
 * Los vecinos solo ven los informes con `publicado = true`.
 */
@Entity(
    tableName = "informe_financiero",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["generadoPorId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("generadoPorId")]
)

data class InformeFinanciero(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val periodoInicio: Long,
    val periodoFin: Long,
    val totalIngresos: Long,
    val totalGastos: Long,
    val saldo: Long,
    val observaciones: String? = null,
    val generadoPorId: Long,
    val fechaGeneracion: Long = System.currentTimeMillis(),
    val publicado: Boolean = false
)