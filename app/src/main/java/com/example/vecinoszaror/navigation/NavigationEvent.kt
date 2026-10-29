package com.example.vecinoszaror.navigation

/**
 * Órdenes de navegación que piden los ViewModels y ejecuta [AppNavigation].
 * El "historial" es la lista de pantallas por las que se vuelve con el botón atrás.
 */
sealed class NavigationEvent {

    /**
     * Va a [destino].
     * - [limpiarHasta]: si no es null, borra del historial las pantallas hasta esa.
     * - [incluirEsaPantalla]: si es true, también borra la pantalla de [limpiarHasta].
     * - [evitarDuplicados]: si es true, no abre [destino] otra vez si ya está arriba.
     */
    data class IrA(
        val destino: Screen,
        val limpiarHasta: Screen? = null,
        val incluirEsaPantalla: Boolean = false,
        val evitarDuplicados: Boolean = false
    ) : NavigationEvent()

    /** Va a [destino] y borra todo el historial (al iniciar o cerrar sesion). */
    data class IrALimpiandoHistorial(val destino: Screen) : NavigationEvent()

    /** Vuelve a la pantalla anterior. */
    data object Volver : NavigationEvent()

    /** Sube un nivel en la jerarquía de pantallas de la app. */
    data object SubirNivel : NavigationEvent()
}
