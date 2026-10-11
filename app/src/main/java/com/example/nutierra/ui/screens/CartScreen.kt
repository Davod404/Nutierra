package com.example.nutierra.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

// PRODUCTOS DE DEMOSTRACIÓN DE FIGMA
private data class CartProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val stock: Int,
    val imageUrl: String,
    val quantity: Int
)

private val initialCart = listOf(
    CartProduct(
        id = 1,
        name = "Almendras Naturales",
        category = "Frutos secos • 250 g",
        price = 4990,
        stock = 24,
        quantity = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBc9bJz_f6QRwpfdVMeL4QvwR5ys3lGHuL3RoiPHFzbxDw_5JYKbmYt7E7hi7GqtWPbQHTSzawH-UhMgQFBbVvNMzMcvdIuu1FIVxlcptlNLXFbuvftr4m3B0SPXtpB7prwIf54Fjt7V5PTIkB-Ochs_ipjCrL64IF1VIbYbMR2IhBgqH5wLXp6TKQx7X89mp6orUd_CChJ_Zz980ZQSv2p0tIxJqJrtuZzqpM6me0"
    ),
    CartProduct(
        id = 2,
        name = "Nueces Mariposa",
        category = "Frutos secos • 250 g",
        price = 5490,
        stock = 18,
        quantity = 1,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBTIR-ryK6GVM-j-2YNJyj9Md88FOk7iFAn3uhWlQPf_FZXu4RIFb7_injcGb3DflksXd4oIAD4cnetPXn4ZO2MOF0sOpkjDLpwC0g5eyjo2IOd_MOC8Vm4oT-FQfgQyWCZAz3wdMvtks2bpfCu9h5ELbzRCWJth1bbQRB1cW_uiuTC_8oACLDlYpYcjPO3TP7FsVnOcXSZKb3ukUKjLXDv0jQNokxRuKE5lgxK0k8"
    ),
    CartProduct(
        id = 4,
        name = "Semillas de Chía",
        category = "Semillas • 250 g",
        price = 2990,
        stock = 40,
        quantity = 1,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAO-DtTVW3VNZTuftOhZLXXCuNveDbiGl9vP59hSQWRfUZcjEv0B6X2mPb6Qrfk7ykhuZn9WBhoZM4ZRawjaZ4R0nzrQm2p_O_9jP6IS2fyuu3xZ92hHE0Q-9feJko8JWpQ1RpXQ74UwvV3QDep7SGIdAwYWz-RHtqZI9vr02mTJjqz0Hnj6MAzgxIXw0ZutTto2Y5z7tfJX4DMQQPriqdXX82kCZ_SJ5vRp8f8gfU"
    )
)

private fun cartPrice(value: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(value)
}

@Composable
fun CartScreen(
    onBack: () -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val products = remember {
        mutableStateListOf<CartProduct>().apply {
            addAll(initialCart)
        }
    }

    val totalUnits = products.sumOf { it.quantity }
    val total = products.sumOf { it.price * it.quantity }

    Scaffold(
        containerColor = colors.surface,
        topBar = {
            Surface(
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
                            .size(34.dp)
                            .background(
                                colors.primaryFixed,
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
                        enabled = products.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
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

            // TÍTULO Y CONTADOR
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

            // EXISTENCIAS VERIFICADAS
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Existencias verificadas",
                                style = typography.labelLarge,
                                color = colors.primary
                            )
                        }

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

            // PRODUCTOS
            if (products.isEmpty()) {
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
                products.forEach { product ->
                    key(product.id) {
                        CartProductCard(
                            product = product,
                            onRemove = {
                                products.removeAll { it.id == product.id }
                            },
                            onDecrease = {
                                updateCartQuantity(
                                    products,
                                    product.id,
                                    product.quantity - 1
                                )
                            },
                            onIncrease = {
                                updateCartQuantity(
                                    products,
                                    product.id,
                                    product.quantity + 1
                                )
                            }
                        )
                    }
                }
            }

            // NOTA DE CONFIANZA
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

            // RESUMEN
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

private fun updateCartQuantity(
    products: SnapshotStateList<CartProduct>,
    id: Int,
    quantity: Int
) {
    val index = products.indexOfFirst { it.id == id }

    if (index == -1) return

    val product = products[index]

    if (quantity in 1..product.stock) {
        products[index] = product.copy(quantity = quantity)
    }
}

@Composable
private fun CartProductCard(
    product: CartProduct,
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
                        url = product.imageUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        product.category,
                        style = typography.labelMedium,
                        color = colors.secondary
                    )

                    Text(
                        product.name,
                        style = typography.titleMedium,
                        color = colors.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            cartPrice(product.price),
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
                        contentDescription = "Eliminar ${product.name}",
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
                                    enabled = product.quantity > 1,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Remove,
                                        contentDescription = "Disminuir",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Text(
                                    product.quantity.toString(),
                                    style = typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(28.dp)
                                )

                                IconButton(
                                    onClick = onIncrease,
                                    enabled = product.quantity < product.stock,
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
                            if (product.id == 1)
                                "Máx. ${product.stock} disp."
                            else
                                "En stock",
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
                            "${cartPrice(product.price * product.quantity)} CLP",
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
        bitmap = withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                connection.getInputStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
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
