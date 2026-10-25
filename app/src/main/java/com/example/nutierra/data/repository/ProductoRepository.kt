package com.example.nutierra.data.repository

import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.dao.ProductoDao
import com.example.nutierra.data.local.DatosIniciales
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao
) {
    val productos: Flow<List<ProductoEntity>> =
        productoDao.obtenerProductos()

    suspend fun poblarSiEstaVacia() {
        if (productoDao.contarProductos() == 0) {
            productoDao.insertarSiNoExisten(DatosIniciales.productos)
        }
    }

    suspend fun insertarProducto(producto: ProductoEntity): Long {
        return productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: ProductoEntity) {
        productoDao.actualizarProducto(producto)
    }

    suspend fun eliminarProducto(producto: ProductoEntity) {
        productoDao.eliminarProducto(producto)
    }

    suspend fun actualizarStock(id: Int, nuevoStock: Int) {
        require(nuevoStock >= 0) {
            "El stock no puede ser negativo"
        }

        productoDao.actualizarStock(id, nuevoStock)
    }
}
