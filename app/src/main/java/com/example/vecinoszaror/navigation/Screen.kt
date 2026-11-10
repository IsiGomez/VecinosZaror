package com.example.vecinoszaror.navigation

/** Rutas de la app */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Reservas : Screen("reservas")
    data object SolicitudReserva : Screen("reservas/solicitar")
    data object DetalleReserva : Screen("reservas/detalle")
    data object Disponibilidad : Screen("reservas/disponibilidad")
    data object Finanzas : Screen("finanzas")
    data object RegistrarPago : Screen("finanzas/pago")
    data object RegistrarGasto : Screen("finanzas/gasto")
    data object Informes : Screen("informes")
    data object GenerarInforme : Screen("informes/generar")
    data object Avisos : Screen("avisos")
    data object EnviarAviso : Screen("avisos/enviar")
    data object Perfil : Screen("perfil")
    data object Gestion : Screen("gestion")
}
