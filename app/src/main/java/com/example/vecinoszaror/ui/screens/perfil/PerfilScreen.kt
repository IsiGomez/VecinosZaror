package com.example.vecinoszaror.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.EtiquetaActivo
import com.example.vecinoszaror.ui.components.FilaDato
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.formatearFecha

/** Perfil de quien inició sesión. */
@Composable
fun PerfilScreen(
    sesion: SesionUsuario,
    onCambiarClave: () -> Unit,
    onVolver: () -> Unit
) {
    val usuario = sesion.usuario
    val iniciales = "${usuario.nombre.firstOrNull() ?: ""}${usuario.apellido.firstOrNull() ?: ""}".uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mi perfil", style = MaterialTheme.typography.headlineSmall)

            Tarjeta(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${usuario.nombre} ${usuario.apellido}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = sesion.nombreRol,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        EtiquetaActivo(usuario.activo)
                    }
                }
            }

            Tarjeta(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Datos de la cuenta", style = MaterialTheme.typography.titleMedium)
                    FilaDato("Correo", usuario.email)
                    FilaDato("Teléfono", usuario.telefono?.takeIf { it.isNotBlank() } ?: "No registrado")
                    FilaDato("Miembro desde", formatearFecha(usuario.fechaCreacion))
                }
            }

            OutlinedButton(onClick = onCambiarClave, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar contraseña")
            }
            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
