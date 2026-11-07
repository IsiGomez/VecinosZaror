package com.example.vecinoszaror.ui.screens.informes

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.InformeFinanciero
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.EstadoVacio
import com.example.vecinoszaror.ui.components.Tarjeta
import com.example.vecinoszaror.ui.utils.formatearFecha
import com.example.vecinoszaror.ui.utils.formatearMonto
import com.example.vecinoszaror.viewmodel.InformesViewModel

/** Rendiciones de cuentas por período. Quien tiene permiso puede publicarlas para los vecinos. */
@Composable
fun InformesScreen(
    viewModel: InformesViewModel,
    sesion: SesionUsuario,
    onGenerarInforme: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Avisa al ViewModel quién es el usuario, se repite solo si cambia la sesión
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    ContenidoCargando(cargando = estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 700.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Text("Informes", style = MaterialTheme.typography.headlineSmall) }

                if (estado.puedeGenerar) {
                    item { Button(onClick = onGenerarInforme) { Text("Generar informe") } }
                }

                if (estado.informes.isEmpty()) {
                    item {
                        EstadoVacio(
                            icono = Icons.Filled.Info,
                            titulo = if (estado.veBorradores) "Aún no hay informes" else "Aún no hay informes publicados",
                            mensaje = if (estado.veBorradores) {
                                "Genera el primer informe para rendir cuentas de un período."
                            } else {
                                "Cuando la directiva publique una rendición de cuentas, la verás aquí."
                            }
                        )
                    }
                } else {
                    items(estado.informes, key = { it.id }) { informe ->
                        TarjetaInforme(
                            // animateItem hace que la tarjeta se mueva suave cuando la lista cambia
                            modifier = Modifier.animateItem(),
                            informe = informe,
                            mostrarEstado = estado.veBorradores,
                            puedePublicar = estado.puedePublicar,
                            onCambiarPublicacion = { viewModel.cambiarPublicacion(informe) }
                        )
                    }
                }
            }
        }
    }
}

/** Un informe: período, totales, si corresponde, su estado y el botón para publicarlo. */
@Composable
private fun TarjetaInforme(
    modifier: Modifier = Modifier,
    informe: InformeFinanciero,
    mostrarEstado: Boolean,
    puedePublicar: Boolean,
    onCambiarPublicacion: () -> Unit
) {
    val colorEstado by animateColorAsState(
        targetValue = if (informe.publicado) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "colorEstado"
    )
    Tarjeta(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(informe.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Período: ${formatearFecha(informe.periodoInicio)} al ${formatearFecha(informe.periodoFin)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (mostrarEstado) {
                Text(
                    text = if (informe.publicado) "Publicado" else "Borrador",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (informe.publicado) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilaTotal("Ingresos", formatearMonto(informe.totalIngresos))
            FilaTotal("Gastos", formatearMonto(informe.totalGastos))
            FilaTotal("Saldo", formatearMonto(informe.saldo), destacado = true, esNegativo = informe.saldo < 0)

            informe.observaciones?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }

            if (puedePublicar) {
                OutlinedButton(onClick = onCambiarPublicacion) {
                    Text(if (informe.publicado) "Quitar publicación" else "Publicar")
                }
            }
        }
    }
}

/** Una línea de totales. */
@Composable
private fun FilaTotal(
    etiqueta: String,
    valor: String,
    destacado: Boolean = false,
    esNegativo: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
            color = if (esNegativo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}
