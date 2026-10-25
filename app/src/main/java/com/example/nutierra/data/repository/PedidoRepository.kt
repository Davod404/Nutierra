package com.example.nutierra.data.repository

import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.local.entity.PedidoResumen
import com.example.nutierra.data.local.dao.PedidoDao
import kotlinx.coroutines.flow.Flow

class PedidoRepository(
    private val pedidoDao: PedidoDao
) {
    fun pedidosDeUsuario(usuarioId: Int): Flow<List<PedidoResumen>> =
        pedidoDao.obtenerResumenDeUsuario(usuarioId)

    fun todosLosPedidos(): Flow<List<PedidoResumen>> =
        pedidoDao.obtenerResumenTodos()

    suspend fun avanzarEstado(pedidoId: Int, estadoActual: String): String? {
        val siguiente = EstadosPedido.siguiente(estadoActual) ?: return null
        pedidoDao.actualizarEstado(pedidoId, siguiente)
        return siguiente
    }
}
