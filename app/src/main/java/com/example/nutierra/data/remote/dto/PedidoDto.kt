package com.example.nutierra.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PedidoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("usuarioId") val usuarioId: Int,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("fecha") val fecha: Long,
    @SerializedName("total") val total: Int,
    @SerializedName("cantidadProductos") val cantidadProductos: Int,
    @SerializedName("cantidadUnidades") val cantidadUnidades: Int,
    @SerializedName("estado") val estado: String
)