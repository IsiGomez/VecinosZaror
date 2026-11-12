package com.example.vecinoszaror.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.vecinoszaror.ui.screens.gestion.GestionScreen
import com.example.vecinoszaror.ui.screens.avisos.AvisosScreen
import com.example.vecinoszaror.ui.screens.avisos.EnviarAvisoScreen
import com.example.vecinoszaror.ui.screens.finanzas.FinanzasScreen
import com.example.vecinoszaror.ui.screens.finanzas.RegistrarGastoScreen
import com.example.vecinoszaror.ui.screens.finanzas.RegistrarPagoScreen
import com.example.vecinoszaror.ui.screens.informes.GenerarInformeScreen
import com.example.vecinoszaror.ui.screens.informes.InformesScreen
import com.example.vecinoszaror.ui.screens.login.LoginScreen
import com.example.vecinoszaror.ui.screens.perfil.CambiarClaveScreen
import com.example.vecinoszaror.ui.screens.perfil.EditarPerfilScreen
import com.example.vecinoszaror.ui.screens.perfil.PerfilScreen
import com.example.vecinoszaror.ui.screens.reservas.DetalleReservaScreen
import com.example.vecinoszaror.ui.screens.reservas.DisponibilidadScreen
import com.example.vecinoszaror.ui.screens.reservas.ReservasScreen
import com.example.vecinoszaror.ui.screens.reservas.SolicitudReservaScreen
import com.example.vecinoszaror.ui.utils.TipoPantalla
import com.example.vecinoszaror.model.Permisos
import com.example.vecinoszaror.viewmodel.AuthViewModel
import com.example.vecinoszaror.viewmodel.AvisosViewModel
import com.example.vecinoszaror.viewmodel.CambiarClaveViewModel
import com.example.vecinoszaror.viewmodel.DetalleReservaViewModel
import com.example.vecinoszaror.viewmodel.DisponibilidadViewModel
import com.example.vecinoszaror.viewmodel.EditarPerfilViewModel
import com.example.vecinoszaror.viewmodel.EnviarAvisoViewModel
import com.example.vecinoszaror.viewmodel.FinanzasViewModel
import com.example.vecinoszaror.viewmodel.GastoViewModel
import com.example.vecinoszaror.viewmodel.GenerarInformeViewModel
import com.example.vecinoszaror.viewmodel.InformesViewModel
import com.example.vecinoszaror.viewmodel.OlvidoClaveViewModel
import com.example.vecinoszaror.viewmodel.NavigationViewModel
import com.example.vecinoszaror.viewmodel.PagoViewModel
import com.example.vecinoszaror.viewmodel.RecintoFormularioViewModel
import com.example.vecinoszaror.viewmodel.RecintosViewModel
import com.example.vecinoszaror.viewmodel.ReservasViewModel
import com.example.vecinoszaror.viewmodel.RolesViewModel
import com.example.vecinoszaror.viewmodel.SolicitudReservaViewModel
import com.example.vecinoszaror.viewmodel.UsuarioFormularioViewModel
import com.example.vecinoszaror.viewmodel.UsuariosViewModel

