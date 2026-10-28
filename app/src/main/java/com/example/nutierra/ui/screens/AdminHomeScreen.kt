package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.entity.EstadosPedido
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.ui.components.CampanitaNotificaciones
import com.example.nutierra.ui.viewmodel.PedidoViewModel
import com.example.nutierra.ui.viewmodel.ProductoViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

private fun adminPrice(value: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(value)
}

private fun initialsOf(nombre: String?, apellido: String?): String {
    val a = nombre?.trim()?.firstOrNull()?.uppercaseChar()
    val b = apellido?.trim()?.firstOrNull()?.uppercaseChar()
    return listOfNotNull(a, b).joinToString("").ifEmpty { "?" }
}

private fun inicioDelDia(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

@Composable
fun AdminHomeScreen(
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    noLeidas: Int = 0,
    productoViewModel: ProductoViewModel = viewModel(),
    pedidoViewModel: PedidoViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val productos by productoViewModel.productos.collectAsStateWithLifecycle()
    val pedidos by pedidoViewModel.todosLosPedidos.collectAsStateWithLifecycle()
    val usuario by SesionManager.usuarioActual.collectAsStateWithLifecycle()

    val nombreAdmin = usuario?.nombre?.takeIf { it.isNotBlank() } ?: "Administrador"

    val productosActivos = productos.count { it.stock > 0 }
    val bajoStock = productos.filter { it.stock in 1..5 }
    val criticos = productos.filter { it.stock == 0 }
    val pendientes = pedidos.filter { it.pedido.estado == EstadosPedido.PENDIENTE }

    val inicioHoy = inicioDelDia()
    val pedidosHoy = pedidos.filter { it.pedido.fecha >= inicioHoy }
    val recaudadoHoy = pedidosHoy.sumOf { it.pedido.total }

    val prioridad = pendientes
        .sortedByDescending { it.pedido.fecha }
        .take(2)

    val inventarioCritico = productos
        .filter { it.stock <= 5 }
        .sortedBy { it.stock }
        .take(3)

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
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    "Inicio",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            CampanitaNotificaciones(
                                noLeidas = noLeidas,
                                onClick = onNotificationsClick
                            )
                        }

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
                        selected = true,
                        onClick = {},
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.tertiary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = "Inicio",
                                    tint = MaterialTheme.colorScheme.surface
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Inventario") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onOrdersClick,
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
                                    Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = "Pedidos",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFE4EAE2),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "●  Modo Administrador • En línea",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            color = colors.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "¡Hola, $nombreAdmin!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Control operativo y balance de hoy",
                                fontSize = 14.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory2,
                            value = productosActivos.toString(),
                            description = "Productos activos",
                            status = if (productosActivos == productos.size) "Al día" else "${productos.size - productosActivos} sin stock",
                            background = Color.White
                        )
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Warning,
                            value = bajoStock.size.toString(),
                            description = "Bajo stock físico",
                            status = if (criticos.isEmpty()) "Sin críticos" else "${criticos.size} críticos",
                            background = Color(0xFFFFDCC5)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.PendingActions,
                            value = pendientes.size.toString(),
                            description = "Por validar",
                            status = if (pendientes.isEmpty()) "Todo al día" else "Requiere acción",
                            background = Color(0xFFFFDDB6)
                        )
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.PointOfSale,
                            value = adminPrice(recaudadoHoy),
                            description = "Total recaudado hoy",
                            status = "${pedidosHoy.size} pedidos",
                            background = colors.primary,
                            dark = true
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminSectionTitle("Accesos rápidos")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdminQuickAction(
                            modifier = Modifier.weight(1f),
                            title = "Gestionar Productos",
                            icon = Icons.Default.Sell,
                            onClick = onProductsClick
                        )
                        AdminQuickAction(
                            modifier = Modifier.weight(1f),
                            title = "Actualizar Stock",
                            icon = Icons.Default.SwapVert,
                            onClick = onInventoryClick
                        )
                        AdminQuickAction(
                            modifier = Modifier.weight(1f),
                            title = "Revisar Pedidos",
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            onClick = onOrdersClick
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            AdminSectionTitle("Validación prioritaria")
                            Text(
                                "${pendientes.size} nuevos",
                                color = colors.primary,
                                fontSize = 12.sp
                            )
                        }
                        TextButton(onClick = onOrdersClick) {
                            Text("Ver todos", color = colors.primary)
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    }

                    if (prioridad.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                "No hay pedidos pendientes de validación.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    } else {
                        prioridad.forEach { pedido ->
                            AdminOrderCard(
                                initials = initialsOf(
                                    pedido.clienteNombre,
                                    pedido.clienteApellido
                                ),
                                customer = listOfNotNull(
                                    pedido.clienteNombre,
                                    pedido.clienteApellido
                                ).joinToString(" ").ifBlank { "Cliente #${pedido.pedido.usuarioId}" },
                                detail = "${pedido.pedido.codigo} • ${pedido.pedido.cantidadProductos} variedades",
                                amount = "${adminPrice(pedido.pedido.total)} CLP",
                                onDetail = onOrdersClick,
                                onValidate = onOrdersClick
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            AdminSectionTitle("Inventario crítico")
                            Text(
                                "Bodega central",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onInventoryClick) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Ver inventario"
                            )
                        }
                    }

                    if (inventarioCritico.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                "Inventario al día. No hay productos con stock crítico.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    } else {
                        inventarioCritico.forEach { producto ->
                            AdminStockCard(
                                name = producto.nombre,
                                detail = "${producto.peso} • ${producto.categoria}",
                                stock = if (producto.stock == 0)
                                    "0 unidades disponibles"
                                else
                                    "${producto.stock} unidades restantes",
                                action = if (producto.stock == 0) "Reponer" else "Ajustar",
                                onClick = onInventoryClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun AdminMetric(
    modifier: Modifier,
    icon: ImageVector,
    value: String,
    description: String,
    status: String,
    background: Color,
    dark: Boolean = false
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(16.dp),
        color = background,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (dark) Color.White else colors.primary,
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dark) colors.primaryFixed else colors.primary,
                    maxLines = 2
                )
            }

            Column {
                Text(
                    value,
                    fontSize = if (value.length > 6) 20.sp else 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dark) Color.White else Color(0xFF171D18)
                )
                Text(
                    description,
                    fontSize = 12.sp,
                    color = if (dark) colors.primaryFixed else colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminQuickAction(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .height(116.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colors.surfaceContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = colors.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun AdminOrderCard(
    initials: String,
    customer: String,
    detail: String,
    amount: String,
    onDetail: () -> Unit,
    onValidate: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(colors.surfaceContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
                Column {
                    Text(
                        customer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        detail,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Text(
                "Por Validar",
                color = Color(0xFF855500),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Monto a cobrar",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        amount,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                Row {
                    TextButton(onClick = onDetail) {
                        Text("Detalle", color = colors.primary)
                    }
                    Button(
                        onClick = onValidate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Text("Validar")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStockCard(
    name: String,
    detail: String,
    stock: String,
    action: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    detail,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    stock,
                    fontSize = 12.sp,
                    color = Color(0xFFBA1A1A),
                    fontWeight = FontWeight.SemiBold
                )
            }
            TextButton(onClick = onClick) {
                Icon(
                    if (action == "Reponer") {
                        Icons.Default.AddBox
                    } else {
                        Icons.Default.Edit
                    },
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(action, color = colors.primary)
            }
        }
    }
}