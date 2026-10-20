package com.example.nutierra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.model.CampoFormulario
import com.example.nutierra.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Estado del formulario de producto (crear o editar). */
data class ProductoFormUiState(
    val nombre: CampoFormulario = CampoFormulario(),
    val categoria: String = "Frutos secos",
    val precio: CampoFormulario = CampoFormulario(),
    val stock: Int = 0,
    val errorStock: String? = null,
    val peso: String = "500 g",
    val imagenUri: String? = null,
    val errorImagen: String? = null,
    // id del producto que se está editando (null = producto nuevo)
    val idCargado: Int? = null
) {
    // Validez "en vivo" para el resumen de la pantalla
    val nombreValido: Boolean get() = Validaciones.nombreProducto(nombre.valor) == null
    val precioValido: Boolean get() = Validaciones.precio(precio.valor) == null
    val stockValido: Boolean get() = Validaciones.stock(stock) == null
    val stockBajo: Boolean get() = stock < 5
}

/**
 * Lógica y validaciones del formulario de productos del administrador.
 * La pantalla solo muestra el estado y avisa los cambios; aquí se decide si los datos son válidos.
 */
class ProductoFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoFormUiState())
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    /** Prepara el formulario para crear un producto nuevo, con valores de ejemplo. */
    fun iniciarNuevo() {
        if (_uiState.value.nombre.valor.isNotEmpty() || _uiState.value.idCargado != null) return
        _uiState.value = ProductoFormUiState(
            nombre = CampoFormulario("Nueces Chandler Peladas 500 g"),
            precio = CampoFormulario("4990")
        )
    }

    /** Rellena el formulario con un producto guardado en SQLite (solo la primera vez). */
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
        // El campo solo acepta dígitos
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

    /**
     * Revalida todo el formulario. Devuelve true si se puede guardar.
     * @param exigirImagen al crear la imagen es obligatoria; al editar se conserva la anterior.
     */
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
