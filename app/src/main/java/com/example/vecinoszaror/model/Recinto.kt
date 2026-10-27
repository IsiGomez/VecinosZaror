package com.example.vecinoszaror.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recinto",
    indices = [Index(value = ["nombre"], unique = true)]
)

data class Recinto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val capacidadPersonas: Int,
    val tarifaBase: Long,
    val activo: Boolean = true
)