package com.example.vecinoszaror.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/** Rol junto con sus permisos */
data class RolConPermisos(
    @Embedded val rol: Rol,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RolPermiso::class,
            parentColumn = "rolId",
            entityColumn = "permisoId"
        )
    )
    val permisos: List<Permiso>
)

/** Después del login para decidir qué pantallas mostrar. */
data class UsuarioConRol(
    @Embedded val usuario: Usuario,
    @Relation(parentColumn = "rolId", entityColumn = "id")
    val rol: Rol
)

/** Reserva con los datos del vecino solicitante y del recinto. */
data class ReservaConDetalle(
    @Embedded val reserva: Reserva,
    @Relation(parentColumn = "usuarioId", entityColumn = "id")
    val solicitante: Usuario,
    @Relation(parentColumn = "recintoId", entityColumn = "id")
    val recinto: Recinto
)