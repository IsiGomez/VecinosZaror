package com.example.vecinoszaror.model

import android.net.Uri

data class GastoErrores(
    val descripcion: String? = null,
    val monto: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = descripcion != null || monto != null
}

/** Estado del formulario para registrar un gasto de la junta. */
data class GastoUiState(
    val categoria: CategoriaGasto = CategoriaGasto.LUZ,
    val descripcion: String = "",
    /** Se convierte a número al validar. */
    val monto: String = "",
    /** Dirección de la foto del comprobante */
    val comprobante: Uri? = null,
    val enviando: Boolean = false,
    val errores: GastoErrores = GastoErrores()
)
