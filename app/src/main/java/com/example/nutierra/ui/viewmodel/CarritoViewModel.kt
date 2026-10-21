package com.example.nutierra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.local.entity.ItemCarrito
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.repository.CarritoRepository
import com.example.nutierra.data.repository.CompraException
import com.example.nutierra.data.session.SesionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Carrito de compras del usuario que inició sesión, guardado en SQLite.
 * Se comparte entre Detalle, Carrito, Checkout y Pedido confirmado.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        CarritoRepository(NutierraDatabase.getDatabase(application))

    // Id del usuario actual (0 si no hay sesión)
    private fun usuarioId(): Int = SesionManager.usuarioActual.value?.id ?: 0

    // Cada vez que cambia el usuario, se observa SU carrito
    val items: StateFlow<List<ItemCarrito>> =
        SesionManager.usuarioActual
            .flatMapLatest { usuario -> repository.obtenerCarrito(usuario?.id ?: 0) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Último pedido confirmado (lo muestra la pantalla de éxito)
    private val _ultimoPedido = MutableStateFlow<PedidoEntity?>(null)
    val ultimoPedido: StateFlow<PedidoEntity?> = _ultimoPedido.asStateFlow()

    fun agregar(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.agregar(usuarioId(), productoId, cantidad)
            } catch (e: CompraException) {
                _error.value = e.message
            } catch (e: Exception) {
                _error.value = "No se pudo agregar al carrito"
            }
        }
    }

    fun cambiarCantidad(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            try {
                repository.cambiarCantidad(usuarioId(), productoId, cantidad)
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar la cantidad"
            }
        }
    }

    fun eliminar(productoId: Int) {
        viewModelScope.launch {
            try {
                repository.eliminar(usuarioId(), productoId)
            } catch (e: Exception) {
                _error.value = "No se pudo quitar el producto"
            }
        }
    }

    /** Confirma la compra; si sale bien, llama a [onExito] con el pedido creado. */
    fun confirmarCompra(onExito: (PedidoEntity) -> Unit) {
        if (_procesando.value) return
        viewModelScope.launch {
            _procesando.value = true
            _error.value = null
            try {
                val pedido = repository.confirmarCompra(usuarioId())
                _ultimoPedido.value = pedido
                onExito(pedido)
            } catch (e: CompraException) {
                _error.value = e.message
            } catch (e: Exception) {
                _error.value = "No se pudo confirmar la compra"
            } finally {
                _procesando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}
