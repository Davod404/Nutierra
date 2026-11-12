package com.example.nutierra.ui.viewmodel

import com.example.nutierra.data.local.entity.UsuarioEntity
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.session.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import com.example.nutierra.data.session.SessionPreferences

data class PerfilUi(
    val nombreCompleto: String,
    val correo: String,
    val iniciales: String,
    val rol: String
)

fun UsuarioEntity?.aPerfilUi(): PerfilUi {
    if (this == null) {
        return PerfilUi("Invitado", "Sin sesión iniciada", "?", "Sin sesión")
    }
    val iniciales = listOf(nombre, apellido)
        .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
        .joinToString("")
    return PerfilUi(
        nombreCompleto = "$nombre $apellido".trim(),
        correo = correo,
        iniciales = iniciales.ifEmpty { "?" },
        rol = if (rol == Roles.ADMIN) "Administrador" else "Cliente"
    )
}

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = NutierraDatabase.getDatabase(application).usuarioDao()

    val usuario: StateFlow<UsuarioEntity?> = SesionManager.usuarioActual

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cerrarSesion() {
        SesionManager.cerrarSesion()
        viewModelScope.launch {
            SessionPreferences.borrarUsuarioId(getApplication())
        }
    }

    fun actualizarPerfil(
        nombre: String,
        apellido: String,
        correo: String,
        telefono: String,
        direccion: String,
        onExito: () -> Unit
    ) {
        val actual = SesionManager.usuarioActual.value ?: return

        viewModelScope.launch {
            _error.value = null
            try {
                val actualizado = actual.copy(
                    nombre = nombre.trim(),
                    apellido = apellido.trim(),
                    correo = correo.trim(),
                    telefono = telefono.trim(),
                    direccion = direccion.trim()
                )
                usuarioDao.actualizarUsuario(actualizado)
                SesionManager.actualizarUsuario(actualizado)
                onExito()
            } catch (_: Exception) {
                _error.value = "No se pudo actualizar el perfil"
            }
        }
    }
}