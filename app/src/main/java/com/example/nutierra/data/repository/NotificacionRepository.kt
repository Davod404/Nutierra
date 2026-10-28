package com.example.nutierra.data.repository

import com.example.nutierra.data.local.entity.NotificacionEntity
import com.example.nutierra.data.local.dao.NotificacionDao
import kotlinx.coroutines.flow.Flow

class NotificacionRepository(
    private val dao: NotificacionDao
) {
    fun observarTodas(): Flow<List<NotificacionEntity>> = dao.observarTodas()

    fun observarNoLeidas(): Flow<Int> = dao.observarNoLeidas()

    fun observarPorRol(rol: String): Flow<List<NotificacionEntity>> = dao.observarPorRol(rol)

    fun observarNoLeidasPorRol(rol: String): Flow<Int> = dao.observarNoLeidasPorRol(rol)

    suspend fun insertar(notificacion: NotificacionEntity): Long = dao.insertar(notificacion)

    suspend fun marcarComoLeida(id: Int) = dao.marcarComoLeida(id)

    suspend fun marcarTodasComoLeidas() = dao.marcarTodasComoLeidas()

    suspend fun borrarTodas() = dao.borrarTodas()
}