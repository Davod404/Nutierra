package com.example.nutierra.data.remote

import com.example.nutierra.data.remote.dto.PedidoDto
import com.example.nutierra.data.remote.dto.ProductoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    suspend fun getProductos(): Result<List<ProductoDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getProductos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPedidos(): Result<List<PedidoDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getPedidos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearPedido(pedido: PedidoDto): Result<PedidoDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.crearPedido(pedido))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}