
package com.example.nutierra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.nutierra.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Obtener todos los productos
    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    // Obtener un producto por su ID
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity?

    // Insertar un nuevo producto
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarProducto(producto: ProductoEntity): Long

    // Insertar datos iniciales sin duplicarlos (mismo id = se ignora)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExisten(productos: List<ProductoEntity>)

    // Actualizar un producto existente
    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    // Eliminar un producto
    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    // Actualizar únicamente el stock
    @Query("UPDATE productos SET stock = :nuevoStock WHERE id = :id")
    suspend fun actualizarStock(id: Int, nuevoStock: Int)

    // Buscar productos por nombre o categoría
    @Query("""
        SELECT * FROM productos
        WHERE nombre LIKE '%' || :busqueda || '%'
        OR categoria LIKE '%' || :busqueda || '%'
        ORDER BY nombre ASC
    """)
    fun buscarProductos(busqueda: String): Flow<List<ProductoEntity>>

    // Contar productos registrados
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int
}