package com.example.nutierra.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.example.nutierra.data.local.entity.PedidoEntity
import com.example.nutierra.data.local.entity.CarritoItemDetalle
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.repository.CarritoRepository
import com.example.nutierra.data.repository.CompraException
import com.example.nutierra.data.session.SesionManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.util.Log

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        CarritoRepository(NutierraDatabase.getDatabase(application))

    private fun usuarioId(): Int = SesionManager.usuarioActual.value?.id ?: 0

    val items: StateFlow<List<CarritoItemDetalle>> =
        SesionManager.usuarioActual
            .flatMapLatest { usuario ->
                Log.d("CarritoViewModel", "observando carrito de ${usuario?.id}")
                repository.obtenerCarrito(usuario?.id ?: 0)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                emptyList()
            )

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _ultimoPedido = MutableStateFlow<PedidoEntity?>(null)
    val ultimoPedido: StateFlow<PedidoEntity?> = _ultimoPedido.asStateFlow()

    private val _ultimoPedidoItems = MutableStateFlow<List<CarritoItemDetalle>>(emptyList())
    val ultimoPedidoItems: StateFlow<List<CarritoItemDetalle>> = _ultimoPedidoItems.asStateFlow()

    fun agregar(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.agregar(usuarioId(), productoId, cantidad)
            } catch (e: CompraException) {
                _error.value = e.message
            } catch (e: Exception) {
                Log.e("CarritoViewModel", "Error agregando", e)
                _error.value = "No se pudo agregar al carrito"
            }
        }
    }

    fun cambiarCantidad(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            try {
                repository.cambiarCantidad(usuarioId(), productoId, cantidad)
            } catch (e: Exception) {
                Log.e("CarritoViewModel", "Error cambiando cantidad", e)
                _error.value = "No se pudo actualizar la cantidad"
            }
        }
    }

    fun eliminar(productoId: Int) {
        viewModelScope.launch {
            try {
                repository.eliminar(usuarioId(), productoId)
            } catch (e: Exception) {
                Log.e("CarritoViewModel", "Error eliminando", e)
                _error.value = "No se pudo quitar el producto"
            }
        }
    }

    fun confirmarCompra(onExito: (PedidoEntity) -> Unit) {
        if (_procesando.value) return
        viewModelScope.launch {
            _procesando.value = true
            _error.value = null

            val itemsAntes = items.value

            val pedido = try {
                repository.confirmarCompra(usuarioId())
            } catch (e: CompraException) {
                _error.value = e.message
                null
            } catch (e: Exception) {
                Log.e("CarritoViewModel", "Error confirmando compra", e)
                _error.value = "No se pudo confirmar la compra"
                null
            } finally {
                _procesando.value = false
            }

            if (pedido != null) {
                _ultimoPedido.value = pedido
                _ultimoPedidoItems.value = itemsAntes
                try {
                    onExito(pedido)
                } catch (e: Exception) {
                    Log.e("CarritoViewModel", "Error en onExito", e)
                }
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}