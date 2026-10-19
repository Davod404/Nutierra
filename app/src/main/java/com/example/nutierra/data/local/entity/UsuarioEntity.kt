package com.example.nutierra.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Roles posibles dentro de la aplicación. */
object Roles {
    const val ADMIN = "ADMIN"
    const val CLIENTE = "CLIENTE"
}

/**
 * Tabla "usuarios". El índice único sobre el correo impide registrar
 * dos cuentas con el mismo correo. La contraseña se guarda como hash SHA-256.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasenaHash: String,
    val rol: String = Roles.CLIENTE
)
