package com.example.vecinoszaror.util

import java.util.Calendar
import java.util.TimeZone

/**
 * El calendario entrega el día a medianoche en UTC
 * Se toma año, mes y día de ahí y se arma la fecha en la hora local.
 * [minutosDelDia] son los minutos desde la medianoche.
 */
fun combinarFechaYHora(fechaUtc: Long, minutosDelDia: Int): Long {
    val dia = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = fechaUtc }
    return Calendar.getInstance().apply {
        set(
            dia.get(Calendar.YEAR), dia.get(Calendar.MONTH), dia.get(Calendar.DAY_OF_MONTH),
            minutosDelDia / 60, minutosDelDia % 60, 0
        )
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private const val MILISEGUNDOS_POR_DIA = 24L * 60 * 60 * 1000

/** Comienzo (00:00) del día elegido en el calendario. */
fun inicioDelDia(fechaUtc: Long): Long = combinarFechaYHora(fechaUtc, 0)

/** Final (23:59:59.999) del día elegido en el calendario. */
fun finDelDia(fechaUtc: Long): Long = combinarFechaYHora(fechaUtc + MILISEGUNDOS_POR_DIA, 0) - 1