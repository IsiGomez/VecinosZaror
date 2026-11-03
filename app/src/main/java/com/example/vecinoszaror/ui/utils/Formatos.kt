package com.example.vecinoszaror.ui.utils

import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.MetodoPago
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val LOCAL_CHILE = Locale.forLanguageTag("es-CL")

/** Convierte milisegundos a texto */
fun formatearFechaHora(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", LOCAL_CHILE).format(Date(milisegundos))


fun formatearFechaUtc(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy", LOCAL_CHILE).apply{ timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(milisegundos))

fun formatearHora(minutosDelDia: Int): String =
    "%02d:%02d".format(minutosDelDia / 60, minutosDelDia % 60)


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


/** Nombre del método de pago para mostrar */
fun MetodoPago.etiqueta(): String = when (this) {
    MetodoPago.EFECTIVO -> "Efectivo"
    MetodoPago.TRANSFERENCIA -> "Transferencia"
    MetodoPago.OTRO -> "Otro"
}


/** Nombre de la categoría de gasto para mostrar */
fun CategoriaGasto.etiqueta(): String = when (this) {
    CategoriaGasto.LUZ -> "Luz"
    CategoriaGasto.AGUA -> "Agua"
    CategoriaGasto.GAS -> "Gas"
    CategoriaGasto.INTERNET -> "Internet"
    CategoriaGasto.ASEO -> "Aseo"
    CategoriaGasto.MANTENCION -> "Mantención"
    CategoriaGasto.OTRO -> "Otro"
}