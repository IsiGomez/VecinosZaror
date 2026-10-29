package com.example.vecinoszaror.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.vecinoszaror.model.Permisos


/** Opción de la barra inferior */
data class OpcionMenu(
    val pantalla: Screen,
    val etiqueta: String,
    val icono: ImageVector
)

/**
 * Arma el menú según los permisos del rol
 * La primera opción de la lista es su pantalla de inicio.
 */
fun opcionesDeMenu(permisos: Set<String>): List<OpcionMenu> = buildList {
    if (Permisos.RESERVA_VER_PROPIAS in permisos || Permisos.RESERVA_VER_TODAS in permisos) {
        add(OpcionMenu(Screen.Reservas, "Reservas", Icons.Filled.DateRange))
    }

    if (Permisos.PAGO_REGISTRAR in permisos ||
        Permisos.GASTO_REGISTRAR in permisos ||
        Permisos.FINANZAS_VER_DETALLE in permisos
    ) {
        add(OpcionMenu(Screen.Finanzas, "Finanzas", Icons.Filled.AccountBox))
    }

    if (Permisos.INFORME_VER_PUBLICADOS in permisos ||
        Permisos.INFORME_GENERAR in permisos ||
        Permisos.INFORME_PUBLICAR in permisos
    ) {
        add(OpcionMenu(Screen.Informes, "Informes", Icons.Filled.Info))
    }

    /** Los avisos los reciben todos los perfiles */
    add(OpcionMenu(Screen.Avisos, "Avisos", Icons.Filled.Notifications))

    if (Permisos.USUARIO_GESTIONAR in permisos) {
        add(OpcionMenu(Screen.Administracion, "Gestión", Icons.Filled.Settings))
    }
}
