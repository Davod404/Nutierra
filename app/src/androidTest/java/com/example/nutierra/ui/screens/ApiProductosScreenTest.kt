package com.example.nutierra.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.nutierra.data.remote.ApiRepository
import com.example.nutierra.data.remote.ApiService
import com.example.nutierra.data.remote.dto.LoginRequest
import com.example.nutierra.data.remote.dto.PedidoDto
import com.example.nutierra.data.remote.dto.ProductoDto
import com.example.nutierra.data.remote.dto.UsuarioDto
import com.example.nutierra.ui.theme.NutierraTheme
import com.example.nutierra.ui.viewmodel.ApiProductoViewModel
import kotlinx.coroutines.awaitCancellation
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ApiProductosScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun mostrarPantalla(respuesta: suspend () -> List<ProductoDto>) {
        val viewModel = ApiProductoViewModel(ApiRepository(ApiFalsa(respuesta)))
        composeRule.setContent {
            NutierraTheme {
                ApiProductosScreen(viewModel = viewModel)
            }
        }
    }

    @Test
    fun mientrasCarga_muestraElIndicador() {
        mostrarPantalla { awaitCancellation() }
        composeRule.onNodeWithTag(TAG_CARGANDO_API).assertIsDisplayed()
    }

    @Test
    fun conDatos_muestraLosProductos() {
        mostrarPantalla {
            listOf(
                ProductoDto(
                    id = 1,
                    nombre = "Almendras de prueba",
                    categoria = "Frutos secos",
                    precio = 1000,
                    stock = 10,
                    peso = "250 g",
                    imagenUri = ""
                )
            )
        }
        composeRule.onNodeWithText("Almendras de prueba").assertIsDisplayed()
        composeRule.onNodeWithText("Frutos secos - $1000").assertIsDisplayed()
    }

    @Test
    fun siFallaLaRed_muestraElError() {
        mostrarPantalla { throw IOException("Sin conexión") }
        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }
}

private class ApiFalsa(
    private val respuesta: suspend () -> List<ProductoDto>
) : ApiService {
    override suspend fun getProductos(): List<ProductoDto> = respuesta()
    override suspend fun getPedidos(): List<PedidoDto> = error("No se usa")
    override suspend fun crearPedido(pedido: PedidoDto): PedidoDto = error("No se usa")
    override suspend fun getUsuarios(): List<UsuarioDto> = error("No se usa")
    override suspend fun login(request: LoginRequest): UsuarioDto = error("No se usa")
}