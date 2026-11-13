package com.example.nutierra.ui.viewmodel

import com.example.nutierra.data.remote.ApiRepository
import com.example.nutierra.data.remote.dto.ProductoDto
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ApiProductoViewModelTest {

    private val repository = mockk<ApiRepository>()

    private val productosDePrueba = listOf(
        ProductoDto(
            id = 1,
            nombre = "Almendras Naturales 250 g",
            categoria = "Frutos secos",
            precio = 4990,
            stock = 24,
            peso = "250 g",
            imagenUri = ""
        )
    )

    @Before
    fun prepararCorrutinas() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun restaurarCorrutinas() {
        Dispatchers.resetMain()
    }

    @Test
    fun alCrearse_cargaLosProductos() = runTest {
        coEvery { repository.getProductos() } returns Result.success(productosDePrueba)

        val viewModel = ApiProductoViewModel(repository)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        estado.productos shouldBe productosDePrueba
        estado.isLoading shouldBe false
        estado.errorMessage.shouldBeNull()
    }

    @Test
    fun mientrasDescarga_estaCargando() = runTest {
        coEvery { repository.getProductos() } coAnswers {
            delay(1_000.milliseconds)
            Result.success(productosDePrueba)
        }

        val viewModel = ApiProductoViewModel(repository)
        runCurrent()

        viewModel.uiState.value.isLoading shouldBe true

        advanceUntilIdle()

        viewModel.uiState.value.isLoading shouldBe false
    }

    @Test
    fun siFallaLaRed_guardaElError() = runTest {
        coEvery { repository.getProductos() } returns Result.failure(IOException("Sin conexión"))

        val viewModel = ApiProductoViewModel(repository)
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        estado.errorMessage!! shouldContain "Sin conexión"
        estado.productos.shouldBeEmpty()
        estado.isLoading shouldBe false
    }
}