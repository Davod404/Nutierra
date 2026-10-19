package com.example.nutierra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.repository.UsuarioRepository
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.model.CampoFormulario
import com.example.nutierra.model.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado completo de la pantalla de inicio de sesión. */
data class LoginUiState(
    val correo: CampoFormulario = CampoFormulario("cliente@nutierra.cl"),
    val contrasena: CampoFormulario = CampoFormulario("Semillas2025!"),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    // Rol del usuario autenticado; cuando deja de ser null la pantalla navega
    val rolAutenticado: String? = null
) {
    // Rol sugerido mientras se escribe (solo informativo para las cuentas demo)
    val rolDetectado: String?
        get() = when (correo.valor.trim().lowercase()) {
            "admin@nutierra.cl" -> "Administrador"
            "cliente@nutierra.cl" -> "Cliente"
            else -> null
        }
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        UsuarioRepository(NutierraDatabase.getDatabase(application).usuarioDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Crea las cuentas de demostración en SQLite si todavía no existen
        viewModelScope.launch {
            try {
                repository.poblarSiEstaVacia()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorGeneral = "No se pudo preparar la base de datos") }
            }
        }
    }

    fun cambiarCorreo(valor: String) = _uiState.update {
        it.copy(correo = CampoFormulario(valor, Validaciones.correo(valor)), errorGeneral = null)
    }

    fun cambiarContrasena(valor: String) = _uiState.update {
        it.copy(
            contrasena = CampoFormulario(valor, Validaciones.contrasenaLogin(valor)),
            errorGeneral = null
        )
    }

    fun usarCuentaDemo(correo: String, contrasena: String) = _uiState.update {
        it.copy(
            correo = CampoFormulario(correo),
            contrasena = CampoFormulario(contrasena),
            errorGeneral = null
        )
    }

    fun iniciarSesion() {
        val actual = _uiState.value
        if (actual.cargando) return

        // Revalida todos los campos antes de intentar
        val validado = actual.copy(
            correo = actual.correo.copy(error = Validaciones.correo(actual.correo.valor)),
            contrasena = actual.contrasena.copy(
                error = Validaciones.contrasenaLogin(actual.contrasena.valor)
            ),
            errorGeneral = null
        )
        _uiState.value = validado
        if (!validado.correo.esValido || !validado.contrasena.esValido) return

        _uiState.update { it.copy(cargando = true) }

        viewModelScope.launch {
            delay(700) // pequeña espera para mostrar el indicador de carga
            val usuario = try {
                repository.autenticar(validado.correo.valor, validado.contrasena.valor)
            } catch (e: Exception) {
                null
            }

            // Se guarda quién inició sesión para mostrar su cuenta personal
            if (usuario != null) {
                SesionManager.iniciarSesion(usuario)
            }

            _uiState.update {
                if (usuario == null) {
                    it.copy(
                        cargando = false,
                        errorGeneral = "Credenciales incorrectas. Revisa tu correo y contraseña."
                    )
                } else {
                    it.copy(cargando = false, rolAutenticado = usuario.rol)
                }
            }
        }
    }

    // La pantalla avisa que ya navegó, para no navegar dos veces
    fun navegacionRealizada() = _uiState.update { it.copy(rolAutenticado = null) }
}
