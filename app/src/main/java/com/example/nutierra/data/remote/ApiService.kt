package com.example.nutierra.data.remote

import com.example.nutierra.data.remote.dto.PedidoDto
import com.example.nutierra.data.remote.dto.ProductoDto
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
}