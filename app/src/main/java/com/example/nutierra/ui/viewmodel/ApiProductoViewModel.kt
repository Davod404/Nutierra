package com.example.nutierra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.remote.ApiRepository
import com.example.nutierra.data.remote.dto.ProductoDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ApiProductoUiState(
    val isLoading: Boolean = false,
    val productos: List<ProductoDto> = emptyList(),
    val errorMessage: String? = null
)

class ApiProductoViewModel(
    private val repository: ApiRepository = ApiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiProductoUiState())
    val uiState: StateFlow<ApiProductoUiState> = _uiState.asStateFlow()

    init {
        fetchProductos()
    }

    fun fetchProductos() {
        viewModelScope.launch {
            _uiState.value = ApiProductoUiState(isLoading = true)
            val resultado = repository.getProductos()
            resultado.onSuccess { productos ->
                _uiState.value = ApiProductoUiState(productos = productos)
            }.onFailure { error ->
                _uiState.value = ApiProductoUiState(
                    errorMessage = "No se pudieron cargar los productos: ${error.message}"
                )
            }
        }
    }
}