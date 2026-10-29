package com.example.vecinoszaror.model

/** Errores de validación del formulario de login (null = sin error). */
data class LoginErrores(
    val email: String? = null,
    val clave: String? = null,
    /** Error que no es de un campo, por ejemplo "correo o clave incorrectos". */
    val general: String? = null
)

/** Estado del formulario de login. */
data class LoginUiState(
    val email: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val errores: LoginErrores = LoginErrores()
)
