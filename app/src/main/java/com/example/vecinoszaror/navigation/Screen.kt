package com.example.vecinoszaror.navigation

/** Rutas de la app */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Reservas : Screen("reservas")
    data object SolicitudReserva : Screen("reservas/solicitar")
    data object Finanzas : Screen("finanzas")
    data object Informes : Screen("informes")
    data object Avisos : Screen("avisos")
    data object Administracion : Screen("administracion")
}
