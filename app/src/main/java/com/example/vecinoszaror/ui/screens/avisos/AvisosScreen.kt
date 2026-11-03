package com.example.vecinoszaror.ui.screens.avisos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.Notificacion
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.utils.formatearFechaHora
import com.example.vecinoszaror.viewmodel.AvisosViewModel

/** Avisos del usuario: cambios de estado de reserva, pagos e informes. */
@Composable
fun AvisosScreen(
    viewModel: AvisosViewModel,
    sesion: SesionUsuario
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Avisa al ViewModel quién es el usuario, se repite solo si cambia la sesión
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    if (estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 700.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Avisos", style = MaterialTheme.typography.headlineSmall)
                    if (estado.noLeidos > 0) {
                        TextButton(onClick = viewModel::marcarTodosLeidos) {
                            Text("Marcar todos como leídos")
                        }
                    }
                }
            }

            if (estado.avisos.isEmpty()) {
                item {
                    Text(
                        text = "No tienes avisos por ahora.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(estado.avisos, key = { it.id }) { aviso ->
                    FilaAviso(aviso = aviso, onClick = { viewModel.marcarLeido(aviso.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

/** Un aviso: los no leídos en negrita y con un puntito. */
@Composable
private fun FilaAviso(aviso: Notificacion, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = aviso.titulo,
                fontWeight = if (aviso.leida) FontWeight.Normal else FontWeight.Bold
            )
        },
        supportingContent = {
            Text("${aviso.mensaje}\n${formatearFechaHora(aviso.fechaCreacion)}")
        },
        trailingContent = if (!aviso.leida) {
            { Badge() }
        } else null
    )
}
