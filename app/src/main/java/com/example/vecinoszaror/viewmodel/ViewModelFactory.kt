package com.example.vecinoszaror.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vecinoszaror.VecinosZarorApp

/**
 * Crea los ViewModels que necesitan los repositorios, tomandolos desde [VecinosZarorApp].
 * Cada ViewModel nuevo se agrega como un caso más del `when`.
 */
class ViewModelFactory(private val app: VecinosZarorApp) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) ->
            AuthViewModel(app.usuarioRepository, app.rolPermisoRepository) as T

        modelClass.isAssignableFrom(NavigationViewModel::class.java) ->
            NavigationViewModel() as T

        modelClass.isAssignableFrom(ReservasViewModel::class.java) ->
            ReservasViewModel(app.reservaRepository) as T

        modelClass.isAssignableFrom(SolicitudReservaViewModel::class.java) ->
            SolicitudReservaViewModel(app.reservaRepository, app.recintoRepository) as T

        modelClass.isAssignableFrom(DisponibilidadViewModel::class.java) ->
            DisponibilidadViewModel(app.reservaRepository, app.recintoRepository) as T

        modelClass.isAssignableFrom(FinanzasViewModel::class.java) ->
            FinanzasViewModel(app.finanzasRepository) as T

        modelClass.isAssignableFrom(PagoViewModel::class.java) ->
            PagoViewModel(app.reservaRepository, app.finanzasRepository) as T

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}