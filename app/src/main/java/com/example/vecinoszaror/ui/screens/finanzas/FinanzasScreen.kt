package com.example.vecinoszaror.ui.screens.finanzas

import androidx.compose.foundation.clickable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vecinoszaror.model.SesionUsuario
import com.example.vecinoszaror.ui.components.ContenidoCargando
import com.example.vecinoszaror.ui.components.FilaFiltros
import com.example.vecinoszaror.ui.utils.etiqueta
import com.example.vecinoszaror.viewmodel.FinanzasViewModel

/** Resumen financiero con el detalle de los pagos recibidos y los gastos. */
@Composable
fun FinanzasScreen(
    viewModel: FinanzasViewModel,
    sesion: SesionUsuario,
    onRegistrarPago: () -> Unit,
    onRegistrarGasto: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    // Si cada sección está desplegada o no, rememberSaveable lo conserva al rotar la pantalla
    var pagosDesplegados by rememberSaveable { mutableStateOf(true) }
    var gastosDesplegados by rememberSaveable { mutableStateOf(true) }

    // Avisa al ViewModel quién es el usuario; se repite solo si cambia la sesión
    LaunchedEffect(sesion) {
        viewModel.cargar(sesion)
    }

    ContenidoCargando(cargando = estado.cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 700.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Finanzas", style = MaterialTheme.typography.headlineSmall) }

                item {
                    TarjetaResumen(
                        totalIngresos = estado.totalIngresos,
                        totalGastos = estado.totalGastos,
                        saldo = estado.saldo
                    )
                }

                if (estado.puedeRegistrarPago || estado.puedeRegistrarGasto) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (estado.puedeRegistrarPago) {
                                Button(onClick = onRegistrarPago) { Text("Registrar pago") }
                            }
                            if (estado.puedeRegistrarGasto) {
                                Button(onClick = onRegistrarGasto) { Text("Registrar gasto") }
                            }
                        }
                    }
                }

                item {
                    EncabezadoSeccion(
                        titulo = "Pagos recibidos",
                        cantidad = textoCantidad(estado.pagosVisibles.size, estado.pagos.size),
                        desplegada = pagosDesplegados,
                        onClick = { pagosDesplegados = !pagosDesplegados }
                    )
                }
                // Si la sección está plegada, no se dibuja ninguna de sus filas
                if (pagosDesplegados) {
                    if (estado.recintosConPagos.size > 1) {
                        item {
                            FilaFiltros(
                                opciones = estado.recintosConPagos,
                                seleccionada = estado.recintosConPagos.firstOrNull { it.id == estado.filtroRecintoId },
                                textoDe = { it.nombre },
                                onSeleccionar = { viewModel.onFiltroRecintoChange(it?.id) }
                            )
                        }
                    }
                    if (estado.pagosVisibles.isEmpty()) {
                        item { TextoVacio("Aún no hay pagos registrados.") }
                    } else {
                        /** key: identifica cada tarjeta para que Compose no las redibuje */
                        items(estado.pagosVisibles, key = { "pago-${it.id}" }) { pago ->
                            Column(modifier = Modifier.animateItem()) {
                                FilaPago(pago, nombreRecinto = estado.recintoPorReserva[pago.reservaId]?.nombre)
                                HorizontalDivider()
                            }
                        }
                    }
                }

                // Gastos
                item {
                    EncabezadoSeccion(
                        titulo = "Gastos",
                        cantidad = textoCantidad(estado.gastosVisibles.size, estado.gastos.size),
                        desplegada = gastosDesplegados,
                        onClick = { gastosDesplegados = !gastosDesplegados }
                    )
                }
                if (gastosDesplegados) {
                    if (estado.categoriasConGastos.size > 1) {
                        item {
                            FilaFiltros(
                                opciones = estado.categoriasConGastos,
                                seleccionada = estado.filtroCategoria,
                                textoDe = { it.etiqueta() },
                                onSeleccionar = viewModel::onFiltroCategoriaChange
                            )
                        }
                    }
                    if (estado.gastosVisibles.isEmpty()) {
                        item { TextoVacio("Aún no hay gastos registrados.") }
                    } else {
                        items(estado.gastosVisibles, key = { "gasto-${it.id}" }) { gasto ->
                            Column(modifier = Modifier.animateItem()) {
                                FilaGasto(gasto)
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}


private fun textoCantidad(visibles: Int, total: Int): String =
    if (visibles == total) "$total" else "$visibles de $total"


/** Sección que se despliega. */
@Composable
private fun EncabezadoSeccion(
    titulo: String,
    cantidad: String,
    desplegada: Boolean,
    onClick: () -> Unit
) {
    val giro by animateFloatAsState(targetValue = if (desplegada) 180f else 0f, label = "giroFlecha")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$titulo ($cantidad)", style = MaterialTheme.typography.titleMedium)
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = if (desplegada) "Ocultar $titulo" else "Mostrar $titulo",
            modifier = Modifier.rotate(giro)
        )
    }
}

@Composable
private fun TextoVacio(mensaje: String) {
    Text(
        text = mensaje,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}