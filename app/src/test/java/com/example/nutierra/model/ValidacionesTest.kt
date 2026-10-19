package com.example.nutierra.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun `correo valido no tiene error`() {
        assertNull(Validaciones.correo("cliente@nutierra.cl"))
    }

    @Test
    fun `correo vacio o mal escrito tiene error`() {
        assertEquals("El correo es obligatorio", Validaciones.correo(""))
        assertEquals("Ingresa un correo válido", Validaciones.correo("cliente@nutierra"))
    }

    @Test
    fun `contrasena nueva exige 8 caracteres y un numero`() {
        assertNotNull(Validaciones.contrasenaNueva("corta1"))
        assertNotNull(Validaciones.contrasenaNueva("sinnumeros"))
        assertNull(Validaciones.contrasenaNueva("Semillas2025!"))
    }

    @Test
    fun `confirmacion debe coincidir`() {
        assertEquals(
            "Las contraseñas no coinciden",
            Validaciones.confirmacion("Semillas2025!", "Semillas2026!")
        )
        assertNull(Validaciones.confirmacion("Semillas2025!", "Semillas2025!"))
    }

    @Test
    fun `campo de texto obligatorio`() {
        assertEquals("El nombre es obligatorio", Validaciones.texto("  ", "El nombre"))
        assertNull(Validaciones.texto("Ana", "El nombre"))
    }
}
