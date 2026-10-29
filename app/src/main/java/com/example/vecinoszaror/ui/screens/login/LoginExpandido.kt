package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.LoginUiState
import com.example.vecinoszaror.style.VecinosZarorTheme

/** panel con el logo a la izquierda y formulario a la derecha */
@Composable
fun LoginExpandido(
    estado: LoginUiState,
    onEmailChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onIngresar: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            EncabezadoLogin(
                tamanologo = 120.dp,
                colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        /** Formulario */
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            LoginFormulario(
                estado = estado,
                onEmailChange = onEmailChange,
                onCLaveChange = onClaveChange,
                onIngresar = onIngresar,
                modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp)
            )
        }
    }
}

@Preview(name = "Expandida", showBackground = true, widthDp = 1100, heightDp = 700)
@Composable
private fun LoginExpandidoPreview() {
    VecinosZarorTheme { LoginExpandido(LoginUiState(), {}, {}, {}) }
}