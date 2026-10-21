package com.example.nutierra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert
    suspend fun insertarDetalles(detalles: List<PedidoDetalleEntity>)

    // Pedidos de un cliente, del más reciente al más antiguo
    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun obtenerPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoEntity>>

    // Todos los pedidos (vista del administrador)
    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<PedidoEntity>>

    // Productos de un pedido
    @Query("SELECT * FROM pedido_detalle WHERE pedidoId = :pedidoId")
    suspend fun obtenerDetalles(pedidoId: Int): List<PedidoDetalleEntity>

    // Cambiar el estado de un pedido (administrador)
    @Query("UPDATE pedidos SET estado = :estado WHERE id = :pedidoId")
    suspend fun actualizarEstado(pedidoId: Int, estado: String)
}
