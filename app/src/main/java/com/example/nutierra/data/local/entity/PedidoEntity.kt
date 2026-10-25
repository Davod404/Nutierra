package com.example.nutierra.data.local.entity

import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index

object EstadosPedido {
    const val PENDIENTE = "Pendiente"
    const val CONFIRMADO = "Confirmado"
    const val PREPARADO = "Preparado"
    const val ENTREGADO = "Entregado"

    val orden = listOf(PENDIENTE, CONFIRMADO, PREPARADO, ENTREGADO)

    fun siguiente(estado: String): String? =
        orden.getOrNull(orden.indexOf(estado) + 1)?.takeIf { estado in orden }

    fun paso(estado: String): Int = (orden.indexOf(estado) + 1).coerceAtLeast(1)
}

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: Int,
    val codigo: String,
    val fecha: Long,
    val total: Int,
    val cantidadProductos: Int,
    val cantidadUnidades: Int,
    val estado: String = EstadosPedido.PENDIENTE
)

@Entity(
    tableName = "pedido_detalle",
    indices = [Index(value = ["pedidoId"])]
)
data class PedidoDetalleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pedidoId: Int,
    val productoId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int
)

data class PedidoResumen(
    @Embedded val pedido: PedidoEntity,
    val clienteNombre: String?,
    val clienteApellido: String?,
    val productos: String?
)
