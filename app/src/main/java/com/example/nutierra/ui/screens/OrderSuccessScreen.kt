package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.Date
import com.example.nutierra.data.local.entity.CarritoItemDetalle
import kotlin.time.Duration.Companion.milliseconds

private fun successPrice(value: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(value)
}

@Composable
fun OrderSuccessScreen(
    orderCode: String = "",
    total: Int = 0,
    productCount: Int = 0,
    unitCount: Int = 0,
    items: List<CarritoItemDetalle> = emptyList(),
    onBack: () -> Unit = {},
    onViewOrder: () -> Unit = {},
    onCatalogClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    var codeCopied by remember { mutableStateOf(false) }

    val orderDate = remember {
        SimpleDateFormat(
            "dd/MM/yyyy, HH:mm 'hrs'",
            Locale.forLanguageTag("es-CL")
        ).format(Date())
    }

    LaunchedEffect(codeCopied) {
        if (codeCopied) {
            kotlinx.coroutines.delay(2000L.milliseconds)
            codeCopied = false
        }
    }

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
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = "Nutierra",
                            tint = colors.onPrimary
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Nutierra",
                        style = typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = colors.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .background(
                                colors.primaryFixed.copy(alpha = 0.4f),
                                CircleShape
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = "Solicitud enviada",
                            tint = colors.onPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = colors.secondaryContainer,
                        shape = CircleShape,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = colors.onSecondaryContainer,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                "Nutierra Cosecha",
                                style = typography.labelMedium,
                                color = colors.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "¡Solicitud enviada con éxito!",
                    style = typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "Hemos recibido tu solicitud de compra. " +
                            "Nuestro equipo revisará las existencias " +
                            "en bodega para confirmar tu pedido a la brevedad.",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Código de Solicitud",
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                orderCode.ifBlank { "—" },
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )

                            if (orderCode.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(
                                            Context.CLIPBOARD_SERVICE
                                        ) as ClipboardManager

                                        // Se usa setPrimaryClip() en lugar de primaryClip porque la propiedad
                                        // sintética de Kotlin no soporta el setter (getter nullable, setter no).
                                        clipboard.setPrimaryClip(
                                            ClipData.newPlainText(
                                                "Código de pedido",
                                                orderCode
                                            )
                                        )

                                        codeCopied = true
                                    }
                                ) {
                                    Icon(
                                        if (codeCopied)
                                            Icons.Default.Check
                                        else
                                            Icons.Default.ContentCopy,
                                        contentDescription = "Copiar código",
                                        tint = colors.primary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        color = colors.surfaceContainer,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = colors.tertiary,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                "Pendiente de Validación",
                                style = typography.labelLarge,
                                color = colors.tertiary
                            )
                        }
                    }

                    HorizontalDivider(
                        color = colors.surfaceContainer
                    )

                    SuccessDetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Fecha y hora",
                        value = orderDate
                    )

                    SuccessDetailRow(
                        icon = Icons.Default.Inventory2,
                        label = "Productos solicitados",
                        value = "$productCount productos ($unitCount unid.)"
                    )

                    SuccessDetailRow(
                        icon = Icons.Default.Payments,
                        label = "Monto estimado total",
                        value = "${successPrice(total)} CLP",
                        isHighlighted = true
                    )

                    Surface(
                        color = colors.surfaceContainerLow,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        colors.primaryFixed,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    "Tiempo estimado de revisión",
                                    style = typography.labelLarge
                                )

                                Text(
                                    "~15 a 30 min en horario hábil",
                                    style = typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Ítems de tu pedido",
                            style = typography.titleMedium
                        )

                        Text(
                            "Cosecha nacional",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    if (items.isEmpty()) {
                        Text(
                            "No hay ítems para mostrar.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    } else {
                        items.forEachIndexed { index, item ->
                            SuccessProductRow(
                                name = item.nombre,
                                detail = "${item.peso} · x${item.cantidad}"
                            )

                            if (index < items.lastIndex) {
                                HorizontalDivider(
                                    color = colors.surfaceContainer
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                colors.secondaryFixed,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = colors.onSecondaryFixed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "¿Tienes preguntas sobre tu despacho?",
                            style = typography.labelLarge
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Cualquier consulta sobre tu solicitud " +
                                    "puedes coordinarla directamente con " +
                                    "nuestro equipo indicando tu código de pedido.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            Button(
                onClick = onViewOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Ver estado de mi pedido",
                    style = typography.labelLarge
                )
            }

            Button(
                onClick = onCatalogClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceContainerLow,
                    contentColor = colors.primary
                ),
                elevation = null
            ) {
                Icon(
                    Icons.Default.Storefront,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Volver al catálogo",
                    style = typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SuccessDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = colors.outline,
            modifier = Modifier.size(18.dp)
        )

        Text(
            label,
            style = typography.bodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            value,
            style = if (isHighlighted)
                typography.titleMedium
            else
                typography.labelMedium,
            fontWeight = if (isHighlighted)
                FontWeight.Bold
            else
                FontWeight.Medium,
            color = if (isHighlighted)
                colors.primary
            else
                colors.onSurface,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SuccessProductRow(
    name: String,
    detail: String
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    colors.surfaceContainerLow,
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                tint = colors.primary
            )
        }

        Column {
            Text(
                name,
                style = typography.labelLarge
            )

            Text(
                detail,
                style = typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }
    }
}