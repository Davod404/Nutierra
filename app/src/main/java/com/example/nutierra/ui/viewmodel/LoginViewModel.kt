package com.example.nutierra.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.nutierra.data.repository.UsuarioRepository
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.model.CampoFormulario
import com.example.nutierra.model.Validaciones
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import kotlin.time.Duration.Companion.milliseconds

data class LoginUiState(
    val correo: CampoFormulario = CampoFormulario("cliente@nutierra.cl"),
    val contrasena: CampoFormulario = CampoFormulario("Semillas2025!"),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val rolAutenticado: String? = null
) {
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
        viewModelScope.launch {
            try {
                repository.poblarSiEstaVacia()
            } catch (_: Exception) {
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
            delay(700.milliseconds)
            val usuario = try {
                repository.autenticar(validado.correo.valor, validado.contrasena.valor)
            } catch (_: Exception) {
                null
            }
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

    fun navegacionRealizada() = _uiState.update { it.copy(rolAutenticado = null) }
}
