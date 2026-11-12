package com.example.nutierra.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("precio") val precio: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("peso") val peso: String,
    @SerializedName("imagenUri") val imagenUri: String? = null
)