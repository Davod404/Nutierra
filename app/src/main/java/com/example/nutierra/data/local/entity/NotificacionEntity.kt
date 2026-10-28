package com.example.nutierra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titulo: String,
    val mensaje: String,
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val tipo: String = TipoNotificacion.SISTEMA,
    val rolDestino: String = TipoNotificacion.ROL_TODOS
)

object TipoNotificacion {
    const val PEDIDO_CLIENTE = "PEDIDO_CLIENTE"
    const val PEDIDO_ADMIN = "PEDIDO_ADMIN"
    const val CARRITO = "CARRITO"
    const val SISTEMA = "SISTEMA"
    const val ROL_CLIENTE = "CLIENTE"
    const val ROL_ADMIN = "ADMIN"
    const val ROL_TODOS = "TODOS"
}