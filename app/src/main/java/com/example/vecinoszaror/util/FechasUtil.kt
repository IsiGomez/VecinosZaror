package com.example.vecinoszaror.util

import java.util.Calendar
import java.util.TimeZone

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