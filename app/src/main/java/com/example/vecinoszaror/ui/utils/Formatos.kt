package com.example.vecinoszaror.ui.utils

import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.Destinatario
import com.example.vecinoszaror.model.EstadoPago
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.MetodoPago
import com.example.vecinoszaror.model.Rol
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val LOCAL_CHILE = Locale.forLanguageTag("es-CL")

/** Convierte milisegundos a texto */
fun formatearFechaHora(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", LOCAL_CHILE).format(Date(milisegundos))

/** Convierte milisegundos a texto, solo la fecha en hora local */
fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy", LOCAL_CHILE).format(Date(milisegundos))

/** Convierte el día del calendario (que viene en UTC) a texto */
fun formatearFechaUtc(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy", LOCAL_CHILE).apply{ timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(milisegundos))

/** Convierte minutos desde la medianoche a hora */
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


/** Nombre del estado del pago para mostrar */
fun EstadoPago.etiqueta(): String = when (this) {
    EstadoPago.NO_APLICA -> "No aplica"
    EstadoPago.SIN_COSTO -> "Sin costo"
    EstadoPago.PENDIENTE -> "Pendiente"
    EstadoPago.ABONADO -> "Abonado"
    EstadoPago.PAGADO -> "Pagado"
}


/** Nombre de cada opción de destinatario de un aviso */
fun Destinatario.etiqueta(): String = when (this) {
    Destinatario.TODOS -> "Todos"
    Destinatario.VECINOS -> "Solo vecinos"
    Destinatario.DIRECTIVA -> "Toda la directiva (Secretaría, Tesorería y Directiva)"
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


/** Nombre del rol para mostrar. */
fun Rol.etiqueta(): String = when (nombre) {
    "VECINO" -> "Vecino/a"
    "SECRETARIA" -> "Secretaría"
    "TESORERIA" -> "Tesorería"
    "DIRECTIVA" -> "Directiva"
    else -> nombre
}

/** Nombre del módulo de un permiso para mostrar. */
fun etiquetaModulo(modulo: String): String = when (modulo) {
    "RESERVAS" -> "Reservas"
    "FINANZAS" -> "Finanzas"
    "INFORMES" -> "Informes"
    "AVISOS" -> "Avisos"
    "ADMINISTRACION" -> "Administración"
    else -> modulo
}
