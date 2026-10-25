package com.example.nutierra.data.repository

import com.example.nutierra.data.local.entity.UsuarioEntity
import com.example.nutierra.data.local.entity.Roles
import com.example.nutierra.data.local.DatosIniciales
import com.example.nutierra.data.local.dao.UsuarioDao
import java.security.MessageDigest

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    suspend fun poblarSiEstaVacia() {
        if (usuarioDao.contarUsuarios() == 0) {
            usuarioDao.insertarSiNoExisten(DatosIniciales.usuarios)
        }
    }

    suspend fun existeCorreo(correo: String): Boolean {
        return usuarioDao.buscarPorCorreo(normalizar(correo)) != null
    }

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

    suspend fun autenticar(correo: String, contrasena: String): UsuarioEntity? {
        val usuario = usuarioDao.buscarPorCorreo(normalizar(correo)) ?: return null
        return if (usuario.contrasenaHash == hash(contrasena)) usuario else null
    }

    companion object {
        fun normalizar(correo: String): String = correo.trim().lowercase()

        fun hash(texto: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest(texto.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
    }
}
