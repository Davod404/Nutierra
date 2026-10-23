package com.example.nutierra.ui.screens

import com.example.nutierra.data.local.entity.ProductoEntity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel

private val productsGreen = Color(0xFF13522F)
private val productsBackground = Color(0xFFF5FBF3)
private val productsSurface = Color(0xFFFFFFFF)
private val productsMuted = Color(0xFF404941)
private val productsSoft = Color(0xFFEAF0E8)

private val productsMint = Color(0xFFB1F1C1)
private val productsError = Color(0xFFBA1A1A)

private data class AdminProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val stock: Int
)

@Composable
fun AdminProductsScreen(
    onHomeClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Int) -> Unit,
    onDeleteProductClick: (Int) -> Unit,
    productos: List<ProductoEntity> = emptyList(),
    onProfileClick: () -> Unit = {}
) {

    var productoAEliminar by remember { mutableStateOf<AdminProduct?>(null) }

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val adminProductList = remember(productos) {
        productos.map {
            AdminProduct(
                id = it.id,
                name = it.nombre,
                category = it.categoria,
                price = "$" + java.text.NumberFormat
                    .getIntegerInstance(java.util.Locale.forLanguageTag("es-CL"))
                    .format(it.precio),
                stock = it.stock
            )
        }
    }

    val categoryCounts = remember(adminProductList) {
        adminProductList
            .groupingBy { it.category }
            .eachCount()
            .toSortedMap()
    }

    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var showFilters by remember { mutableStateOf(false) }

    val filteredProducts = adminProductList.filter { product ->
        val matchesSearch = product.name.contains(search, ignoreCase = true)
        val matchesCategory =
            selectedCategory == "Todos" || product.category == selectedCategory

        matchesSearch && matchesCategory
    }

    Scaffold(
        containerColor = productsBackground,
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
                                        color = productsMint,
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
                                    "Gestión De Productos",
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
                                .background(productsGreen, CircleShape)
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
                                    Icons.Default.Dashboard,
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
                                    Icons.Default.Inventory2,
                                    contentDescription = "Productos",
                                    tint = if (true) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
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
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Warehouse,
                                    contentDescription = "Inventario",
                                    tint = if (false) MaterialTheme.colorScheme.surface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
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
                                        if (false) MaterialTheme.colorScheme.tertiary
                                        else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Assignment,
                                    contentDescription = "Pedidos",
                                    tint = if (false) MaterialTheme.colorScheme.surface
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
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Gestión de productos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Surface(
                            color = productsMint,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "${adminProductList.size} ítems",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp
                                ),
                                fontSize = 12.sp,
                                color = productsGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        "Catálogo centralizado para ventas y control de stock",
                        fontSize = 14.sp,
                        color = productsMuted
                    )
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Buscar por nombre comercial o SKU...",
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (search.isNotEmpty()) {
                                IconButton(onClick = { search = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = productsSoft,
                            unfocusedContainerColor = productsSoft,
                            focusedBorderColor = productsGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = { showFilters = !showFilters },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                productsSoft,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Filtros avanzados"
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (
                        listOf("Todos" to adminProductList.size) +
                            categoryCounts.toList()
                    ).forEach { (category, count) ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    "$category ($count)",
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = if (selectedCategory == category) {
                                {
                                    Icon(
                                        Icons.Default.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                null
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = productsGreen,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = productsSoft
                            ),
                            border = null
                        )
                    }
                }
            }

            if (showFilters) {
                item {
                    Surface(
                        color = productsSoft,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Filtros",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Selecciona una categoría para filtrar los productos.",
                                fontSize = 13.sp,
                                color = productsMuted
                            )
                            TextButton(
                                onClick = {
                                    search = ""
                                    selectedCategory = "Todos"
                                    showFilters = false
                                }
                            ) {
                                Text(
                                    "Restablecer filtros",
                                    color = productsGreen
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onAddProductClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = productsGreen
                    )
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Agregar producto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = productsMuted
                        )
                        Text(
                            "No se encontraron productos",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Prueba con otros términos de búsqueda o cambia la categoría activa.",
                            fontSize = 13.sp,
                            color = productsMuted
                        )
                        TextButton(
                            onClick = {
                                search = ""
                                selectedCategory = "Todos"
                            }
                        ) {
                            Text("Restablecer filtros", color = productsGreen)
                        }
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    AdminProductRow(
                        product = product,
                        onEditClick = { onEditProductClick(product.id) },
                        onDeleteClick = { productoAEliminar = product }
                    )
                }
            }

            item {
                Surface(
                    color = productsSoft,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            tint = productsGreen
                        )
                        Column {
                            Text(
                                "Base de datos central Room & API REST",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Sincronización activa • Room local",
                                fontSize = 11.sp,
                                color = productsMuted
                            )
                        }
                    }
                }
            }
        }
    }
    if (productoAEliminar != null) {
        val producto = productoAEliminar!!
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = productsError
                )
            },
            title = { Text("¿Eliminar producto?") },
            text = {
                Text(
                    "Se eliminará \"${producto.name}\" del catálogo. " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProductClick(producto.id)
                        productoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = productsError
                    )
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AdminProductRow(
    product: AdminProduct,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = productsSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(productsSoft, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = productsGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(product.category, fontSize = 11.sp, color = productsMuted)
                Text(
                    product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.price + " CLP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = productsGreen
                )

                val stockText = when {
                    product.stock == 0 -> "Agotado (0)"
                    product.stock <= 5 -> "${product.stock} unid (Bajo)"
                    else -> "${product.stock} unid"
                }

                Text(
                    stockText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (product.stock <= 5) productsError else productsMuted
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(42.dp)
                        .background(productsSoft, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar ${product.name}",
                        tint = productsGreen
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(42.dp)
                        .background(productsSoft, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar ${product.name}",
                        tint = productsError
                    )
                }
            }
        }
    }
}