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
    PAGO_REGISTRADO,
    RECORDATORIO,
    INFORME_PUBLICADO,
    GENERAL
}
