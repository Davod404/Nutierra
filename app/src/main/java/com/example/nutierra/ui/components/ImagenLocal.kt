package com.example.nutierra.ui.components

import android.content.Context
import android.net.Uri
import java.io.File

fun copiarImagenALaApp(context: Context, uri: Uri): String? = try {
    val archivo = File.createTempFile("nutierra_producto_", ".jpg", context.filesDir)
    context.contentResolver.openInputStream(uri)?.use { entrada ->
        archivo.outputStream().use { salida -> entrada.copyTo(salida) }
    }
    Uri.fromFile(archivo).toString()
} catch (_: Exception) {
    null
}
