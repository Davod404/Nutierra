package com.example.nutierra.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.repository.ProductoRepository
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application

class ProductoViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = NutierraDatabase.getDatabase(application)
    private val repository = ProductoRepository(database.productoDao())

    val productos: StateFlow<List<ProductoEntity>> =
        repository.productos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.poblarSiEstaVacia()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al preparar el catálogo"
            }
        }
    }

    fun insertarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.insertarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al insertar producto"
            }
        }
    }

    fun actualizarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.actualizarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar producto"
            }
        }
    }

    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.eliminarProducto(producto)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar producto"
            }
        }
    }

    fun actualizarStock(id: Int, nuevoStock: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.actualizarStock(id, nuevoStock)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar stock"
            }
        }
    }
}