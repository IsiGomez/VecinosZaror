package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.LoginUiState
import com.example.vecinoszaror.style.VecinosZarorTheme

/** Una sola columna con logo arriba y formulario abajo */
@Composable
fun LoginCompacto(
    estado: LoginUiState,
    onEmailChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onIngresar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EncabezadoLogin()
        Spacer(modifier = Modifier.height(32.dp))
        LoginFormulario(estado, onEmailChange, onClaveChange, onIngresar)
    }
}

@Preview(name = "Compacta", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun LoginCompactoPreview() {
    VecinosZarorTheme { LoginCompacto(LoginUiState(), {}, {}, {}) }
}