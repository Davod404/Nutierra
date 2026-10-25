package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.local.entity.PedidoResumen
import com.example.nutierra.ui.components.formatoFechaPedido
import com.example.nutierra.ui.components.formatoPesos

private data class AdminOrder(
    val id: String,
    val client: String,
    val initials: String,
    val phone: String,
    val date: String,
    val products: String,
    val units: String,
    val delivery: String,
    val total: String,
    val urgent: Boolean = false,
    val note: String = "",
    val status: String,
    val pedidoId: Int = 0
)

private fun PedidoResumen.aAdminOrder(): AdminOrder {
    val nombre = listOfNotNull(clienteNombre, clienteApellido).joinToString(" ").ifBlank { "Cliente" }
    val iniciales = listOfNotNull(clienteNombre, clienteApellido)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }
    return AdminOrder(
        id = pedido.codigo,
        client = nombre,
        initials = iniciales,
        phone = "",
        date = formatoFechaPedido(pedido.fecha),
        products = productos ?: "",
        units = "${pedido.cantidadUnidades} unidades en total",
        delivery = "Retiro en tienda",
        total = formatoPesos(pedido.total),
        urgent = pedido.estado == EstadosPedido.PENDIENTE,
        status = pedido.estado,
        pedidoId = pedido.id
    )
}

