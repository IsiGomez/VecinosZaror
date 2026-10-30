package com.example.vecinoszaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Pantallas de relleno para probar la navegación por rol. */

@Composable
fun FinanzasScreen() = PantallaEnConstruccion("Finanzas", "Pagos, gastos y comprobantes.")

@Composable
fun InformesScreen() = PantallaEnConstruccion("Informes", "Rendiciones de cuentas por período.")

@Composable
fun AvisosScreen() = PantallaEnConstruccion("Avisos", "Notificaciones de reservas, pagos e informes.")

@Composable
fun AdministracionScreen() = PantallaEnConstruccion("Administración", "Usuarios, roles, permisos y recintos.")

@Composable
private fun PantallaEnConstruccion(titulo: String, descripcion: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "En construcción",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
