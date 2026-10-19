package com.example.nutierra.data.repository

import com.example.nutierra.data.local.DatosIniciales
import com.example.nutierra.data.local.dao.UsuarioDao
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.entity.UsuarioEntity
import java.security.MessageDigest

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // Crea las cuentas de demostración la primera vez
    suspend fun poblarSiEstaVacia() {
        if (usuarioDao.contarUsuarios() == 0) {
            usuarioDao.insertarSiNoExisten(DatosIniciales.usuarios)
        }
    }

    // ¿Existe ya una cuenta con este correo?
    suspend fun existeCorreo(correo: String): Boolean {
        return usuarioDao.buscarPorCorreo(normalizar(correo)) != null
    }

    // Registrar un cliente nuevo
    suspend fun registrarCliente(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String
    ): Long {
        return usuarioDao.insertarUsuario(
            UsuarioEntity(
                nombre = nombre.trim(),
                apellido = apellido.trim(),
                correo = normalizar(correo),
                contrasenaHash = hash(contrasena),
                rol = Roles.CLIENTE
            )
        )
    }

    // Devuelve el usuario si el correo y la contraseña coinciden; si no, null
    suspend fun autenticar(correo: String, contrasena: String): UsuarioEntity? {
        val usuario = usuarioDao.buscarPorCorreo(normalizar(correo)) ?: return null
        return if (usuario.contrasenaHash == hash(contrasena)) usuario else null
    }

    companion object {
        fun normalizar(correo: String): String = correo.trim().lowercase()

        // Hash SHA-256 en hexadecimal (no se guarda la contraseña en texto plano)
        fun hash(texto: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest(texto.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
    }
}
