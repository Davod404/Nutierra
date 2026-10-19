package com.example.nutierra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutierra.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    // Insertar un usuario nuevo (falla si el correo ya existe)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    // Insertar cuentas de demostración sin duplicarlas
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExisten(usuarios: List<UsuarioEntity>)

    // Buscar un usuario por su correo
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    // Contar usuarios registrados
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
