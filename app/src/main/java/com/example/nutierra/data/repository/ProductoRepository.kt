
package com.example.nutierra.data.repository

import com.example.nutierra.data.local.DatosIniciales
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao
) {

    // Obtener todos los productos
    val productos: Flow<List<ProductoEntity>> =
        productoDao.obtenerProductos()

    // Poblar el catálogo con los productos iniciales la primera vez (base vacía)
    suspend fun poblarSiEstaVacia() {
        if (productoDao.contarProductos() == 0) {
            productoDao.insertarSiNoExisten(DatosIniciales.productos)
        }
    }

    // Obtener un producto por ID
    suspend fun obtenerProductoPorId(id: Int): ProductoEntity? {
        return productoDao.obtenerProductoPorId(id)
    }

    // Insertar producto
    suspend fun insertarProducto(producto: ProductoEntity): Long {
        return productoDao.insertarProducto(producto)
    }

    // Actualizar producto
    suspend fun actualizarProducto(producto: ProductoEntity) {
        productoDao.actualizarProducto(producto)
    }

    // Eliminar producto
    suspend fun eliminarProducto(producto: ProductoEntity) {
        productoDao.eliminarProducto(producto)
    }

    // Actualizar stock
    suspend fun actualizarStock(id: Int, nuevoStock: Int) {
        require(nuevoStock >= 0) {
            "El stock no puede ser negativo"
        }

        productoDao.actualizarStock(id, nuevoStock)
    }

    // Buscar productos
    fun buscarProductos(busqueda: String): Flow<List<ProductoEntity>> {
        return productoDao.buscarProductos(busqueda)
    }

    // Contar productos registrados
    suspend fun contarProductos(): Int {
        return productoDao.contarProductos()
    }

}
