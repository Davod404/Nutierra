package com.example.nutierra.ui.screens

import com.example.nutierra.data.local.entity.ProductoEntity
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

private val catalogCategories = listOf(
    "Todos", "Frutos secos", "Semillas", "Deshidratados"
)

private fun catalogPrice(value: Int): String {
    return "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(value)
}

@Composable
fun CatalogScreen(
    productos: List<ProductoEntity> = emptyList(),
    onHomeClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val catalogProducts = productos

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todos") }
    var sort by remember { mutableStateOf("Disponibilidad") }
    var sortExpanded by remember { mutableStateOf(false) }

    val visibleProducts = remember(catalogProducts, search, category, sort) {
        val filtered = catalogProducts.filter { product ->
            (category == "Todos" || product.categoria == category) &&
                    product.nombre.contains(search.trim(), ignoreCase = true)
        }

        when (sort) {
            "Menor precio" -> filtered.sortedBy { it.precio }
            "Mayor precio" -> filtered.sortedByDescending { it.precio }
            else -> filtered.sortedByDescending { it.stock }
        }
    }

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
                        .statusBarsPadding()
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
                                "Nutierra",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                            Text(
                                "Catálogo",
                                style = typography.titleMedium,
                                color = colors.onSurface
                            )
                        }
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
                            contentDescription = "Mi perfil",
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
                                    Icons.Default.Storefront,
                                    contentDescription = "Catálogo",
                                    tint = if (true) MaterialTheme.colorScheme.surface
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
                                    Icons.AutoMirrored.Filled.ReceiptLong,
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
    ) { innerPadding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Nuestros productos",
                            style = typography.headlineMedium,
                            color = colors.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    colors.surfaceContainerHigh,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Buscar por nombre...")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = colors.outline
                            )
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
                            focusedContainerColor = colors.surfaceContainerLowest,
                            unfocusedContainerColor = colors.surfaceContainerLowest,
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = typography.bodyMedium
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    catalogCategories.forEach { item ->
                        val selected = item == category

                        Surface(
                            modifier = Modifier
                                .height(48.dp)
                                .clickable { category = item },
                            color = if (selected)
                                colors.primary
                            else
                                colors.surfaceContainerLowest,
                            shape = CircleShape,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (selected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = colors.onPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    item,
                                    style = typography.labelMedium,
                                    color = if (selected)
                                        colors.onPrimary
                                    else
                                        colors.onSurface
                                )
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (visibleProducts.isEmpty()) {
                            "0 productos encontrados"
                        } else {
                            "Mostrando ${visibleProducts.size} " +
                                    if (visibleProducts.size == 1)
                                        "producto disponible"
                                    else
                                        "productos disponibles"
                        },
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        Surface(
                            modifier = Modifier
                                .height(48.dp)
                                .clickable { sortExpanded = true },
                            color = colors.surfaceContainerLow,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Sort,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )

                                Text(
                                    sort,
                                    style = typography.labelMedium,
                                    color = colors.onSurface
                                )

                                Icon(
                                    Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = colors.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            listOf(
                                "Disponibilidad",
                                "Menor precio",
                                "Mayor precio"
                            ).forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option,
                                            style = typography.labelMedium
                                        )
                                    },
                                    onClick = {
                                        sort = option
                                        sortExpanded = false
                                    },
                                    trailingIcon = {
                                        if (sort == option) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = colors.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (visibleProducts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    colors.surfaceContainerLow,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Sin coincidencias",
                            style = typography.titleMedium
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "No encontramos productos para tu búsqueda. " +
                                    "Prueba con otro término o limpia los filtros.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                search = ""
                                category = "Todos"
                                sort = "Disponibilidad"
                            }
                        ) {
                            Icon(
                                Icons.Default.RestartAlt,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Restablecer catálogo")
                        }
                    }
                }
            } else {
                items(
                    items = visibleProducts,
                    key = { it.id }
                ) { product ->
                    CatalogProductCard(
                        product = product,
                        onProductClick = {
                            onProductClick(product.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CatalogProductCard(
    product: ProductoEntity,
    onProductClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    var favorite by remember(product.id) {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (product.stock == 0) 0.6f else 1f),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(colors.surfaceContainerHigh)
            ) {
                CatalogRemoteImage(
                    url = product.imagenUri,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = if (product.stock > 0)
                        colors.surfaceContainerLow
                    else
                        colors.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (product.stock > 0) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(colors.primary, CircleShape)
                            )
                        } else {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = null,
                                tint = colors.onErrorContainer,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Text(
                            if (product.stock > 0)
                                "${product.stock} disp."
                            else
                                "Agotado",
                            style = typography.labelMedium,
                            color = if (product.stock > 0)
                                colors.primary
                            else
                                colors.onErrorContainer
                        )
                    }
                }

                if (product.stock > 0) {
                    IconButton(
                        onClick = { favorite = !favorite },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(32.dp)
                            .background(
                                colors.surfaceContainerLowest.copy(alpha = 0.9f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (favorite)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (favorite)
                                colors.error
                            else
                                colors.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    product.categoria,
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    product.nombre,
                    style = typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        catalogPrice(product.precio),
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    Text(
                        "CLP",
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onProductClick,
                    enabled = product.stock > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = colors.surfaceContainerHighest,
                        disabledContentColor = colors.outline
                    )
                ) {
                    Text(
                        if (product.stock > 0)
                            "Ver producto"
                        else
                            "Agotado",
                        style = typography.labelMedium,
                        maxLines = 1
                    )

                    if (product.stock > 0) {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogRemoteImage(
    url: String?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) {
        mutableStateOf<android.graphics.Bitmap?>(null)
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
                MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}