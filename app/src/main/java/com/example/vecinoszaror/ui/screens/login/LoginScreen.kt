package com.example.vecinoszaror.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.ui.utils.TipoPantalla
import com.example.vecinoszaror.viewmodel.AuthViewModel

/**
 * Pantalla de inicio de sesión. Conecta el [AuthViewModel] con el diseño que corresponde
 * al tamaño de pantalla, la validación y la consulta a la base están en el ViewModel.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    tipoPantalla: TipoPantalla,
    onLoginExitoso: () -> Unit
) {
    val estado by authViewModel.estado.collectAsStateWithLifecycle()
    val sesion by authViewModel.sesion.collectAsStateWithLifecycle()

    /** Cuando el login resulta bien, la sesión deja de ser null y se avisa para navegar */
    LaunchedEffect(sesion) {
        if (sesion != null) onLoginExitoso()
    }

    when (tipoPantalla) {
        TipoPantalla.COMPACTA -> LoginCompacto(
            estado, authViewModel::onEmailChange, authViewModel::onClaveChange, authViewModel::iniciarSesion
        )
        TipoPantalla.MEDIANA -> LoginMediano(
            estado, authViewModel::onEmailChange, authViewModel::onClaveChange, authViewModel::iniciarSesion
        )
        TipoPantalla.EXPANDIDA -> LoginExpandido(
            estado, authViewModel::onEmailChange, authViewModel::onClaveChange, authViewModel::iniciarSesion
        )
    }

}
