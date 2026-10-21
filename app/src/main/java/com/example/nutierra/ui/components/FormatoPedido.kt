package com.example.nutierra.ui.components

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val localeChile = Locale.forLanguageTag("es-CL")

/** $12.970 */
fun formatoPesos(valor: Int): String =
    "$" + NumberFormat.getIntegerInstance(localeChile).format(valor)

/** "Hoy, 14:30 hrs", "Ayer, 18:15 hrs" o "12/10/2026, 09:05 hrs". */
fun formatoFechaPedido(millis: Long): String {
    val fecha = Calendar.getInstance().apply { timeInMillis = millis }
    val hoy = Calendar.getInstance()
    val ayer = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val hora = SimpleDateFormat("HH:mm", localeChile).format(Date(millis))

    fun mismoDia(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    return when {
        mismoDia(fecha, hoy) -> "Hoy, $hora hrs"
        mismoDia(fecha, ayer) -> "Ayer, $hora hrs"
        else -> SimpleDateFormat("dd/MM/yyyy", localeChile).format(Date(millis)) + ", $hora hrs"
    }
}
