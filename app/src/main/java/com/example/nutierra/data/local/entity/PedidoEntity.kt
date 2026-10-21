package com.example.nutierra.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Estados por los que pasa un pedido, en orden. */
object EstadosPedido {
    const val PENDIENTE = "Pendiente"
    const val CONFIRMADO = "Confirmado"
    const val PREPARADO = "Preparado"
    const val ENTREGADO = "Entregado"

    val orden = listOf(PENDIENTE, CONFIRMADO, PREPARADO, ENTREGADO)

    /** Estado siguiente, o null si ya está entregado. */
    fun siguiente(estado: String): String? =
        orden.getOrNull(orden.indexOf(estado) + 1)?.takeIf { estado in orden }

    /** Paso del 1 al 4 (para la barra de progreso del cliente). */
    fun paso(estado: String): Int = (orden.indexOf(estado) + 1).coerceAtLeast(1)
}

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
    val estado: String = EstadosPedido.PENDIENTE
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

/** Pedido con el nombre del cliente y la lista de productos (resultado de un JOIN). */
data class PedidoResumen(
    @Embedded val pedido: PedidoEntity,
    val clienteNombre: String?,
    val clienteApellido: String?,
    val productos: String?
)
