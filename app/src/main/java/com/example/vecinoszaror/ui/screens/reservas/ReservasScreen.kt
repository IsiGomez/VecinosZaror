package com.example.vecinoszaror.ui.screens.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.viewmodel.ReservasViewModel

/**
 * Lista de reservas.
 * Qué reservas cargar lo decide el [ReservasViewModel] según la sesión.
 */
@Composable
fun ReservasScreen(
    viewModel: ReservasViewModel,
    sesion: SesionUsuario
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    /** Avisa al ViewModel quién es el usuario */
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = if (estado.puedeVerTodas) "Todas las reservas"
                   else "Mis reservas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )

        when {
            estado.cargando -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            estado.reservas.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (estado.puedeVerTodas) "Aún no hay reservas registradas."
                           else "Aún no has solicitado reservas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                /** key: identifica cada tarjeta para que Compose no las redibuje */
                items(estado.reservas, key = { it.reserva.id }) { detalle ->
                    TarjetaReserva(detalle, mostrarSolicitante = estado.puedeVerTodas)
                }
            }
        }
    }
}