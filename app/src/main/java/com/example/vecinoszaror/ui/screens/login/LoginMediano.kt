package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.LoginUiState
import com.example.vecinoszaror.style.VecinosZarorTheme

/** Dentro de una tarjeta centrada de ancho limitado */
@Composable
fun LoginMediano(
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
        Card(modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp)) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EncabezadoLogin(tamanologo = 88.dp)
                Spacer(modifier = Modifier.height(32.dp))
                LoginFormulario(estado, onEmailChange, onClaveChange, onIngresar)
            }
        }
    }
}

@Preview(name = "Mediana", showBackground = true, widthDp = 700, heightDp = 900)
@Composable
private fun LoginMedianoPreview() {
    VecinosZarorTheme { LoginMediano(LoginUiState(), {}, {}, {})}
}