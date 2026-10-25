package com.example.nutierra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.repository.UsuarioRepository
import com.example.nutierra.model.CampoFormulario
import com.example.nutierra.model.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: CampoFormulario = CampoFormulario(),
    val apellido: CampoFormulario = CampoFormulario(),
    val correo: CampoFormulario = CampoFormulario(),
    val contrasena: CampoFormulario = CampoFormulario(),
    val confirmacion: CampoFormulario = CampoFormulario(),
    val aceptaTerminos: Boolean = false,
    val errorTerminos: String? = null,
    val guardando: Boolean = false,
    val errorGeneral: String? = null,
    val registroExitoso: Boolean = false
) {
    val contrasenasCoinciden: Boolean
        get() = contrasena.valor.isNotEmpty() &&
                confirmacion.valor.isNotEmpty() &&
                contrasena.valor == confirmacion.valor

    val formularioValido: Boolean
        get() = listOf(nombre, apellido, correo, contrasena, confirmacion)
            .all { it.valor.isNotBlank() && it.esValido } && aceptaTerminos
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        UsuarioRepository(NutierraDatabase.getDatabase(application).usuarioDao())

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) = _uiState.update {
        it.copy(nombre = CampoFormulario(valor, Validaciones.texto(valor, "El nombre")))
    }

    fun cambiarApellido(valor: String) = _uiState.update {
        it.copy(apellido = CampoFormulario(valor, Validaciones.texto(valor, "El apellido")))
    }

    fun cambiarCorreo(valor: String) = _uiState.update {
        it.copy(correo = CampoFormulario(valor, Validaciones.correo(valor)), errorGeneral = null)
    }

    fun cambiarContrasena(valor: String) = _uiState.update {
        it.copy(
            contrasena = CampoFormulario(valor, Validaciones.contrasenaNueva(valor)),
            confirmacion = if (it.confirmacion.valor.isEmpty()) it.confirmacion
            else it.confirmacion.copy(
                error = Validaciones.confirmacion(valor, it.confirmacion.valor)
            )
        )
    }

    fun cambiarConfirmacion(valor: String) = _uiState.update {
        it.copy(
            confirmacion = CampoFormulario(
                valor,
                Validaciones.confirmacion(it.contrasena.valor, valor)
            )
        )
    }

    fun cambiarTerminos(acepta: Boolean) = _uiState.update {
        it.copy(aceptaTerminos = acepta, errorTerminos = null)
    }

    fun registrar() {
        val a = _uiState.value
        if (a.guardando) return

        val validado = a.copy(
            nombre = a.nombre.copy(error = Validaciones.texto(a.nombre.valor, "El nombre")),
            apellido = a.apellido.copy(error = Validaciones.texto(a.apellido.valor, "El apellido")),
            correo = a.correo.copy(error = Validaciones.correo(a.correo.valor)),
            contrasena = a.contrasena.copy(error = Validaciones.contrasenaNueva(a.contrasena.valor)),
            confirmacion = a.confirmacion.copy(
                error = Validaciones.confirmacion(a.contrasena.valor, a.confirmacion.valor)
            ),
            errorTerminos = if (a.aceptaTerminos) null
            else "Debes aceptar las condiciones de servicio",
            errorGeneral = null
        )
        _uiState.value = validado
        if (!validado.formularioValido) return

        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            try {
                if (repository.existeCorreo(validado.correo.valor)) {
                    _uiState.update {
                        it.copy(
                            guardando = false,
                            correo = it.correo.copy(error = "Este correo ya está registrado")
                        )
                    }
                } else {
                    repository.registrarCliente(
                        nombre = validado.nombre.valor,
                        apellido = validado.apellido.valor,
                        correo = validado.correo.valor,
                        contrasena = validado.contrasena.valor
                    )
                    _uiState.update { it.copy(guardando = false, registroExitoso = true) }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(guardando = false, errorGeneral = "No se pudo guardar la cuenta")
                }
            }
        }
    }

    fun registroConsumido() = _uiState.update { RegistroUiState() }
}
