package com.example.nutierra.ui.components

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Copia una imagen elegida en la galería (content://) al almacenamiento interno de la app
 * y devuelve su dirección file://. Así la imagen se puede mostrar siempre, incluso después
 * de cerrar la app (el permiso sobre content:// es temporal).
 */
fun copiarImagenALaApp(context: Context, uri: Uri): String? = try {
    val archivo = File.createTempFile("nutierra_producto_", ".jpg", context.filesDir)
    context.contentResolver.openInputStream(uri)?.use { entrada ->
        archivo.outputStream().use { salida -> entrada.copyTo(salida) }
    }
    Uri.fromFile(archivo).toString()
} catch (e: Exception) {
    null
}
