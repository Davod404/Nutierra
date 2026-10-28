package com.example.nutierra.data.local.dao

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Dao
import com.example.nutierra.data.local.entity.NotificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(notificacion: NotificacionEntity): Long

    @Query("SELECT * FROM notificaciones ORDER BY fecha DESC")
    fun observarTodas(): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE leida = 0")
    fun observarNoLeidas(): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: Int)

    @Query("""
        SELECT * FROM notificaciones
        WHERE rolDestino = :rol OR rolDestino = 'TODOS'
        ORDER BY fecha DESC
    """)
    fun observarPorRol(rol: String): Flow<List<NotificacionEntity>>

    @Query("""
        SELECT COUNT(*) FROM notificaciones
        WHERE leida = 0 AND (rolDestino = :rol OR rolDestino = 'TODOS')
    """)
    fun observarNoLeidasPorRol(rol: String): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1")
    suspend fun marcarTodasComoLeidas()

    @Query("DELETE FROM notificaciones")
    suspend fun borrarTodas()
}