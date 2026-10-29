package com.example.vecinoszaror.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vecinoszaror.ui.components.BarraInferior
import com.example.vecinoszaror.ui.components.BarraLateral
import com.example.vecinoszaror.ui.components.BarraSuperior
import com.example.vecinoszaror.ui.screens.AdministracionScreen
import com.example.vecinoszaror.ui.screens.AvisosScreen
import com.example.vecinoszaror.ui.screens.FinanzasScreen
import com.example.vecinoszaror.ui.screens.InformesScreen
import com.example.vecinoszaror.ui.screens.ReservasScreen
import com.example.vecinoszaror.ui.screens.login.LoginScreen
import com.example.vecinoszaror.ui.utils.TipoPantalla
import com.example.vecinoszaror.viewmodel.AuthViewModel
import com.example.vecinoszaror.viewmodel.NavigationViewModel

/**
 * Estructura principal de la app: login
 * barra superior y barra inferior con las secciones que permite el rol del usuario
 */
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    navigationViewModel: NavigationViewModel,
    tipoPantalla: TipoPantalla
) {
    val navController = rememberNavController()
    val sesion = authViewModel.sesion.collectAsStateWithLifecycle().value
    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route

    /** Ejecuta los eventos de navegación que piden los ViewModels */
    LaunchedEffect(navController) {
        navigationViewModel.eventos.collect { evento ->
            when (evento) {
                is NavigationEvent.IrA -> navController.navigate(evento.destino.route) {
                    evento.limpiarHasta?.let { pantalla ->
                        popUpTo(pantalla.route) { inclusive = evento.incluirEsaPantalla }
                    }
                    launchSingleTop = evento.evitarDuplicados
                }

                is NavigationEvent.IrALimpiandoHistorial -> navController.navigate(evento.destino.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }

                NavigationEvent.Volver -> navController.popBackStack()
                NavigationEvent.SubirNivel -> navController.navigateUp()
            }
        }
    }

    val opciones = if (sesion != null) opcionesDeMenu(sesion.permisos) else emptyList()
    val mostrarBarras = sesion != null && rutaActual != null && rutaActual != Screen.Login.route
    val usarBarraLateral = tipoPantalla != TipoPantalla.COMPACTA

    val seleccionarOpcion: (OpcionMenu) -> Unit = { opcion ->
        /** Al volver atrás, se regresa a la pantalla de inicio del rol */
        navigationViewModel.irA(
            destino = opcion.pantalla,
            limpiarHasta = opciones.first().pantalla,
            evitarDuplicados = true
        )
    }

    Scaffold(
        topBar = {
            if (mostrarBarras) {
                BarraSuperior(
                    nombre = sesion.usuario.nombre,
                    rol = sesion.nombreRol,
                    onCerrarSesion = {
                        authViewModel.cerrarSesion()
                        navigationViewModel.irALimpiandoHistorial(Screen.Login)
                    }
                )
            }
        },
        bottomBar = {
            if (mostrarBarras && !usarBarraLateral) {
                BarraInferior(
                    opciones = opciones,
                    rutaActual = rutaActual,
                    onSeleccionar = seleccionarOpcion
                )
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            if (mostrarBarras && usarBarraLateral) {
                BarraLateral(
                    opciones = opciones,
                    rutaActual = rutaActual,
                    onSeleccionar = seleccionarOpcion
                )
            }
            NavHost(
                navController = navController,
                startDestination = Screen.Login.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        tipoPantalla = tipoPantalla,
                        onLoginExitoso = {
                            /** La pantalla de inicio es la primera sección que permite el rol */
                            val inicio = authViewModel.sesion.value
                                ?.let { opcionesDeMenu(it.permisos).first().pantalla }
                                ?: Screen.Avisos
                            navigationViewModel.irALimpiandoHistorial(inicio)
                        }
                    )
                }
                composable(Screen.Reservas.route) { ReservasScreen() }
                composable(Screen.Finanzas.route) { FinanzasScreen() }
                composable(Screen.Informes.route) { InformesScreen() }
                composable(Screen.Avisos.route) { AvisosScreen() }
                composable(Screen.Administracion.route) { AdministracionScreen() }
            }
        }
    }
}
