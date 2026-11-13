package com.example.nutierra.data.remote

import com.example.nutierra.data.remote.dto.LoginRequest
import com.example.nutierra.data.remote.dto.PedidoDto
import com.example.nutierra.data.remote.dto.ProductoDto
import com.example.nutierra.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDto>

    @GET("api/pedidos")
    suspend fun getPedidos(): List<PedidoDto>

    @POST("api/pedidos")
    suspend fun crearPedido(@Body pedido: PedidoDto): PedidoDto

    @GET("api/usuarios")
    suspend fun getUsuarios(): List<UsuarioDto>

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): UsuarioDto
}