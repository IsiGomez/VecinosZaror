package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** VECINO, SECRETARIA, TESORERIA, DIRECTIVA. */
@Entity(
    tableName = "rol",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Rol(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String
)
