package com.example.nutierra.ui.screens

import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.local.entity.PedidoResumen
import com.example.nutierra.ui.components.formatoFechaPedido
import com.example.nutierra.ui.components.formatoPesos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ordersGreen = Color(0xFF13522F)
private val ordersBackground = Color(0xFFF5FBF3)
private val ordersSoft = Color(0xFFEAF0E8)
private val ordersMuted = Color(0xFF404941)
private val ordersAmber = Color(0xFF855500)

private data class ClientOrder(
    val id: String,
    val date: String,
    val status: String,
    val products: String,
    val summary: String,
    val total: String,
    val progress: Int,
    val progressMessage: String
)

// Convierte un pedido guardado en SQLite al formato de esta pantalla
private fun PedidoResumen.aClientOrder(): ClientOrder {
    val estado = pedido.estado
    return ClientOrder(
        id = pedido.codigo,
        date = formatoFechaPedido(pedido.fecha),
        status = if (estado == EstadosPedido.PENDIENTE) "Pendiente de Validación" else estado,
        products = productos ?: "",
        summary = "${pedido.cantidadUnidades} artículos • ${pedido.cantidadProductos} productos",
        total = formatoPesos(pedido.total),
        progress = EstadosPedido.paso(estado),
        progressMessage = when (estado) {
            EstadosPedido.PENDIENTE -> "Validando el pedido y las existencias en bodega"
            EstadosPedido.CONFIRMADO -> "Pedido confirmado, pronto comenzará su preparación"
            EstadosPedido.PREPARADO -> "Listo para entrega / retiro en tienda"
            else -> "Pedido entregado correctamente"
        }
    )
}

@Composable
fun OrdersScreen(
    onHomeClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    pedidos: List<PedidoResumen> = emptyList()
) {
    // Pedidos reales del cliente en sesión (SQLite)
    val demoClientOrders = remember(pedidos) { pedidos.map { it.aClientOrder() } }
    val entregados = demoClientOrders.count { it.status == "Entregado" }

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var selectedFilter by remember { mutableStateOf("Todos") }
    var selectedOrder by remember { mutableStateOf<ClientOrder?>(null) }
    var showReceipt by remember { mutableStateOf<ClientOrder?>(null) }
    var showRepeat by remember { mutableStateOf(false) }
    var refreshMessage by remember { mutableStateOf(false) }

    val filteredOrders = when (selectedFilter) {
        "En proceso" -> demoClientOrders.filter {
            it.status != "Entregado"
        }
        "Entregados" -> demoClientOrders.filter {
            it.status == "Entregado"
        }
        else -> demoClientOrders
    }

    Scaffold(
        containerColor = ordersBackground,
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
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                        Column {
                            Text(
                                text = "Nutierra",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                            Text(
                                text = "Pedidos",
                                style = typography.titleMedium,
                                color = colors.onSurface
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ordersGreen, CircleShape)
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                shadowElevation = 2.dp
            ) {
                NavigationBar(
                    containerColor = colors.surface,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onHomeClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Inicio",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Inicio") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onCatalogClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = "Catálogo",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Catálogo") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onMapClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = "Mapa",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Mapa") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onScanClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = "Escanear",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Escanear") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (true) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = "Pedidos",
                                    tint = if (true) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Pedidos") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onProfileClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Perfil") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Mis pedidos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Historial de tus solicitudes de compra",
                            fontSize = 13.sp,
                            color = ordersMuted
                        )
                    }

                    IconButton(
                        onClick = { refreshMessage = true },
                        modifier = Modifier
                            .background(
                                ordersSoft,
                                RoundedCornerShape(50.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Actualizar estado",
                            tint = ordersGreen
                        )
                    }
                }
            }

            if (refreshMessage) {
                item {
                    Surface(
                        color = ordersSoft,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Los estados se actualizan automáticamente cuando la tienda avanza tu pedido.",
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp
                            )
                            IconButton(
                                onClick = { refreshMessage = false }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar"
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Todos" to "Todos (${demoClientOrders.size})",
                        "En proceso" to "En proceso (${demoClientOrders.size - entregados})",
                        "Entregados" to "Entregados ($entregados)"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = selectedFilter == value,
                            onClick = { selectedFilter = value },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ordersGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                item {
                    Text(
                        if (demoClientOrders.isEmpty()) {
                            "Aún no tienes pedidos. Agrega productos al carrito y confirma tu compra."
                        } else {
                            "No hay pedidos en esta categoría."
                        },
                        fontSize = 14.sp,
                        color = ordersMuted,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(filteredOrders, key = { it.id }) { order ->
                ClientOrderCard(
                    order = order,
                    onDetailClick = { selectedOrder = order },
                    onReceiptClick = { showReceipt = order },
                    onRepeatClick = { showRepeat = true }
                )
            }

            item {
                Surface(
                    color = ordersSoft,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = ordersGreen
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Garantía de frescura artesanal",
                                fontWeight = FontWeight.SemiBold,
                                color = ordersGreen
                            )
                        }

                        Text(
                            "Recuerda que todas las solicitudes son validadas " +
                                    "por la tienda antes de su preparación física para " +
                                    "asegurar lotes frescos recién envasados.",
                            fontSize = 13.sp,
                            color = ordersMuted
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onCatalogClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ordersGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingBag,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Hacer un nuevo pedido")
                }
            }
        }
    }

    selectedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrder = null },
            title = { Text("Detalle de solicitud") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(order.id, fontWeight = FontWeight.Bold)
                    Text("Fecha: ${order.date}")
                    Text("Estado: ${order.status}")
                    HorizontalDivider()
                    Text(order.products)
                    HorizontalDivider()
                    Text(
                        "Total: ${order.total} CLP",
                        fontWeight = FontWeight.Bold,
                        color = ordersGreen
                    )
                    Text(
                        order.progressMessage,
                        fontSize = 12.sp,
                        color = ordersMuted
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedOrder = null }) {
                    Text("Cerrar", color = ordersGreen)
                }
            }
        )
    }

    showReceipt?.let { order ->
        AlertDialog(
            onDismissRequest = { showReceipt = null },
            title = { Text("Boleta") },
            text = {
                Text(
                    "Pedido ${order.id}\n" +
                            "Total: ${order.total} CLP\n\n" +
                            "La descarga de la boleta estará disponible " +
                            "cuando se conecte el sistema de pedidos."
                )
            },
            confirmButton = {
                TextButton(onClick = { showReceipt = null }) {
                    Text("Cerrar", color = ordersGreen)
                }
            }
        )
    }

    if (showRepeat) {
        AlertDialog(
            onDismissRequest = { showRepeat = false },
            title = { Text("Repetir pedido") },
            text = {
                Text(
                    "Esta función estará disponible cuando " +
                            "los pedidos estén conectados al carrito."
                )
            },
            confirmButton = {
                TextButton(onClick = { showRepeat = false }) {
                    Text("Entendido", color = ordersGreen)
                }
            }
        )
    }
}

