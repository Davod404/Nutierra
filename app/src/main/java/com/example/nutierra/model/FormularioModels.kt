package com.example.nutierra.model

/**
 * Un campo de formulario: el texto ingresado y su mensaje de error.
 * Si error es null, el campo es válido.
 */
data class CampoFormulario(
    val valor: String = "",
    val error: String? = null
) {
    val esValido: Boolean get() = error == null
}

/**
 * Reglas de validación centralizadas (no dependen de la interfaz ni de Android),
 * por eso se pueden probar con pruebas unitarias simples.
 */
object Validaciones {

    private val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun texto(valor: String, campo: String): String? = when {
        valor.isBlank() -> "$campo es obligatorio"
        valor.trim().length < 2 -> "Escribe al menos 2 caracteres"
        valor.trim().length > 40 -> "Máximo 40 caracteres"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !regexCorreo.matches(valor.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    fun contrasenaLogin(valor: String): String? =
        if (valor.isBlank()) "La contraseña es obligatoria" else null

    fun contrasenaNueva(valor: String): String? = when {
        valor.isBlank() -> "La contraseña es obligatoria"
        valor.length < 8 -> "Debe tener al menos 8 caracteres"
        valor.none { it.isDigit() } -> "Debe incluir al menos un número"
        else -> null
    }

    fun confirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Confirma tu contraseña"
        confirmacion != contrasena -> "Las contraseñas no coinciden"
        else -> null
    }
}
