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
import com.example.vecinoszaror.viewmodel.NavigationViewModel
import com.example.vecinoszaror.viewmodel.ReservasViewModel
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
                val tipoPantalla = calculateWindowSizeClass(this).tipoPantalla()
                AppNavigation(authViewModel, navigationViewModel, reservasViewModel, tipoPantalla)
            }
        }
    }
}
