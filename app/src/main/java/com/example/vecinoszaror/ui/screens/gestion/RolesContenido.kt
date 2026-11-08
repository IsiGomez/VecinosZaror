package com.example.vecinoszaror.ui.screens.gestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.FilaFiltros
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.ui.utils.etiquetaModulo
import com.example.vecinoszaror.viewmodel.RolesViewModel

/** Roles: elige un rol y activa o desactiva cada uno de sus permisos. */
@Composable
fun RolesContenido(
    viewModel: RolesViewModel,
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
                    // Siempre hay un rol elegido, así que no se muestra la opción "Todos"
                    FilaFiltros(
                        opciones = estado.roles.map { it.rol },
                        seleccionada = estado.rolElegido?.rol,
                        textoDe = { it.etiqueta() },
                        onSeleccionar = { rol -> rol?.let { viewModel.onRolChange(it.id) } },
                        mostrarTodos = false
                    )
                }
                item {
                    Text(
                        text = "Los cambios se aplican la próxima vez que cada persona inicie sesión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Una tarjeta por módulo, con sus permisos adentro
                estado.permisosPorModulo.forEach { (modulo, permisos) ->
                    item(key = "modulo-$modulo") {
                        Tarjeta(modifier = Modifier.fillMaxWidth().animateItem()) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(etiquetaModulo(modulo), style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "${permisos.count { estado.tiene(it) }} de ${permisos.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                permisos.forEachIndexed { indice, permiso ->
                                    if (indice > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                    val protegido = viewModel.esPermisoProtegido(permiso, sesion)
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                        headlineContent = { Text(permiso.descripcion) },
                                        supportingContent = {
                                            Text(
                                                if (protegido) "${permiso.codigo} · no puedes quitárselo a tu propio rol"
                                                else permiso.codigo
                                            )
                                        },
                                        trailingContent = {
                                            Switch(
                                                checked = estado.tiene(permiso),
                                                onCheckedChange = { viewModel.cambiarPermiso(permiso, it, sesion) },
                                                // El permiso protegido no se puede apagar, pero sí se ve
                                                enabled = !protegido
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
