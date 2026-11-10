package com.example.vecinoszaror.model

/** Estados de una solicitud de arriendo. */
enum class EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    RECHAZADA,
    CANCELADA,
    FINALIZADA
}

enum class MetodoPago {
    EFECTIVO,
    TRANSFERENCIA,
    OTRO
}

enum class CategoriaGasto {
    LUZ,
    AGUA,
    GAS,
    INTERNET,
    ASEO,
    MANTENCION,
    OTRO
}

/** (confirmaciones, pagos, recordatorios). */
enum class TipoNotificacion {
    RESERVA_CONFIRMADA,
    RESERVA_RECHAZADA,
    RESERVA_CANCELADA,
    PAGO_REGISTRADO,
    RECORDATORIO,
    INFORME_PUBLICADO,
    GENERAL
}

/** Cómo va el pago de una reserva. */
enum class EstadoPago {
    /** Rechazada o cancelada: no hay nada que pagar. */
    NO_APLICA,
    SIN_COSTO,
    PENDIENTE,
    ABONADO,
    PAGADO
}

/** A quién se envía un aviso, [roles] son los nombres de rol que lo reciben. */
enum class Destinatario(val roles: List<String>) {
    TODOS(listOf("VECINO", "SECRETARIA", "TESORERIA", "DIRECTIVA")),
    VECINOS(listOf("VECINO")),
    DIRECTIVA(listOf("SECRETARIA", "TESORERIA", "DIRECTIVA"))
}
