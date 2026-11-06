package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
                        Column(modifier = Modifier.animateItem()) {
                            FilaRecinto(recinto = recinto, onClick = { formularioViewModel.editar(recinto) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    // La ventana de crear/editar, no dibuja nada mientras está cerrada
    RecintoFormularioDialog(viewModel = formularioViewModel, sesion = sesion)
}

/** Un recinto: nombre, descripción, capacidad y tarifa; los inactivos llevan una marca. */
@Composable
private fun FilaRecinto(recinto: Recinto, onClick: () -> Unit) {
    ListItem(
        // Al tocar la fila se abre la ventana para editar al usuario
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(recinto.nombre) },
        supportingContent = {
            Text("${recinto.descripcion}\nCapacidad ${recinto.capacidadPersonas} personas · ${formatearMonto(recinto.tarifaBase)}")
        },
        trailingContent = if (!recinto.activo) {
            { Text("Inactivo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error) }
        } else null
    )
}
