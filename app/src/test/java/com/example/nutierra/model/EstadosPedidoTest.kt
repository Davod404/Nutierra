package com.example.nutierra.model

import com.example.nutierra.data.local.entity.EstadosPedido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EstadosPedidoTest {

    @Test
    fun `el pedido avanza en orden hasta entregado`() {
        assertEquals("Confirmado", EstadosPedido.siguiente("Pendiente"))
        assertEquals("Preparado", EstadosPedido.siguiente("Confirmado"))
        assertEquals("Entregado", EstadosPedido.siguiente("Preparado"))
        assertNull(EstadosPedido.siguiente("Entregado"))
    }

    @Test
    fun `paso para la barra de progreso`() {
        assertEquals(1, EstadosPedido.paso("Pendiente"))
        assertEquals(4, EstadosPedido.paso("Entregado"))
    }
}
