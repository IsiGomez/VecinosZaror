package com.example.vecinoszaror.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.vecinoszaror.model.InformeFinanciero
import kotlinx.coroutines.flow.Flow

@Dao
interface InformeFinancieroDao {

    @Insert
    suspend fun insertar(informe: InformeFinanciero): Long

    @Query("SELECT * FROM informe_financiero WHERE id = :id")
    suspend fun obtenerPorId(id: Long): InformeFinanciero?

    /** Todos los informes (Tesorería y Directiva). */
    @Query("SELECT * FROM informe_financiero ORDER BY fechaGeneracion DESC")
    fun observarTodos(): Flow<List<InformeFinanciero>>

    /** Solo los publicados (lo que ven los vecinos). */
    @Query("SELECT * FROM informe_financiero WHERE publicado = 1 ORDER BY periodoFin DESC")
    fun observarPublicados(): Flow<List<InformeFinanciero>>

    @Query("UPDATE informe_financiero SET publicado = :publicado WHERE id = :id")
    suspend fun cambiarPublicacion(id: Long, publicado: Boolean)
}