@Composable
private fun ClientOrderCard(
    order: ClientOrder,
    onDetailClick: () -> Unit,
    onReceiptClick: () -> Unit,
    onRepeatClick: () -> Unit
) {
    val delivered = order.status == "Entregado"
    val pending = order.progress == 1

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        order.id,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        order.date,
                        fontSize = 12.sp,
                        color = ordersMuted
                    )
                }

                Surface(
                    color = when {
                        pending -> Color(0xFFFFDDB6)
                        delivered -> Color(0xFFB1F1C1)
                        else -> Color(0xFFB1F1C1)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        order.status,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pending) ordersAmber else ordersGreen
                    )
                }
            }

            Surface(
                color = Color(0xFFEFF5ED),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color(0xFFDEE4DC),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = ordersGreen
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column {
                        Text(
                            order.products,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            order.summary,
                            fontSize = 11.sp,
                            color = ordersMuted
                        )
                    }
                }
            }

            if (!delivered) {
                val stages = listOf(
                    "Pendiente",
                    "Confirmado",
                    "Preparado",
                    "Entregado"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        stages.forEachIndexed { index, stage ->
                            Text(
                                "${index + 1}. $stage",
                                fontSize = 9.sp,
                                fontWeight = if (index + 1 == order.progress) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (index + 1 <= order.progress) {
                                    ordersGreen
                                } else {
                                    ordersMuted
                                }
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { order.progress / 4f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (pending) {
                            Color(0xFFFFB959)
                        } else {
                            ordersGreen
                        },
                        trackColor = ordersSoft
                    )

                    Text(
                        order.progressMessage,
                        fontSize = 11.sp,
                        color = if (pending) ordersAmber else ordersGreen
                    )
                }
            }

            HorizontalDivider(color = ordersSoft)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (delivered) "Total final" else "Total orden",
                        fontSize = 11.sp,
                        color = ordersMuted
                    )
                    Text(
                        "${order.total} CLP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ordersGreen
                    )
                }

                if (!delivered) {
                    TextButton(onClick = onDetailClick) {
                        Text(
                            "Ver detalle",
                            fontSize = 12.sp,
                            color = ordersGreen
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ordersGreen,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            if (delivered) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReceiptClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Boleta")
                    }

                    Button(
                        onClick = onRepeatClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ordersGreen
                        )
                    ) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Repetir")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.Order(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                icon,
                contentDescription = title
            )
        },
        label = {
            Text(title)
        }
    )
}