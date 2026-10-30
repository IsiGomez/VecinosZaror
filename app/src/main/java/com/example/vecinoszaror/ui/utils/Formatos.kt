package com.example.vecinoszaror.ui.utils

import com.example.vecinoszaror.model.EstadoReserva
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LOCAL_CHILE = Locale.forLanguageTag("es-CL")

/** Convierte milisegundos a texto */
fun formatearFechaHora(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", LOCAL_CHILE).format(Date(milisegundos))


/** Convierte un monto entero a pesos chilenos */
fun formatearMonto(monto: Long): String =
    NumberFormat.getCurrencyInstance(LOCAL_CHILE).format(monto)


/** Nombre del estado para mostrar */
fun EstadoReserva.etiqueta(): String = when (this) {
    EstadoReserva.PENDIENTE -> "Pendiente"
    EstadoReserva.CONFIRMADA -> "Confirmada"
    EstadoReserva.RECHAZADA -> "Rechazada"
    EstadoReserva.CANCELADA -> "Cancelada"
    EstadoReserva.FINALIZADA -> "Finalizada"
}