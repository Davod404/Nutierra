package com.example.nutierra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutierra.data.local.entity.CarritoItemEntity
import com.example.nutierra.data.local.entity.CarritoItemDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query("""
        SELECT c.productoId, c.cantidad, p.nombre, p.categoria, p.precio,
               p.stock, p.peso, p.imagenUri
        FROM carrito c
        INNER JOIN productos p ON p.id = c.productoId
        WHERE c.usuarioId = :usuarioId
        ORDER BY p.nombre ASC
    """)
    fun obtenerCarrito(usuarioId: Int): Flow<List<CarritoItemDetalle>>

    @Query("""
        SELECT c.productoId, c.cantidad, p.nombre, p.categoria, p.precio,
               p.stock, p.peso, p.imagenUri
        FROM carrito c
        INNER JOIN productos p ON p.id = c.productoId
        WHERE c.usuarioId = :usuarioId
    """)
    suspend fun obtenerCarritoUnaVez(usuarioId: Int): List<CarritoItemDetalle>

    @Query("SELECT cantidad FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun cantidadActual(usuarioId: Int, productoId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: CarritoItemEntity)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun eliminar(usuarioId: Int, productoId: Int)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciar(usuarioId: Int)
}
