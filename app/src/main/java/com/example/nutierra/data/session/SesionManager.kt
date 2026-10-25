package com.example.nutierra.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.nutierra.data.local.entity.UsuarioEntity

object SesionManager {
    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual: StateFlow<UsuarioEntity?> = _usuarioActual.asStateFlow()

    fun iniciarSesion(usuario: UsuarioEntity) {
        _usuarioActual.value = usuario
    }

    fun actualizarUsuario(usuario: UsuarioEntity) {
        _usuarioActual.value = usuario
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}
