package com.example.nutierra.ui.screens

import com.example.nutierra.data.local.entity.ProductoEntity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutierra.ui.theme.Surface

private val inventoryGreen = Color(0xFF13522F)
private val inventoryBackground = Color(0xFFF5FBF3)
private val inventorySoft = Color(0xFFEAF0E8)
private val inventoryMuted = Color(0xFF404941)

private val inventoryMint = Color(0xFFB1F1C1)
private val inventoryRed = Color(0xFFBA1A1A)

private data class InventoryProduct(
    val id: Int,
    val sku: String,
    val name: String,
    val category: String,
    val format: String,
    val price: Int,
    val stock: Int
)

@Composable
fun AdminInventoryScreen(
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    productos: List<ProductoEntity> = emptyList(),
    onActualizarStock: (Int, Int) -> Unit = { _, _ -> },
    onProfileClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val products = remember(productos) {
        productos.map {
            InventoryProduct(
                id = it.id,
                sku = it.nombre.take(3).uppercase() + "-" + it.id.toString().padStart(3, '0'),
                name = it.nombre,
                category = it.categoria,
                format = it.peso,
                price = it.precio,
                stock = it.stock
            )
        }
    }

    val categoryCounts = remember(productos) {
        productos
            .groupingBy { it.categoria }
            .eachCount()
            .toSortedMap()
    }

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todos") }
    var selectedProduct by remember { mutableStateOf<InventoryProduct?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    var showReport by remember { mutableStateOf(false) }
    var showBulkAdjust by remember { mutableStateOf(false) }

    val filtered = products.filter {
        (category == "Todos" || it.category == category) &&
                (it.name.contains(search, ignoreCase = true) ||
                        it.sku.contains(search, ignoreCase = true))
    }

    val criticalCount = products.count { it.stock <= 5 }

    Scaffold(
        containerColor = inventoryBackground,
        topBar = {
            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = colors.surface,
                shadowElevation = 2.dp
            ){
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
                                        color = inventoryMint,
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
                                    "Inventario",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(inventoryGreen, CircleShape)
                                .clickable(onClick = onProfileClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Administrador",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 2.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProductsClick,
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Productos") },
                    label = { Text("Productos") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Warehouse, contentDescription = "Inventario") },
                    label = { Text("Inventario") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Pedidos") },
                    label = { Text("Perfil") }
                )
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
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Control de inventario",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Gestión centralizada de stock en bodega",
                            fontSize = 14.sp,
                            color = inventoryMuted
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar por nombre o SKU...")
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
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inventorySoft,
                        unfocusedContainerColor = inventorySoft,
                        focusedBorderColor = inventoryGreen,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (
                        listOf("Todos" to productos.size) +
                            categoryCounts.toList()
                    ).forEach { (value, count) ->
                        FilterChip(
                            selected = category == value,
                            onClick = { category = value },
                            label = {
                                Text("$value ($count)", fontSize = 12.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = inventoryGreen,
                                selectedLabelColor = Color.White,
                                containerColor = inventorySoft
                            ),
                            border = null
                        )
                    }
                }
            }

            item {
                Surface(
                    color = inventorySoft,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Datos de inventario de demostración",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Umbral de alerta: ≤ 5 unidades",
                                fontSize = 11.sp,
                                color = inventoryMuted
                            )
                        }
                        Surface(
                            color = Color(0xFFFFDAD6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "$criticalCount críticos",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = inventoryRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (showSuccess) {
                item {
                    Surface(
                        color = Color(0xFFB1F1C1),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = inventoryGreen
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Stock actualizado correctamente",
                                modifier = Modifier.weight(1f),
                                color = inventoryGreen,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { showSuccess = false }
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

            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = inventoryMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            "No se encontraron productos",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Intenta con otro término de búsqueda o selecciona una categoría diferente.",
                            fontSize = 12.sp,
                            color = inventoryMuted
                        )
                    }
                }
            } else {
                items(filtered, key = { it.id }) { product ->
                    InventoryProductCard(
                        product = product,
                        onAdjust = { selectedProduct = product }
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showReport = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Summarize,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Reporte stock", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    selectedProduct?.let { product ->
        InventoryAdjustDialog(
            product = product,
            onDismiss = { selectedProduct = null },
            onSave = { newStock ->
                onActualizarStock(product.id, newStock)
                selectedProduct = null
                showSuccess = true
            }
        )
    }

    if (showReport) {
        AlertDialog(
            onDismissRequest = { showReport = false },
            title = { Text("Reporte de stock") },
            text = {
                Text(
                    "Productos registrados: ${products.size}\n" +
                            "Productos críticos: $criticalCount\n" +
                            "Unidades totales: ${products.sumOf { it.stock }}\n\n" +
                            "Este es un resumen local de demostración."
                )
            },
            confirmButton = {
                TextButton(onClick = { showReport = false }) {
                    Text("Cerrar", color = inventoryGreen)
                }
            }
        )
    }

    if (showBulkAdjust) {
        AlertDialog(
            onDismissRequest = { showBulkAdjust = false },
            title = { Text("Ajuste masivo") },
            text = {
                Text(
                    "La operación masiva requiere seleccionar productos " +
                            "y definir sus cantidades. Por ahora, utiliza Ajustar, " +
                            "Reponer o Ingreso en cada producto."
                )
            },
            confirmButton = {
                TextButton(onClick = { showBulkAdjust = false }) {
                    Text("Entendido", color = inventoryGreen)
                }
            }
        )
    }
}

@Composable
private fun InventoryProductCard(
    product: InventoryProduct,
    onAdjust: () -> Unit
) {
    val critical = product.stock <= 5

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            inventorySoft,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = inventoryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            product.format,
                            fontSize = 11.sp,
                            color = inventoryMuted
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        product.category,
                        fontSize = 11.sp,
                        color = inventoryMuted
                    )
                    Text(
                        "SKU: ${product.sku}",
                        fontSize = 11.sp,
                        color = inventoryMuted
                    )
                    Text(
                        product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "$${"%,d".format(product.price).replace(',', '.')} CLP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = inventoryGreen
                    )
                }
            }

            HorizontalDivider(color = inventorySoft)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (critical) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = inventoryRed,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                    }

                    Text(
                        when {
                            product.stock == 0 ->
                                "0 unidades (Sin existencias)"
                            critical ->
                                "${product.stock} unidades (Límite crítico)"
                            else ->
                                "${product.stock} unidades disponibles"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (critical) inventoryRed else inventoryMuted
                    )
                }

                TextButton(onClick = onAdjust) {
                    Icon(
                        if (critical) Icons.Default.AddCircle
                        else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = inventoryGreen
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        when {
                            product.stock == 0 -> "Ingreso"
                            critical -> "Reponer"
                            else -> "Ajustar"
                        },
                        fontSize = 12.sp,
                        color = inventoryGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryAdjustDialog(
    product: InventoryProduct,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var quantity by remember(product.id) {
        mutableIntStateOf(product.stock)
    }
    var reason by remember(product.id) {
        mutableStateOf("Conteo manual")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    "SKU: ${product.sku}",
                    fontSize = 12.sp,
                    color = inventoryMuted
                )
                Text(
                    "${product.name} ${product.format}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Existencias actuales en bodega: ${product.stock}",
                    fontSize = 13.sp,
                    color = inventoryMuted
                )

                Surface(
                    color = inventorySoft,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (quantity > 0) quantity--
                            }
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Disminuir"
                            )
                        }

                        Text(
                            quantity.toString(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { quantity++ }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar",
                                tint = inventoryGreen
                            )
                        }
                    }
                }

                Text(
                    "Unidades físicas verificadas",
                    fontSize = 12.sp,
                    color = inventoryMuted
                )

                Text(
                    "Motivo del ajuste",
                    fontWeight = FontWeight.SemiBold
                )

                listOf(
                    "Recepción",
                    "Merma / Daño",
                    "Conteo manual"
                ).forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { reason = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = reason == option,
                            onClick = { reason = option },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = inventoryGreen
                            )
                        )
                        Text(option, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(quantity) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = inventoryGreen
                )
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Guardar stock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = inventoryMuted)
            }
        },
        containerColor = inventoryBackground,
        shape = RoundedCornerShape(20.dp)
    )
}
