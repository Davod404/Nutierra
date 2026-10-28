package com.example.nutierra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CampanitaNotificaciones(
    noLeidas: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    colors.surfaceContainer,
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = colors.primary
            )
        }

        if (noLeidas > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 5.dp, y = (-5).dp)
                    .size(20.dp)
                    .background(colors.error, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (noLeidas > 9) "9+" else noLeidas.toString(),
                    color = colors.onError,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}