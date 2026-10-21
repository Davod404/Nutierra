package com.example.nutierra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.ItemCarrito
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    // Carrito del usuario con los datos actuales de cada producto (JOIN)
    @Query("""
        SELECT c.productoId, c.cantidad, p.nombre, p.categoria, p.precio,
               p.stock, p.peso, p.imagenUri
        FROM carrito c
        INNER JOIN productos p ON p.id = c.productoId
        WHERE c.usuarioId = :usuarioId
        ORDER BY p.nombre ASC
    """)
    fun obtenerCarrito(usuarioId: Int): Flow<List<ItemCarrito>>

    // Lo mismo, pero una sola vez (para confirmar la compra)
    @Query("""
        SELECT c.productoId, c.cantidad, p.nombre, p.categoria, p.precio,
               p.stock, p.peso, p.imagenUri
        FROM carrito c
        INNER JOIN productos p ON p.id = c.productoId
        WHERE c.usuarioId = :usuarioId
    """)
    suspend fun obtenerCarritoUnaVez(usuarioId: Int): List<ItemCarrito>

    // Cantidad que ya hay de un producto en el carrito (null si no está)
    @Query("SELECT cantidad FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun cantidadActual(usuarioId: Int, productoId: Int): Int?

    // Agregar o reemplazar una línea del carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: CarritoItemEntity)

    // Quitar un producto del carrito
    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun eliminar(usuarioId: Int, productoId: Int)

    // Vaciar el carrito del usuario
    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciar(usuarioId: Int)
}
