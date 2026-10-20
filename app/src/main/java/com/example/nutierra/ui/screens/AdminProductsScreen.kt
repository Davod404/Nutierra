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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val productsGreen = Color(0xFF13522F)
private val productsBackground = Color(0xFFF5FBF3)
private val productsSurface = Color(0xFFFFFFFF)
private val productsMuted = Color(0xFF404941)
private val productsSoft = Color(0xFFEAF0E8)
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
    onNotificationsClick: () -> Unit = {},
    productos: List<ProductoEntity> = emptyList(),
    onProfileClick: () -> Unit = {}
) {
    // Lista real desde SQLite (Room)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(productsBackground)
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Gestión De Productos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = productsMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(productsGreen, CircleShape)
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Admin",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = productsSurface,
                tonalElevation = 3.dp
            ) {
                val tabs = listOf(
                    Triple("Inicio", Icons.Default.Dashboard, 0),
                    Triple("Productos", Icons.Default.Inventory2, 1),
                    Triple("Inventario", Icons.Default.Warehouse, 2),
                    Triple("Pedidos", Icons.AutoMirrored.Filled.ReceiptLong, 3)
                )

                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = index == 1,
                        onClick = {
                            when (index) {
                                0 -> onHomeClick()
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
                            selectedIconColor = productsGreen,
                            selectedTextColor = productsGreen,
                            indicatorColor = productsSoft
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
                            color = Color(0xFFB1F1C1),
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
                    listOf(
                        "Todos" to "Todos (8)",
                        "Frutos secos" to "Frutos secos (3)",
                        "Semillas" to "Semillas (2)",
                        "Deshidratados" to "Deshidratados (3)"
                    ).forEach { (category, label) ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(label, fontSize = 12.sp)
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
                        onEditClick = {
                            onEditProductClick(product.id)
                        }
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
                                "Sincronización activa • Último sync hace 2 min",
                                fontSize = 11.sp,
                                color = productsMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductRow(
    product: AdminProduct,
    onEditClick: () -> Unit
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
                    .background(
                        productsSoft,
                        RoundedCornerShape(12.dp)
                    ),
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
                Text(
                    product.category,
                    fontSize = 11.sp,
                    color = productsMuted
                )

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
                    color = if (product.stock <= 5) {
                        productsError
                    } else {
                        productsMuted
                    }
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        productsSoft,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar ${product.name}",
                    tint = productsGreen
                )
            }
        }
    }
}
