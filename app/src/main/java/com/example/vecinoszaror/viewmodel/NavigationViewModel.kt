package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vecinoszaror.navigation.NavigationEvent
import com.example.vecinoszaror.navigation.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Recibe las órdenes de navegación y las entrega a UI como un flujo de eventos.
 * Usa un Channel para que no se pierda ninguna orden enviada antes de que la UI la escuche.
 */
class NavigationViewModel : ViewModel() {

    private val _eventos = Channel<NavigationEvent>(Channel.BUFFERED)
    val eventos: Flow<NavigationEvent> = _eventos.receiveAsFlow()

    fun irA(
        destino: Screen,
        limpiarHasta: Screen? = null,
        incluirEsaPantalla: Boolean = false,
        evitarDuplicados: Boolean = false
    ) {
        _eventos.trySend(NavigationEvent.IrA(destino, limpiarHasta, incluirEsaPantalla, evitarDuplicados))
    }

    fun irALimpiandoHistorial(destino: Screen) {
        _eventos.trySend(NavigationEvent.IrALimpiandoHistorial(destino))
    }

    fun volver() {
        _eventos.trySend(NavigationEvent.Volver)
    }

    fun subirNivel() {
        _eventos.trySend(NavigationEvent.SubirNivel)
    }
}
