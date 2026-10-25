package com.example.nutierra.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import com.example.nutierra.data.local.entity.PedidoResumen
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.repository.PedidoRepository
import com.example.nutierra.data.session.SesionManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application

@OptIn(ExperimentalCoroutinesApi::class)
class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        PedidoRepository(NutierraDatabase.getDatabase(application).pedidoDao())

    val misPedidos: StateFlow<List<PedidoResumen>> =
        SesionManager.usuarioActual
            .flatMapLatest { usuario -> repository.pedidosDeUsuario(usuario?.id ?: 0) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val todosLosPedidos: StateFlow<List<PedidoResumen>> =
        repository.todosLosPedidos()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun avanzarEstado(pedidoId: Int, estadoActual: String) {
        viewModelScope.launch {
            try {
                repository.avanzarEstado(pedidoId, estadoActual)
            } catch (_: Exception) {
                _error.value = "No se pudo actualizar el estado del pedido"
            }
        }
    }
}
