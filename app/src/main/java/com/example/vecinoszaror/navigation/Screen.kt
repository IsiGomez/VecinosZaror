package com.example.vecinoszaror.navigation

/** Rutas de la app */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Reservas : Screen("reservas")
    data object SolicitudReserva : Screen("reservas/solicitar")
    data object Disponibilidad : Screen("reservas/disponibilidad")
    data object Finanzas : Screen("finanzas")
    data object RegistrarPago : Screen("finanzas/pago")
    data object Informes : Screen("informes")
    data object Avisos : Screen("avisos")
    data object Administracion : Screen("administracion")
}
