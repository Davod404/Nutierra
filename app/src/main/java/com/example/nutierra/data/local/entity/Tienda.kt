package com.example.nutierra.model

data class Tienda(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val lat: Double,
    val lon: Double,
    val telefono: String,
    val horario: String
)