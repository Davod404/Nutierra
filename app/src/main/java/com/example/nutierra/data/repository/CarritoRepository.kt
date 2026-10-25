package com.example.nutierra.data.repository

import com.example.nutierra.data.local.entity.PedidoDetalleEntity
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.entity.ItemCarrito
import com.example.nutierra.data.local.NutierraDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

class CompraException(mensaje: String) : Exception(mensaje)

class CarritoRepository(
    private val database: NutierraDatabase
) {
    private val carritoDao = database.carritoDao()
    private val pedidoDao = database.pedidoDao()
    private val productoDao = database.productoDao()

    fun obtenerCarrito(usuarioId: Int): Flow<List<ItemCarrito>> =
        carritoDao.obtenerCarrito(usuarioId)

    suspend fun agregar(usuarioId: Int, productoId: Int, cantidad: Int): Int {
        val producto = productoDao.obtenerProductoPorId(productoId)
            ?: throw CompraException("El producto ya no existe")
        if (producto.stock <= 0) throw CompraException("${producto.nombre} no tiene stock")

        val actual = carritoDao.cantidadActual(usuarioId, productoId) ?: 0
        val nueva = (actual + cantidad).coerceAtMost(producto.stock)
        carritoDao.guardar(CarritoItemEntity(usuarioId, productoId, nueva))
        return nueva
    }

    suspend fun cambiarCantidad(usuarioId: Int, productoId: Int, cantidad: Int) {
        if (cantidad <= 0) {
            carritoDao.eliminar(usuarioId, productoId)
            return
        }
        val stock = productoDao.obtenerProductoPorId(productoId)?.stock ?: 0
        carritoDao.guardar(
            CarritoItemEntity(usuarioId, productoId, cantidad.coerceAtMost(stock))
        )
    }

    suspend fun eliminar(usuarioId: Int, productoId: Int) =
        carritoDao.eliminar(usuarioId, productoId)

    suspend fun confirmarCompra(usuarioId: Int): PedidoEntity =
        database.withTransaction {
            val items = carritoDao.obtenerCarritoUnaVez(usuarioId)
            if (items.isEmpty()) throw CompraException("Tu carrito está vacío")

            items.firstOrNull { it.cantidad > it.stock }?.let {
                throw CompraException("Stock insuficiente de ${it.nombre} (quedan ${it.stock})")
            }

            val fecha = System.currentTimeMillis()
            val pedido = PedidoEntity(
                usuarioId = usuarioId,
                codigo = "#PED-" + (fecha % 1_000_000).toString().padStart(6, '0'),
                fecha = fecha,
                total = items.sumOf { it.subtotal },
                cantidadProductos = items.size,
                cantidadUnidades = items.sumOf { it.cantidad }
            )
            val pedidoId = pedidoDao.insertarPedido(pedido).toInt()

            pedidoDao.insertarDetalles(
                items.map {
                    PedidoDetalleEntity(
                        pedidoId = pedidoId,
                        productoId = it.productoId,
                        nombre = it.nombre,
                        precio = it.precio,
                        cantidad = it.cantidad
                    )
                }
            )

            items.forEach {
                productoDao.actualizarStock(it.productoId, it.stock - it.cantidad)
            }

            carritoDao.vaciar(usuarioId)
            pedido.copy(id = pedidoId)
        }
}
