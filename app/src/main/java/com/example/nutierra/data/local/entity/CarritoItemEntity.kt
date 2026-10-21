package com.example.nutierra.data.local.entity

import androidx.room.Entity

/**
 * Tabla "carrito": cada fila es un producto en el carrito de un usuario.
 * La clave compuesta (usuarioId, productoId) evita repetir el mismo producto.
 */
@Entity(
    tableName = "carrito",
    primaryKeys = ["usuarioId", "productoId"]
)
data class CarritoItemEntity(
    val usuarioId: Int,
    val productoId: Int,
    val cantidad: Int
)

/** Resultado de unir el carrito con la tabla productos (para mostrarlo en pantalla). */
data class ItemCarrito(
    val productoId: Int,
    val cantidad: Int,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val peso: String,
    val imagenUri: String?
) {
    val subtotal: Int get() = precio * cantidad
}
