package com.example.nutierra.data.remote

import com.example.nutierra.data.remote.dto.ProductoDto
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class ApiRepositoryTest {

    private val api = mockk<ApiService>()
    private val repository = ApiRepository(api)

    private val productosDePrueba = listOf(
        ProductoDto(
            id = 1,
            nombre = "Almendras Naturales 250 g",
            categoria = "Frutos secos",
            precio = 4990,
            stock = 24,
            peso = "250 g",
            imagenUri = ""
        ),
        ProductoDto(
            id = 2,
            nombre = "Nueces Mariposa 250 g",
            categoria = "Frutos secos",
            precio = 5490,
            stock = 18,
            peso = "250 g",
            imagenUri = ""
        )
    )

    @Test
    fun getProductos_devuelveLosProductosDeLaApi() = runTest {
        coEvery { api.getProductos() } returns productosDePrueba

        val resultado = repository.getProductos()

        resultado.isSuccess shouldBe true
        resultado.getOrNull() shouldBe productosDePrueba
        coVerify(exactly = 1) { api.getProductos() }
    }

    @Test
    fun getProductos_cuandoFallaLaRed_devuelveResultFailure() = runTest {
        coEvery { api.getProductos() } throws IOException("Sin conexión")

        val resultado = repository.getProductos()

        resultado.isFailure shouldBe true
        resultado.exceptionOrNull()?.message shouldBe "Sin conexión"
    }
}