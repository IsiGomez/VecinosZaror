package com.example.vecinoszaror

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vecinoszaror.navigation.AppNavigation
import com.example.vecinoszaror.style.VecinosZarorTheme
import com.example.vecinoszaror.ui.utils.tipoPantalla
import com.example.vecinoszaror.viewmodel.AuthViewModel
import com.example.vecinoszaror.viewmodel.AvisosViewModel
import com.example.vecinoszaror.viewmodel.CambiarClaveViewModel
import com.example.vecinoszaror.viewmodel.DetalleReservaViewModel
import com.example.vecinoszaror.viewmodel.DisponibilidadViewModel
import com.example.vecinoszaror.viewmodel.FinanzasViewModel
import com.example.vecinoszaror.viewmodel.GenerarInformeViewModel
import com.example.vecinoszaror.viewmodel.EnviarAvisoViewModel
import com.example.vecinoszaror.viewmodel.GastoViewModel
import com.example.vecinoszaror.viewmodel.InformesViewModel
import com.example.vecinoszaror.viewmodel.NavigationViewModel
import com.example.vecinoszaror.viewmodel.PagoViewModel
import com.example.vecinoszaror.viewmodel.RecintoFormularioViewModel
import com.example.vecinoszaror.viewmodel.RecintosViewModel
import com.example.vecinoszaror.viewmodel.ReservasViewModel
import com.example.vecinoszaror.viewmodel.RolesViewModel
import com.example.vecinoszaror.viewmodel.SolicitudReservaViewModel
import com.example.vecinoszaror.viewmodel.UsuarioFormularioViewModel
import com.example.vecinoszaror.viewmodel.UsuariosViewModel
import com.example.vecinoszaror.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as VecinosZarorApp
        setContent {
            VecinosZarorTheme {
                val factory = remember { ViewModelFactory(app) }
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                val navigationViewModel: NavigationViewModel = viewModel(factory = factory)
                val reservasViewModel: ReservasViewModel = viewModel(factory = factory)
                val detalleReservaViewModel: DetalleReservaViewModel = viewModel(factory = factory)
                val solicitudReservaViewModel: SolicitudReservaViewModel = viewModel(factory = factory)
                val disponibilidadViewModel: DisponibilidadViewModel = viewModel(factory = factory)
                val finanzasViewModel: FinanzasViewModel = viewModel(factory = factory)
                val pagoViewModel: PagoViewModel = viewModel(factory = factory)
                val gastoViewModel: GastoViewModel = viewModel(factory = factory)
                val avisosViewModel: AvisosViewModel = viewModel(factory = factory)
                val enviarAvisoViewModel: EnviarAvisoViewModel = viewModel(factory = factory)
                val informesViewModel: InformesViewModel = viewModel(factory = factory)
                val generarInformeViewModel: GenerarInformeViewModel = viewModel(factory = factory)
                val usuariosViewModel: UsuariosViewModel = viewModel(factory = factory)
                val usuarioFormularioViewModel: UsuarioFormularioViewModel = viewModel(factory = factory)
                val recintosViewModel: RecintosViewModel = viewModel(factory = factory)
                val recintoFormularioViewModel: RecintoFormularioViewModel = viewModel(factory = factory)
                val rolesViewModel: RolesViewModel = viewModel(factory = factory)
                val cambiarClaveViewModel: CambiarClaveViewModel = viewModel(factory = factory)
                val tipoPantalla = calculateWindowSizeClass(this).tipoPantalla()
                AppNavigation(
                    authViewModel, navigationViewModel, reservasViewModel,
                    detalleReservaViewModel,
                    solicitudReservaViewModel, disponibilidadViewModel, finanzasViewModel,
                    pagoViewModel, gastoViewModel, avisosViewModel, enviarAvisoViewModel, informesViewModel,
                    generarInformeViewModel, usuariosViewModel,
                    usuarioFormularioViewModel, recintosViewModel, recintoFormularioViewModel,
                    rolesViewModel, cambiarClaveViewModel, tipoPantalla
                )
            }
        }
    }
}