/**
 * Estructura principal de la app: login
 * barra superior y barra inferior con las secciones que permite el rol del usuario
 */
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    navigationViewModel: NavigationViewModel,
    reservasViewModel: ReservasViewModel,
    detalleReservaViewModel: DetalleReservaViewModel,
    solicitudReservaViewModel: SolicitudReservaViewModel,
    disponibilidadViewModel: DisponibilidadViewModel,
    finanzasViewModel: FinanzasViewModel,
    pagoViewModel: PagoViewModel,
    gastoViewModel: GastoViewModel,
    avisosViewModel: AvisosViewModel,
    enviarAvisoViewModel: EnviarAvisoViewModel,
    informesViewModel: InformesViewModel,
    generarInformeViewModel: GenerarInformeViewModel,
    usuariosViewModel: UsuariosViewModel,
    usuarioFormularioViewModel: UsuarioFormularioViewModel,
    recintosViewModel: RecintosViewModel,
    recintoFormularioViewModel: RecintoFormularioViewModel,
    rolesViewModel: RolesViewModel,
    cambiarClaveViewModel: CambiarClaveViewModel,
    olvidoClaveViewModel: OlvidoClaveViewModel,
    editarPerfilViewModel: EditarPerfilViewModel,
    tipoPantalla: TipoPantalla
) {
    val navController = rememberNavController()
    val sesion = authViewModel.sesion.collectAsStateWithLifecycle().value
    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route

    /** Ejecuta los eventos de navegación que piden los ViewModels */
    LaunchedEffect(navController) {
        navigationViewModel.eventos.collect { evento ->
            when (evento) {
                is NavigationEvent.IrA -> {
                    if (evento.evitarDuplicados && navController.currentDestination?.route == evento.destino.route) {
                        return@collect
                    }
                    navController.navigate(evento.destino.route) {
                        evento.limpiarHasta?.let { pantalla ->
                            popUpTo(pantalla.route) { inclusive = evento.incluirEsaPantalla }
                        }
                        launchSingleTop = evento.evitarDuplicados
                    }
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
                    onVerPerfil = { navigationViewModel.irA(Screen.Perfil, evitarDuplicados = true) },
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
                modifier = Modifier.weight(1f),
                enterTransition = { fadeIn(tween(durationMillis = 200, delayMillis = 100)) },
                exitTransition = { fadeOut(tween(durationMillis = 100)) },
                popEnterTransition = { fadeIn(tween(durationMillis = 200, delayMillis = 100)) },
                popExitTransition = { fadeOut(tween(durationMillis = 100)) }
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        olvidoClaveViewModel = olvidoClaveViewModel,
                        tipoPantalla = tipoPantalla,
                        onLoginExitoso = {
                            avisosViewModel.reiniciar()
                            reservasViewModel.reiniciar()
                            informesViewModel.reiniciar()
                            finanzasViewModel.reiniciar()

                            /** La pantalla de inicio es la primera sección que permite el rol */
                            val inicio = authViewModel.sesion.value
                                ?.let { opcionesDeMenu(it.permisos).first().pantalla }
                                ?: Screen.Avisos
                            navigationViewModel.irALimpiandoHistorial(inicio)
                        }
                    )
                }
                composable(Screen.Reservas.route) {
                    if (sesion != null) {
                        ReservasScreen(
                            viewModel = reservasViewModel,
                            sesion = sesion,
                            onSolicitarReserva = {
                                solicitudReservaViewModel.reiniciar()
                                navigationViewModel.irA(Screen.SolicitudReserva)
                            },
                            onVerDisponibilidad = { navigationViewModel.irA(Screen.Disponibilidad) },
                            onVerDetalle = { reservaId ->
                                /** Primero se le dice al ViewModel qué reserva, luego se navega */
                                detalleReservaViewModel.cargar(reservaId, sesion)
                                navigationViewModel.irA(Screen.DetalleReserva, evitarDuplicados = true)
                            }
                        )
                    }
                }
                composable(Screen.DetalleReserva.route) {
                    if (sesion != null) {
                        DetalleReservaScreen(
                            viewModel = detalleReservaViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Disponibilidad.route) {
                    if (sesion != null && sesion.tienePermiso(Permisos.RESERVA_VERIFICAR_DISPONIBILIDAD)) {
                        DisponibilidadScreen(
                            viewModel = disponibilidadViewModel,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.SolicitudReserva.route) {
                    if (sesion != null) {
                        SolicitudReservaScreen(
                            viewModel = solicitudReservaViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Finanzas.route) {
                    if (sesion != null) {
                        FinanzasScreen(
                            viewModel = finanzasViewModel,
                            sesion = sesion,
                            onRegistrarPago = {
                                pagoViewModel.reiniciar()
                                navigationViewModel.irA(Screen.RegistrarPago)
                            },
                            onRegistrarGasto = {
                                gastoViewModel.reiniciar()
                                navigationViewModel.irA(Screen.RegistrarGasto)
                            }
                        )
                    }
                }
                composable(Screen.RegistrarPago.route) {
                    if (sesion != null && sesion.tienePermiso(Permisos.PAGO_REGISTRAR)) {
                        RegistrarPagoScreen(
                            viewModel = pagoViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.RegistrarGasto.route) {
                    if (sesion != null && sesion.tienePermiso(Permisos.GASTO_REGISTRAR)) {
                        RegistrarGastoScreen(
                            viewModel = gastoViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Informes.route) {
                    if (sesion != null) {
                        InformesScreen(
                            viewModel = informesViewModel,
                            sesion = sesion,
                            onGenerarInforme = {
                                generarInformeViewModel.reiniciar()
                                navigationViewModel.irA(Screen.GenerarInforme)
                            }
                        )
                    }
                }
                composable(Screen.GenerarInforme.route) {
                    if (sesion != null && sesion.tienePermiso(Permisos.INFORME_GENERAR)) {
                        GenerarInformeScreen(
                            viewModel = generarInformeViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Avisos.route) {
                    if (sesion != null) {
                        AvisosScreen(
                            viewModel = avisosViewModel,
                            sesion = sesion,
                            onEnviarAviso = {
                                enviarAvisoViewModel.reiniciar()
                                navigationViewModel.irA(Screen.EnviarAviso, evitarDuplicados = true)
                            }
                        )
                    }
                }
                composable(Screen.EnviarAviso.route) {
                    if (sesion != null && sesion.tienePermiso(Permisos.AVISO_ENVIAR)) {
                        EnviarAvisoScreen(
                            viewModel = enviarAvisoViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Perfil.route) {
                    if (sesion != null) {
                        PerfilScreen(
                            sesion = sesion,
                            onEditar = {
                                editarPerfilViewModel.cargar(sesion)
                                navigationViewModel.irA(Screen.EditarPerfil, evitarDuplicados = true)
                            },
                            onCambiarClave = {
                                cambiarClaveViewModel.reiniciar()
                                navigationViewModel.irA(Screen.CambiarClave, evitarDuplicados = true)
                            },
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.EditarPerfil.route) {
                    if (sesion != null) {
                        EditarPerfilScreen(
                            viewModel = editarPerfilViewModel,
                            sesion = sesion,
                            onActualizado = authViewModel::actualizarUsuario,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.CambiarClave.route) {
                    if (sesion != null) {
                        CambiarClaveScreen(
                            viewModel = cambiarClaveViewModel,
                            sesion = sesion,
                            onVolver = { navigationViewModel.volver() }
                        )
                    }
                }
                composable(Screen.Gestion.route) {
                    if (sesion != null) {
                        GestionScreen(
                            usuariosViewModel = usuariosViewModel,
                            usuarioFormularioViewModel = usuarioFormularioViewModel,
                            recintosViewModel = recintosViewModel,
                            recintoFormularioViewModel = recintoFormularioViewModel,
                            rolesViewModel = rolesViewModel,
                            sesion = sesion
                        )
                    }
                }
            }
        }
    }
}