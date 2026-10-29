package com.example.vecinoszaror.model

/** Usuario que inició sesión, con su rol y los códigos de permiso de ese rol. */
data class SesionUsuario(
    val usuario: Usuario,
    val rol: Rol,
    val permisos: Set<String>
) {
    /** Nombre del rol para mostrar en pantalla. */
    val nombreRol: String
        get() = when (rol.nombre) {
            "VECINO" -> "Vecino/a"
            "SECRETARIA" -> "Secretaría"
            "TESORERIA" -> "Tesorería"
            "DIRECTIVA" -> "Directiva"
            else -> rol.nombre
        }

    fun tienePermiso(codigo: String): Boolean = codigo in permisos
}
