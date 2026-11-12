package com.example.vecinoszaror.model.uistate

/** Estado del formulario "¿Olvidaste tu contraseña?" del login. */
data class OlvidoClaveUiState(
    val email: String = "",
    val enviando: Boolean = false,
    val enviado: Boolean = false,
    val errorEmail: String? = null
)
