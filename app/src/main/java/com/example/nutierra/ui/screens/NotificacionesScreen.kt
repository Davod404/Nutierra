package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.entity.NotificacionEntity
import com.example.nutierra.data.local.entity.TipoNotificacion
import com.example.nutierra.ui.viewmodel.NotificacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onBack: () -> Unit = {},
    onIrAPedidos: () -> Unit = {},
    viewModel: NotificacionViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val notificaciones by viewModel.notificaciones.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = colors.surface,
        topBar = {
            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = colors.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = colors.onSurface
                        )
                    }

                    Text(
                        "Notificaciones",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )

                    if (notificaciones.any { !it.leida }) {
                        TextButton(onClick = { viewModel.marcarTodasComoLeidas() }) {
                            Text(
                                "Marcar todas",
                                color = colors.primary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (notificaciones.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "No tienes notificaciones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Cuando tengas novedades de tus pedidos, aparecerán aquí.",
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notificaciones, key = { it.id }) { notificacion ->
                    NotificacionCard(
                        notificacion = notificacion,
                        onTocar = {
                            viewModel.marcarComoLeida(notificacion.id)
                            if (
                                notificacion.tipo == TipoNotificacion.PEDIDO_CLIENTE ||
                                notificacion.tipo == TipoNotificacion.PEDIDO_ADMIN
                            ) {
                                onIrAPedidos()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificacionCard(
    notificacion: NotificacionEntity,
    onTocar: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val icono = when (notificacion.tipo) {
        TipoNotificacion.PEDIDO_CLIENTE -> Icons.Default.ShoppingBag
        TipoNotificacion.PEDIDO_ADMIN -> Icons.Default.Inventory2
        TipoNotificacion.CARRITO -> Icons.Default.ShoppingCart
        else -> Icons.Default.Notifications
    }

    val fechaFormateada = remember(notificacion.fecha) {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.forLanguageTag("es-CL")
        ).format(Date(notificacion.fecha))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTocar),
        color = if (notificacion.leida)
            colors.surfaceContainerLowest
        else
            colors.surfaceContainer,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (notificacion.leida) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (notificacion.leida)
                            colors.surfaceContainer
                        else
                            colors.primaryFixed,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icono,
                    contentDescription = null,
                    tint = if (notificacion.leida)
                        colors.onSurfaceVariant
                    else
                        colors.onPrimaryFixed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    notificacion.titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = colors.onSurface
                )
                Text(
                    notificacion.mensaje,
                    fontSize = 13.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    fechaFormateada,
                    fontSize = 11.sp,
                    color = colors.outline
                )
            }

            if (!notificacion.leida) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors.primary, CircleShape)
                )
            }
        }
    }
}