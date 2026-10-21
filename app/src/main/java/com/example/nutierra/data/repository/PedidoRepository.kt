package com.example.nutierra.data.repository

import com.example.nutierra.data.local.dao.PedidoDao
import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.local.entity.PedidoResumen
import kotlinx.coroutines.flow.Flow

class PedidoRepository(
    private val pedidoDao: PedidoDao
) {

    // Pedidos de un cliente
    fun pedidosDeUsuario(usuarioId: Int): Flow<List<PedidoResumen>> =
        pedidoDao.obtenerResumenDeUsuario(usuarioId)

    // Todos los pedidos (administrador)
    fun todosLosPedidos(): Flow<List<PedidoResumen>> =
        pedidoDao.obtenerResumenTodos()

    /** Pasa el pedido al estado siguiente. Devuelve el nuevo estado, o null si ya estaba entregado. */
    suspend fun avanzarEstado(pedidoId: Int, estadoActual: String): String? {
        val siguiente = EstadosPedido.siguiente(estadoActual) ?: return null
        pedidoDao.actualizarEstado(pedidoId, siguiente)
        return siguiente
    }
}
