package com.example.nutierra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.entity.CarritoItemDetalle
import com.example.nutierra.ui.viewmodel.CarritoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.text.NumberFormat
import java.util.Locale
import java.net.URL

private fun cartPrice(value: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(value)
}

@Composable
fun CartScreen(
    viewModel: CarritoViewModel = viewModel(),
    onBack: () -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val items by viewModel.items.collectAsStateWithLifecycle()
    val procesando by viewModel.procesando.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val totalUnits = items.sumOf { it.cantidad }
    val total = items.sumOf { it.subtotal }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarError()
        }
    }

    Scaffold(
        containerColor = colors.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            .background(colors.primary, CircleShape)
                            .clickable(onClick = onProfileClick),
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
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onCheckout,
                        enabled = items.isNotEmpty() && !procesando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        if (procesando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colors.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                "Continuar a confirmación",
                                style = typography.labelLarge
                            )

                            Spacer(Modifier.width(8.dp))

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = onContinueShopping,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Seguir comprando",
                            style = typography.labelLarge,
                            color = colors.primary
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
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Mi Carrito",
                        style = typography.titleMedium
                    )

                    Surface(
                        color = colors.primaryFixed,
                        shape = CircleShape
                    ) {
                        Text(
                            "$totalUnits artículos",
                            style = typography.labelMedium,
                            color = colors.onPrimaryFixed,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(colors.primary, CircleShape)
                    )

                    Text(
                        "Directo de Cosecha",
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Column {
                        Text(
                            "Existencias verificadas",
                            style = typography.labelLarge,
                            color = colors.primary
                        )

                        Text(
                            "Bodega Central",
                            style = typography.labelMedium,
                            color = colors.secondary
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Tus productos se reservan por 30 minutos " +
                                    "listos para empaque artesanal.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            if (items.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(52.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Tu carrito está vacío",
                        style = typography.titleMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Agrega productos desde el catálogo.",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = onContinueShopping) {
                        Text("Ver catálogo")
                    }
                }
            } else {
                items.forEach { item ->
                    key(item.productoId) {
                        CartItemCard(
                            item = item,
                            onRemove = { viewModel.eliminar(item.productoId) },
                            onDecrease = {
                                viewModel.cambiarCantidad(
                                    item.productoId,
                                    item.cantidad - 1
                                )
                            },
                            onIncrease = {
                                viewModel.cambiarCantidad(
                                    item.productoId,
                                    item.cantidad + 1
                                )
                            }
                        )
                    }
                }
            }

            Surface(
                color = colors.secondaryFixed.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = colors.secondary
                    )

                    Column {
                        Text(
                            "Solicitud de Reserva en Tienda",
                            style = typography.labelLarge,
                            color = colors.onSurface
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Sin pagos en línea requeridos. El equipo de " +
                                    "Nutierra empaquetará tu orden y coordinará " +
                                    "la entrega y pago directo al momento del retiro.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                "Resumen del pedido",
                style = typography.titleMedium
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CartSummaryRow(
                        label = "Subtotal productos ($totalUnits unid.)",
                        value = "${cartPrice(total)} CLP"
                    )

                    HorizontalDivider(
                        color = colors.surfaceContainerHighest
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Estimación de preparación",
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    "Sin costo adicional",
                                    style = typography.labelMedium,
                                    color = colors.primary
                                )
                            }
                        }

                        Text(
                            "$0 CLP",
                            style = typography.labelLarge
                        )
                    }

                    HorizontalDivider(
                        color = colors.surfaceContainerHighest
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total consolidado",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "IVA incluido",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Text(
                            "${cartPrice(total)} CLP",
                            style = typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CarritoItemDetalle,
    onRemove: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceContainer)
                ) {
                    CartRemoteImage(
                        url = item.imagenUri.orEmpty(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${item.categoria} • ${item.peso}",
                        style = typography.labelMedium,
                        color = colors.secondary
                    )

                    Text(
                        item.nombre,
                        style = typography.titleMedium,
                        color = colors.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            cartPrice(item.precio),
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "CLP / unid.",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar ${item.nombre}",
                        tint = colors.outline
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            color = colors.surfaceContainerLowest,
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onDecrease,
                                    enabled = item.cantidad > 1,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Disminuir",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Text(
                                    item.cantidad.toString(),
                                    style = typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(28.dp)
                                )

                                IconButton(
                                    onClick = onIncrease,
                                    enabled = item.cantidad < item.stock,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Aumentar",
                                        tint = colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Máx. ${item.stock} disp.",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "Subtotal",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )

                        Text(
                            "${cartPrice(item.subtotal)} CLP",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CartRemoteImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(url) {
        if (url.isBlank()) return@LaunchedEffect
        bitmap = withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.getInputStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Imagen del producto",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(
                MaterialTheme.colorScheme.surfaceContainer
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}