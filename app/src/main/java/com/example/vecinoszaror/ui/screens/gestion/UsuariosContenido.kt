package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.example.vecinoszaror.model.Usuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.EstadoVacio
import com.example.vecinoszaror.ui.components.FilaFiltros
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.UsuariosViewModel

/** Lista de usuarios con filtro por rol. */
@Composable
fun UsuariosContenido(viewModel: UsuariosViewModel) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    ContenidoCargando(cargando = estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 700.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                        Column(modifier = Modifier.animateItem()) {
                            FilaUsuario(usuario = usuario, nombreRol = estado.rolDe(usuario)?.etiqueta() ?: "Sin rol")
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

/** Un usuario: nombre, correo y rol. Los desactivados llevan una marca. */
@Composable
private fun FilaUsuario(usuario: Usuario, nombreRol: String) {
    ListItem(
        headlineContent = { Text("${usuario.nombre} ${usuario.apellido}") },
        supportingContent = { Text("${usuario.email} · $nombreRol") },
        trailingContent = if (!usuario.activo) {
            { Text("Inactivo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error) }
        } else null
    )
}
