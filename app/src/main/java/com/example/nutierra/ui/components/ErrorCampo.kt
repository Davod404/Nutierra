package com.example.nutierra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Devuelve el contenido para "supportingText" de un OutlinedTextField:
 * un ícono de error + el mensaje. Si no hay error, no muestra nada.
 */
fun textoDeError(error: String?): (@Composable () -> Unit)? =
    if (error == null) {
        null
    } else {
        { FilaError(error) }
    }

@Composable
fun FilaError(mensaje: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(14.dp)
        )
        Text(
            mensaje,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