@Composable
fun AdminOrdersScreen(
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    pedidos: List<PedidoResumen> = emptyList(),
    onAvanzarEstado: (Int, String) -> Unit = { _, _ -> }
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val orders = remember(pedidos) { pedidos.map { it.aAdminOrder() } }
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var selectedOrder by remember { mutableStateOf<AdminOrder?>(null) }
    var orderToAdvance by remember { mutableStateOf<AdminOrder?>(null) }

    val filters = listOf(
        "Todos",
        "Pendiente",
        "Confirmado",
        "Preparado",
        "Entregado"
    )

    val visibleOrders = orders.filter { order ->
        val matchesSearch = search.isBlank() ||
                order.id.contains(search, ignoreCase = true) ||
                order.client.contains(search, ignoreCase = true)

        val matchesFilter = selectedFilter == "Todos" ||
                order.status == selectedFilter

        matchesSearch && matchesFilter
    }

    fun advanceOrder(order: AdminOrder) {
        val nextStatus = when (order.status) {
            "Pendiente" -> "Confirmado"
            "Confirmado" -> "Preparado"
            "Preparado" -> "Entregado"
            else -> null
        }

        if (nextStatus != null) {
            onAvanzarEstado(order.pedidoId, order.status)
        }
    }

    Scaffold(
        containerColor = colors.background,
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
                    Column {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Nutierra",
                                        style = typography.labelMedium,
                                        color = colors.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Surface(
                                        color = colors.primaryFixed,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            "ADMIN",
                                            modifier = Modifier.padding(
                                                horizontal = 7.dp,
                                                vertical = 3.dp
                                            ),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.onPrimaryFixed
                                        )
                                    }
                                }
                                Text(
                                    "Pedidos",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onSurface
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(colors.primary, CircleShape)
                                .clickable(onClick = onProfileClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Administrador",
                                tint = colors.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = "Inicio",
                                    tint = colors.onSurfaceVariant
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
                        onClick = onProductsClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Inventory2,
                                    contentDescription = "Productos",
                                    tint = colors.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Productos") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onInventoryClick,
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Warehouse,
                                    contentDescription = "Inventario",
                                    tint = colors.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Inventario") },
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
                                        colors.tertiary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = "Pedidos",
                                    tint = colors.onTertiary
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = colors.onSurfaceVariant
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Gestión de pedidos",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurface
                            )

                            Text(
                                "Revisión y avance de solicitudes de compra",
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = colors.surfaceContainer,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 7.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(colors.primary, CircleShape)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Demo",
                                    fontSize = 11.sp,
                                    color = colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("#PED-2024... o nombre")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = { search = "" }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar"
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Filtros"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = colors.surfaceContainerLowest,
                        unfocusedContainerColor = colors.surfaceContainerLowest
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val count = if (filter == "Todos") {
                            orders.size
                        } else {
                            orders.count { it.status == filter }
                        }

                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text("$filter $count")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary,
                                selectedLabelColor = colors.onPrimary
                            )
                        )
                    }
                }
            }

            if (visibleOrders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = colors.onSurfaceVariant
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "No se encontraron pedidos",
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )

                        Text(
                            "Prueba con otra búsqueda o filtro",
                            fontSize = 13.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            items(
                visibleOrders,
                key = { it.id }
            ) { order ->
                AdminOrderCard(
                    order = order,
                    onDetailClick = {
                        selectedOrder = order
                    },
                    onAdvanceClick = {
                        orderToAdvance = order
                    }
                )
            }
        }
    }

    selectedOrder?.let { order ->
        AlertDialog(
            onDismissRequest = {
                selectedOrder = null
            },
            title = {
                Text("Solicitud ${order.id}")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        order.client,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Estado: ${order.status}")
                    Text("Fecha: ${order.date}")

                    if (order.phone.isNotBlank()) {
                        Text("Teléfono: ${order.phone}")
                    }

                    HorizontalDivider()

                    Text(
                        "Productos",
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(order.products)
                    Text(order.units)

                    HorizontalDivider()

                    Text("Entrega: ${order.delivery}")

                    Text(
                        "Total: ${order.total} CLP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            },
            confirmButton = {
                if (order.status != "Entregado") {
                    TextButton(
                        onClick = {
                            selectedOrder = null
                            orderToAdvance = order
                        }
                    ) {
                        Text(
                            if (order.status == "Pendiente") {
                                "Aprobar pedido"
                            } else {
                                "Avanzar estado"
                            },
                            color = colors.primary
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedOrder = null }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    orderToAdvance?.let { order ->
        val nextStatus = when (order.status) {
            "Pendiente" -> "Confirmado"
            "Confirmado" -> "Preparado"
            "Preparado" -> "Entregado"
            else -> order.status
        }

        AlertDialog(
            onDismissRequest = {
                orderToAdvance = null
            },
            title = {
                Text("Actualizar pedido")
            },
            text = {
                Text(
                    "¿Cambiar el pedido ${order.id} de " +
                            "\"${order.status}\" a \"$nextStatus\"?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        advanceOrder(order)
                        orderToAdvance = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        orderToAdvance = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AdminOrderCard(
    order: AdminOrder,
    onDetailClick: () -> Unit,
    onAdvanceClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val statusBackground = when (order.status) {
        "Pendiente" -> colors.secondaryFixed
        "Confirmado" -> colors.primaryFixed
        "Preparado" -> colors.primaryFixed
        else -> colors.surfaceContainer
    }

    val statusTextColor = when (order.status) {
        "Pendiente" -> colors.onSecondaryFixed
        "Confirmado" -> colors.onPrimaryFixed
        "Preparado" -> colors.onPrimaryFixed
        else -> colors.onSurfaceVariant
    }

    val statusText = when (order.status) {
        "Pendiente" -> "Pendiente de Validación"
        else -> order.status
    }

    val actionText = when (order.status) {
        "Pendiente" -> "Revisar y validar"
        "Confirmado" -> "Marcar como preparado"
        "Preparado" -> "Marcar como entregado"
        else -> "Entregado"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    order.id,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = statusBackground,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        statusText,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusTextColor
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
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                        Text(
                            order.initials,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSecondaryFixed
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            order.client,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )

                        if (order.phone.isNotBlank()) {
                            Text(
                                order.phone,
                                fontSize = 12.sp,
                                color = colors.primary
                            )
                        }
                    }

                    Text(
                        order.date,
                        fontSize = 10.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            colors.surfaceContainer,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        tint = colors.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        order.products,
                        fontSize = 13.sp,
                        color = colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        order.units,
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (order.delivery == "Despacho") {
                                Icons.Default.LocalShipping
                            } else {
                                Icons.Default.Storefront
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.tertiary
                        )

                        Spacer(Modifier.width(5.dp))

                        Text(
                            order.delivery,
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "${order.total} CLP",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                if (order.urgent) {
                    Surface(
                        color = colors.tertiaryFixed,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "¡ Urgente",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onTertiaryFixed
                        )
                    }
                }
            }

            HorizontalDivider(
                color = colors.surfaceContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 10.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        "Ver detalle",
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }

                if (order.status != "Entregado") {
                    Button(
                        onClick = onAdvanceClick,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 6.dp,
                            vertical = 10.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Text(
                            actionText,
                            fontSize = 11.sp,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}