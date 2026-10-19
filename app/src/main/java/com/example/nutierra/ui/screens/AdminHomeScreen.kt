package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val adminGreen = Color(0xFF13522F)
private val adminBackground = Color(0xFFF5FBF3)
private val adminSurface = Color.White
private val adminMuted = Color(0xFF404941)
private val adminSoft = Color(0xFFEAF0E8)
private val adminMint = Color(0xFFB1F1C1)

@Composable
fun AdminHomeScreen(
    onProductsClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = adminBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(adminBackground)
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "OYL FRUTOS SECOS",
                            color = adminGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = adminMint,
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = adminMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(adminGreen, CircleShape)
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Administrador",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = adminSurface,
                tonalElevation = 3.dp
            ) {
                val items = listOf(
                    Triple("Inicio", Icons.Default.Dashboard, 0),
                    Triple("Productos", Icons.Default.Inventory2, 1),
                    Triple("Inventario", Icons.Default.Warehouse, 2),
                    Triple("Pedidos", Icons.Default.Assignment, 3)
                )

                items.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = index == 0,
                        onClick = {
                            when (index) {
                                1 -> onProductsClick()
                                2 -> onInventoryClick()
                                3 -> onOrdersClick()
                            }
                        },
                        icon = {
                            Icon(icon, contentDescription = label)
                        },
                        label = {
                            Text(label, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = adminGreen,
                            selectedTextColor = adminGreen,
                            indicatorColor = adminSoft
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
                            color = adminGreen,
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
                                "¡Hola, Rodrigo!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Control operativo y balance de hoy",
                                fontSize = 14.sp,
                                color = adminMuted
                            )
                        }
                        Icon(
                            Icons.Default.Storefront,
                            contentDescription = null,
                            tint = adminGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory2,
                            value = "8",
                            description = "Productos activos",
                            status = "Al día",
                            background = Color.White
                        )
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Warning,
                            value = "≤ 5",
                            description = "Bajo stock físico",
                            status = "2 críticos",
                            background = Color(0xFFFFDCC5)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.PendingActions,
                            value = "4",
                            description = "Por validar",
                            status = "Requiere acción",
                            background = Color(0xFFFFDDB6)
                        )
                        AdminMetric(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.PointOfSale,
                            value = "$118.420",
                            description = "Total recaudado hoy",
                            status = "9 pedidos",
                            background = adminGreen,
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
                                "4 nuevos",
                                color = adminGreen,
                                fontSize = 12.sp
                            )
                        }
                        TextButton(onClick = onOrdersClick) {
                            Text("Ver todos", color = adminGreen)
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    }

                    AdminOrderCard(
                        initials = "FM",
                        customer = "Francisca Morales",
                        detail = "#PED-2024-00129 • 4 variedades",
                        amount = "$18.460 CLP",
                        onDetail = onOrdersClick,
                        onValidate = onOrdersClick
                    )

                    AdminOrderCard(
                        initials = "CM",
                        customer = "Carlos Mendoza",
                        detail = "#PED-2024-00128 • 3 variedades",
                        amount = "$12.970 CLP",
                        onDetail = onOrdersClick,
                        onValidate = onOrdersClick
                    )
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
                                color = adminMuted
                            )
                        }
                        IconButton(onClick = onInventoryClick) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Ver inventario"
                            )
                        }
                    }

                    AdminStockCard(
                        name = "Cranberries Rojos",
                        detail = "Bolsa 250 g • Tostaduría",
                        stock = "0 unidades disponibles",
                        action = "Reponer",
                        onClick = onInventoryClick
                    )
                    AdminStockCard(
                        name = "Damascos Turcos",
                        detail = "Bolsa 250 g • Importado",
                        stock = "4 unidades restantes",
                        action = "Ajustar",
                        onClick = onInventoryClick
                    )
                    AdminStockCard(
                        name = "Linaza Dorada",
                        detail = "Granel 500 g • Cosecha Sur",
                        stock = "5 unidades restantes",
                        action = "Ajustar",
                        onClick = onInventoryClick
                    )
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
                    tint = if (dark) Color.White else adminGreen,
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dark) adminMint else adminGreen,
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
                    color = if (dark) adminMint else adminMuted
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
                    .background(adminSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = adminGreen
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
                        .background(adminSoft, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        initials,
                        fontWeight = FontWeight.Bold,
                        color = adminGreen
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
                        color = adminMuted
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
                        color = adminMuted
                    )
                    Text(
                        amount,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                Row {
                    TextButton(onClick = onDetail) {
                        Text("Detalle", color = adminGreen)
                    }
                    Button(
                        onClick = onValidate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = adminGreen
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
                    color = adminMuted
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
                Text(action, color = adminGreen)
            }
        }
    }
}
