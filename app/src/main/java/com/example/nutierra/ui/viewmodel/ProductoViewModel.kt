
package com.example.nutierra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) :
    AndroidViewModel(application) {

    // 1. Conexión con SQLite mediante Room
    private val database = NutierraDatabase.getDatabase(application)

    // 2. Conexión con el Repository
    private val repository = ProductoRepository(database.productoDao())

    // 3. Lista observable de productos
    val productos: StateFlow<List<ProductoEntity>> =
        repository.productos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 4. Producto seleccionado
    private val _productoSeleccionado =
        MutableStateFlow<ProductoEntity?>(null)

    val productoSeleccionado: StateFlow<ProductoEntity?> =
        _productoSeleccionado.asStateFlow()

    // 5. Estado de carga
    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    // 6. Mensajes de error
    private val _error = MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    // 7. Obtener producto por ID
    fun obtenerProductoPorId(id: Int) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                _productoSeleccionado.value =
                    repository.obtenerProductoPorId(id)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al obtener producto"
            } finally {
                _cargando.value = false
            }
        }
    }

    // 8. Insertar producto
    fun insertarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repository.insertarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al insertar producto"
            } finally {
                _cargando.value = false
            }
        }
    }

    // 9. Actualizar producto
    fun actualizarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repository.actualizarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar producto"
            } finally {
                _cargando.value = false
            }
        }
    }

    // 10. Eliminar producto
    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repository.eliminarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar producto"
            } finally {
                _cargando.value = false
            }
        }
    }

    // 11. Actualizar stock
    fun actualizarStock(id: Int, nuevoStock: Int) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                repository.actualizarStock(id, nuevoStock)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar stock"
            } finally {
                _cargando.value = false
            }
        }
    }

    // 12. Limpiar mensajes de error
    fun limpiarError() {
        _error.value = null
    }
}
