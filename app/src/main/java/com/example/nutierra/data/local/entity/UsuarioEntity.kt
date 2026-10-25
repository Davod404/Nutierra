package com.example.nutierra.data.local.entity

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.Index

object Roles {
    const val ADMIN = "ADMIN"
    const val CLIENTE = "CLIENTE"
}

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
    val rol: String = Roles.CLIENTE,

    val telefono: String = "",
    val direccion: String = ""
)
