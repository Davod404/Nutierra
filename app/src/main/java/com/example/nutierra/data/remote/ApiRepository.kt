package com.example.nutierra.data.remote

import com.example.nutierra.data.remote.dto.LoginRequest
import com.example.nutierra.data.remote.dto.PedidoDto
import com.example.nutierra.data.remote.dto.ProductoDto
import com.example.nutierra.data.remote.dto.UsuarioDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class ApiRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {
    open suspend fun getProductos(): Result<List<ProductoDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getProductos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun getPedidos(): Result<List<PedidoDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getPedidos())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun crearPedido(pedido: PedidoDto): Result<PedidoDto> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.crearPedido(pedido))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun getUsuarios(): Result<List<UsuarioDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getUsuarios())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    open suspend fun login(correo: String, contrasena: String): Result<UsuarioDto> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(apiService.login(LoginRequest(correo, contrasena)))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}