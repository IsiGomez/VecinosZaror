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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.EstadoVacio
import com.example.vecinoszaror.ui.components.FilaFiltros
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.UsuarioFormularioViewModel
import com.example.vecinoszaror.viewmodel.UsuariosViewModel

/** Lista de usuarios con filtro por rol. */
@Composable
fun UsuariosContenido(
    viewModel: UsuariosViewModel,
    formularioViewModel: UsuarioFormularioViewModel,
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
                    Button(onClick = formularioViewModel::nuevo) { Text("Nuevo usuario") }
                }
                item {
                    FilaFiltros(
                        opciones = estado.roles,
                        seleccionada = estado.rolFiltrado,
                        textoDe = { it.etiqueta() },
                        onSeleccionar = { viewModel.onFiltroRolChange(it?.id) }
                    )
                }

                if (estado.usuariosVisibles.isEmpty()) {
                    item {
                        EstadoVacio(
                            icono = Icons.Filled.Person,
                            titulo = "No hay usuarios",
                            mensaje = "No hay usuarios con este rol."
                        )
                    }
                } else {
                    items(estado.usuariosVisibles, key = { it.id }) { usuario ->
                        TarjetaUsuario(
                            usuario = usuario,
                            nombreRol = estado.rolDe(usuario)?.etiqueta() ?: "Sin rol",
                            onClick = { formularioViewModel.editar(usuario, sesion) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    // La ventana de crear/editar, no dibuja nada mientras está cerrada
    UsuarioFormularioDialog(viewModel = formularioViewModel, sesion = sesion)
}

/** Un usuario: círculo con sus iniciales, nombre, correo y rol, y una etiqueta Activo / Inactivo. */
@Composable
private fun TarjetaUsuario(
    usuario: Usuario,
    nombreRol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iniciales = "${usuario.nombre.firstOrNull() ?: ""}${usuario.apellido.firstOrNull() ?: ""}".uppercase()

    Tarjeta(modifier = modifier.fillMaxWidth()) {
        Row(
            // Al tocar la tarjeta se abre la ventana para editar al usuario
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("${usuario.nombre} ${usuario.apellido}", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$nombreRol · ${usuario.email}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            EtiquetaActivo(usuario.activo)
        }
    }
}

/** Etiqueta redonda. */
@Composable
private fun EtiquetaActivo(activo: Boolean) {
    val fondo = if (activo) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.errorContainer
    val texto = if (activo) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
    Surface(shape = CircleShape, color = fondo) {
        Text(
            text = if (activo) "Activo" else "Inactivo",
            color = texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
