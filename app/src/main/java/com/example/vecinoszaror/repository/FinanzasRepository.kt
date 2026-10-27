package com.example.vecinoszaror.repository

import androidx.room.withTransaction
import com.example.vecinoszaror.database.AppDatabase
import com.example.vecinoszaror.model.CategoriaGasto
import com.example.vecinoszaror.model.EstadoReserva
import com.example.vecinoszaror.model.Gasto
import com.example.vecinoszaror.model.InformeFinanciero
import com.example.vecinoszaror.model.MetodoPago
import com.example.vecinoszaror.model.Notificacion
import com.example.vecinoszaror.model.Pago
import com.example.vecinoszaror.model.TipoNotificacion
import kotlinx.coroutines.flow.Flow

/** Ingresos (pagos), gastos e informes: lo que maneja Tesorería y consulta la Directiva. */
class FinanzasRepository(private val db: AppDatabase) {

    private val pagoDao = db.pagoDao()
    private val gastoDao = db.gastoDao()
    private val informeDao = db.informeFinancieroDao()
    private val reservaDao = db.reservaDao()
    private val notificacionDao = db.notificacionDao()

    // --- Ingresos ---
    fun observarPagos(): Flow<List<Pago>> = pagoDao.observarTodos()

    fun observarPagosDeReserva(reservaId: Long): Flow<List<Pago>> = pagoDao.observarPorReserva(reservaId)

    fun observarTotalIngresos(): Flow<Long> = pagoDao.observarTotalIngresos()

    /** Registra un pago y avisa al vecino de la reserva */
    suspend fun registrarPago(
        reservaId: Long,
        monto: Long,
        metodo: MetodoPago,
        registradoPorId: Long,
        comprobanteUri: String? = null,
        observacion: String? = null
    ): Long? {
        if (monto <= 0) return null
        return db.withTransaction {
            val reserva = reservaDao.obtenerPorId(reservaId) ?: return@withTransaction null
            // Solo se registran pagos sobre reservas vigentes (no canceladas, rechazadas ni finalizadas)
            if (reserva.estado != EstadoReserva.PENDIENTE && reserva.estado != EstadoReserva.CONFIRMADA) {
                return@withTransaction null
            }
            val id = pagoDao.insertar(
                Pago(
                    reservaId = reservaId,
                    monto = monto,
                    metodo = metodo,
                    comprobanteUri = comprobanteUri,
                    observacion = observacion,
                    registradoPorId = registradoPorId
                )
            )
            notificacionDao.insertar(
                Notificacion(
                    usuarioId = reserva.usuarioId,
                    tipo = TipoNotificacion.PAGO_REGISTRADO,
                    titulo = "Pago registrado",
                    mensaje = "Se registró un pago de $$monto asociado a tu reserva.",
                    reservaId = reservaId
                )
            )
            id
        }
    }

    // --- Gastos ---
    fun observarGastos(): Flow<List<Gasto>> = gastoDao.observarTodos()

    fun observarGastosPorCategoria(categoria: CategoriaGasto): Flow<List<Gasto>> =
        gastoDao.observarPorCategoria(categoria)

    fun observarTotalGastos(): Flow<Long> = gastoDao.observarTotalGastos()

    suspend fun registrarGasto(
        categoria: CategoriaGasto,
        descripcion: String,
        monto: Long,
        registradoPorId: Long,
        comprobanteUri: String? = null
    ): Long? {
        if (monto <= 0) return null
        return gastoDao.insertar(
            Gasto(
                categoria = categoria,
                descripcion = descripcion.trim(),
                monto = monto,
                comprobanteUri = comprobanteUri,
                registradoPorId = registradoPorId
            )
        )
    }

    // --- Informes / rendición de cuentas ---
    fun observarInformes(): Flow<List<InformeFinanciero>> = informeDao.observarTodos()

    fun observarInformesPublicados(): Flow<List<InformeFinanciero>> = informeDao.observarPublicados()

    /** Calcula ingresos, gastos y saldo del período y guarda el informe (sin publicar). */
    suspend fun generarInforme(
        titulo: String,
        periodoInicio: Long,
        periodoFin: Long,
        generadoPorId: Long,
        observaciones: String? = null
    ): Long = db.withTransaction {
        val ingresos = pagoDao.totalIngresos(periodoInicio, periodoFin)
        val gastos = gastoDao.totalGastos(periodoInicio, periodoFin)
        informeDao.insertar(
            InformeFinanciero(
                titulo = titulo.trim(),
                periodoInicio = periodoInicio,
                periodoFin = periodoFin,
                totalIngresos = ingresos,
                totalGastos = gastos,
                saldo = ingresos - gastos,
                observaciones = observaciones,
                generadoPorId = generadoPorId
            )
        )
    }

    suspend fun cambiarPublicacion(informeId: Long, publicado: Boolean) =
        informeDao.cambiarPublicacion(informeId, publicado)
}
