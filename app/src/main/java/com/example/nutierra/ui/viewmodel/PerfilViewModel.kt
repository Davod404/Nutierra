package com.example.nutierra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.entity.UsuarioEntity
import com.example.nutierra.data.session.SesionManager
import kotlinx.coroutines.flow.StateFlow

/** Datos listos para mostrar en la pantalla de cuenta. */
data class PerfilUi(
    val nombreCompleto: String,
    val correo: String,
    val iniciales: String,
    val rol: String
)

/** Convierte el usuario guardado en SQLite en datos para la interfaz. */
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

class PerfilViewModel : ViewModel() {

    // Usuario que inició sesión (null si no hay sesión)
    val usuario: StateFlow<UsuarioEntity?> = SesionManager.usuarioActual

    fun cerrarSesion() {
        SesionManager.cerrarSesion()
    }
}
