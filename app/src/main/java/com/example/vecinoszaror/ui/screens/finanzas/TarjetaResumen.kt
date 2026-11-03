package com.example.vecinoszaror.ui.screens.finanzas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.style.VecinosZarorTheme
import com.example.vecinoszaror.ui.utils.formatearMonto

/** Resumen con los ingresos, los gastos y el saldo. */
@Composable
fun TarjetaResumen(
    totalIngresos: Long,
    totalGastos: Long,
    saldo: Long,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilaResumen("Ingresos", formatearMonto(totalIngresos))
            FilaResumen("Gastos", formatearMonto(totalGastos))
            HorizontalDivider()
            FilaResumen(
                etiqueta = "Saldo",
                valor = formatearMonto(saldo),
                destacado = true,
                esNegativo = saldo < 0
            )
        }
    }
}

@Composable
private fun FilaResumen(
    etiqueta: String,
    valor: String,
    destacado: Boolean = false,
    esNegativo: Boolean = false
) {
    val estilo = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
    val color = if (esNegativo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = etiqueta, style = estilo)
        Text(
            text = valor,
            style = estilo,
            color = color,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaResumenPreview() {
    VecinosZarorTheme {
        TarjetaResumen(totalIngresos = 135000, totalGastos = 48000, saldo = 87000)
    }
}
