package com.example.nutierra.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import com.example.nutierra.data.local.entity.ProductoEntity

private data class InventoryProduct(
    val id: Int,
    val sku: String,
    val name: String,
    val category: String,
    val format: String,
    val price: Int,
    val stock: Int,
    val imagenUri: String?
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

    val products = remember(productos) {
        productos.map {
            InventoryProduct(
                id = it.id,
                sku = it.nombre.take(3).uppercase() + "-" + it.id.toString().padStart(3, '0'),
                name = it.nombre,
                category = it.categoria,
                format = it.peso,
                price = it.precio,
                stock = it.stock,
                imagenUri = it.imagenUri
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
                                        style = MaterialTheme.typography.labelMedium,
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
                                    "Inventario",
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
                                        if (false) colors.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = "Inicio",
                                    tint = if (false) colors.surface
                                    else colors.onSurfaceVariant
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
                                        if (false) colors.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Inventory2,
                                    contentDescription = "Productos",
                                    tint = if (false) colors.surface
                                    else colors.onSurfaceVariant
                                )
                            }
                        },
                        label = { Text("Productos") },
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
                                        if (true) colors.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Warehouse,
                                    contentDescription = "Inventario",
                                    tint = if (true) colors.onTertiary
                                    else colors.onSurfaceVariant
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
                                        if (false) colors.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = "Pedidos",
                                    tint = if (false) colors.surface
                                    else colors.onSurfaceVariant
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
                                        if (false) colors.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Perfil",
                                    tint = if (false) colors.surface
                                    else colors.onSurfaceVariant
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
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                        Text(
                            "Gestión centralizada de stock en bodega",
                            fontSize = 14.sp,
                            color = colors.onSurfaceVariant
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
                        focusedContainerColor = colors.surfaceContainer,
                        unfocusedContainerColor = colors.surfaceContainer,
                        focusedBorderColor = colors.primary,
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
                                    selectedContainerColor = colors.primary,
                                    selectedLabelColor = colors.onPrimary,
                                    containerColor = colors.surfaceContainer
                                ),
                                border = null
                            )
                        }
                }
            }

            item {
                Surface(
                    color = colors.surfaceContainer,
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
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onSurface
                            )
                            Text(
                                "Umbral de alerta: ≤ 5 unidades",
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = colors.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "$criticalCount críticos",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = colors.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (showSuccess) {
                item {
                    Surface(
                        color = colors.primaryFixed,
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
                                tint = colors.onPrimaryFixed
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Stock actualizado correctamente",
                                modifier = Modifier.weight(1f),
                                color = colors.onPrimaryFixed,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { showSuccess = false }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = colors.onPrimaryFixed
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
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            "No se encontraron productos",
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                        Text(
                            "Intenta con otro término de búsqueda o selecciona una categoría diferente.",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
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
                    Text("Cerrar", color = colors.primary)
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
                    Text("Entendido", color = colors.primary)
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
    val colors = MaterialTheme.colorScheme
    val critical = product.stock <= 5

    Surface(
        color = colors.surfaceContainerLowest,
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
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product.imagenUri.isNullOrBlank()) {
                        AsyncImage(
                            model = product.imagenUri,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                product.format,
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        product.category,
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        "SKU: ${product.sku}",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = colors.onSurface
                    )
                    Text(
                        "$${"%,d".format(product.price).replace(',', '.')} CLP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            HorizontalDivider(color = colors.surfaceContainer)

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
                            tint = colors.error,
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
                        color = if (critical) colors.error else colors.onSurfaceVariant
                    )
                }

                TextButton(onClick = onAdjust) {
                    Icon(
                        if (critical) Icons.Default.AddCircle
                        else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = colors.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        when {
                            product.stock == 0 -> "Ingreso"
                            critical -> "Reponer"
                            else -> "Ajustar"
                        },
                        fontSize = 12.sp,
                        color = colors.primary
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
    val colors = MaterialTheme.colorScheme

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
                    color = colors.onSurfaceVariant
                )
                Text(
                    "${product.name} ${product.format}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
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
                    color = colors.onSurfaceVariant
                )

                Surface(
                    color = colors.surfaceContainer,
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
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        IconButton(onClick = { quantity++ }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar",
                                tint = colors.primary
                            )
                        }
                    }
                }

                Text(
                    "Unidades físicas verificadas",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )

                Text(
                    "Motivo del ajuste",
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
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
                                selectedColor = colors.primary
                            )
                        )
                        Text(
                            option,
                            fontSize = 14.sp,
                            color = colors.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(quantity) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
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
                Text("Cancelar", color = colors.onSurfaceVariant)
            }
        },
        containerColor = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(20.dp)
    )
}