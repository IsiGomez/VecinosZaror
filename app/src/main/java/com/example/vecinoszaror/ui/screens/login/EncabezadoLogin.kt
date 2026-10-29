package com.example.vecinoszaror.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.R

@Composable
fun EncabezadoLogin(
    tamanologo: Dp = 72.dp,
    colorTexto: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo Junta de vecinos Población Zaror",
            modifier = Modifier.size(tamanologo)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Junta de Vecinos Población Zaror",
            style = MaterialTheme.typography.headlineSmall,
            color = colorTexto,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gestión de arriendos y transparencia financiera",
            style = MaterialTheme.typography.bodyMedium,
            color = colorTexto,
            textAlign = TextAlign.Center
        )
    }

}