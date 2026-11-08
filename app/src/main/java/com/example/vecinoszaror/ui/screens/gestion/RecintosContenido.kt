package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.Recinto
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.EstadoVacio
import com.example.vecinoszaror.ui.components.EtiquetaActivo
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.formatearMonto
import com.example.vecinoszaror.viewmodel.RecintoFormularioViewModel
import com.example.vecinoszaror.viewmodel.RecintosViewModel

/** Lista de recintos que se pueden arrendar. */
@Composable
fun RecintosContenido(
    viewModel: RecintosViewModel,
    formularioViewModel: RecintoFormularioViewModel,
    sesion: SesionUsuario
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    ContenidoCargando(cargando = estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 700.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Button(onClick = formularioViewModel::nuevo) { Text("Nuevo recinto") }
                }

                if (estado.recintos.isEmpty()) {
                    item {
                        EstadoVacio(
                            icono = Icons.Filled.Home,
                            titulo = "No hay recintos",
                            mensaje = "Crea el primer recinto para que los vecinos puedan reservarlo."
                        )
                    }
                } else {
                    items(estado.recintos, key = { it.id }) { recinto ->
                        TarjetaRecinto(
                            recinto = recinto,
                            onClick = { formularioViewModel.editar(recinto) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    // La ventana de crear/editar, no dibuja nada mientras está cerrada
    RecintoFormularioDialog(viewModel = formularioViewModel, sesion = sesion)
}

/** Un recinto: ícono, nombre y estado, descripción, capacidad y tarifa. */
@Composable
private fun TarjetaRecinto(recinto: Recinto, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Tarjeta(modifier = modifier.fillMaxWidth()) {
        Row(
            // Al tocar la tarjeta se abre la ventana para editar el recinto
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recinto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    EtiquetaActivo(recinto.activo)
                }

                Text(
                    text = recinto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Capacidad ${recinto.capacidadPersonas} personas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatearMonto(recinto.tarifaBase),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
