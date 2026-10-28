package com.example.nutierra.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "carrito",
    primaryKeys = ["usuarioId", "productoId"]
)
data class CarritoItemEntity(
    val usuarioId: Int,
    val productoId: Int,
    val cantidad: Int
)

data class CarritoItemDetalle(
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