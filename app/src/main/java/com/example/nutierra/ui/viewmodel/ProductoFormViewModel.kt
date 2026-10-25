package com.example.nutierra.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.model.CampoFormulario
import com.example.nutierra.model.Validaciones
import androidx.lifecycle.ViewModel

data class ProductoFormUiState(
    val nombre: CampoFormulario = CampoFormulario(),
    val categoria: String = "Frutos secos",
    val precio: CampoFormulario = CampoFormulario(),
    val stock: Int = 0,
    val errorStock: String? = null,
    val peso: String = "500 g",
    val imagenUri: String? = null,
    val errorImagen: String? = null,
    val idCargado: Int? = null
) {
    val nombreValido: Boolean get() = Validaciones.nombreProducto(nombre.valor) == null
    val precioValido: Boolean get() = Validaciones.precio(precio.valor) == null
    val stockValido: Boolean get() = Validaciones.stock(stock) == null
}

class ProductoFormViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductoFormUiState())
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    fun iniciarNuevo() {
        if (_uiState.value.nombre.valor.isNotEmpty() || _uiState.value.idCargado != null) return
        _uiState.value = ProductoFormUiState(
            nombre = CampoFormulario(""),
            precio = CampoFormulario("")
        )
    }

    fun cargarProducto(producto: ProductoEntity) {
        if (_uiState.value.idCargado == producto.id) return
        _uiState.value = ProductoFormUiState(
            nombre = CampoFormulario(producto.nombre),
            categoria = producto.categoria,
            precio = CampoFormulario(producto.precio.toString()),
            stock = producto.stock,
            peso = producto.peso,
            imagenUri = producto.imagenUri,
            idCargado = producto.id
        )
    }

    fun cambiarNombre(valor: String) {
        if (valor.length > 60) return
        _uiState.update {
            it.copy(nombre = CampoFormulario(valor, Validaciones.nombreProducto(valor)))
        }
    }

    fun cambiarCategoria(categoria: String) = _uiState.update { it.copy(categoria = categoria) }

    fun cambiarPrecio(valor: String) {
        if (!valor.all { it.isDigit() }) return
        _uiState.update {
            it.copy(precio = CampoFormulario(valor, Validaciones.precio(valor)))
        }
    }

    fun aumentarStock() = _uiState.update {
        val nuevo = it.stock + 1
        it.copy(stock = nuevo, errorStock = Validaciones.stock(nuevo))
    }

    fun disminuirStock() = _uiState.update {
        if (it.stock == 0) it.copy(errorStock = "El stock no puede ser negativo")
        else it.copy(stock = it.stock - 1, errorStock = null)
    }

    fun cambiarPeso(peso: String) = _uiState.update { it.copy(peso = peso) }

    fun cambiarImagen(uri: String?) = _uiState.update {
        it.copy(imagenUri = uri, errorImagen = null)
    }

    fun validar(exigirImagen: Boolean): Boolean {
        val a = _uiState.value
        val validado = a.copy(
            nombre = a.nombre.copy(error = Validaciones.nombreProducto(a.nombre.valor)),
            precio = a.precio.copy(error = Validaciones.precio(a.precio.valor)),
            errorStock = Validaciones.stock(a.stock),
            errorImagen = if (exigirImagen) Validaciones.imagen(a.imagenUri) else null
        )
        _uiState.value = validado
        return validado.nombre.esValido &&
                validado.precio.esValido &&
                validado.errorStock == null &&
                validado.errorImagen == null
    }
}
