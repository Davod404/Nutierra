ProductoEntity.kt

package com.example.nutierra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val categoria: String,
    val precio: Int,
    val stock: Int,
    val peso: String,
    val imagenUri: String? = null,
    val descripcion: String = ""
)
