package com.example.nutierra.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla "pedidos": una compra confirmada por un cliente. */
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
    val estado: String = "Confirmado"
)

/** Tabla "pedido_detalle": los productos de cada pedido, con el precio al momento de comprar. */
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
