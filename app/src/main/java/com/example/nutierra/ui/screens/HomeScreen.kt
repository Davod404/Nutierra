package com.example.nutierra.ui.screens

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.graphics.BitmapFactory
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.ui.viewmodel.CarritoViewModel
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.ui.components.CampanitaNotificaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun HomeScreen(
    productos: List<ProductoEntity> = emptyList(),
    onCatalogClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    onMapClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    noLeidas: Int = 0,
    viewModel: CarritoViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val cartItems by viewModel.items.collectAsStateWithLifecycle()
    val usuario by SesionManager.usuarioActual.collectAsStateWithLifecycle()
    val cartCount = cartItems.sumOf { it.cantidad }
    val nombreUsuario = usuario?.nombre?.takeIf { it.isNotBlank() } ?: "Invitado"
    val snackbarHostState = remember { SnackbarHostState() }

    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val visibleProducts = productos.filter {
        (selectedCategory == null || it.categoria == selectedCategory) &&
                (search.isBlank() || it.nombre.contains(search, ignoreCase = true))
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            message = null
        }
    }

    Scaffold(
        containerColor = colors.surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                                text = "Inicio",
                                style = typography.titleMedium,
                                color = colors.onSurface
                            )
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
                                    Icons.Default.Home,
                                    contentDescription = "Inicio",
                                    tint = colors.onTertiary
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = "Catálogo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = "Mapa",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = "Escanear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "¡Hola, $nombreUsuario! 👋",
                                style = typography.headlineMedium,
                                color = colors.onSurface
                            )

                            Text(
                                text = "Encuentra tus favoritos 100% naturales",
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { onCartClick() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        colors.surfaceContainer,
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    tint = colors.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 5.dp, y = (-5).dp)
                                    .size(20.dp)
                                    .background(colors.secondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    color = colors.onSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                colors.surfaceContainerLowest,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = colors.outline
                        )

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            if (search.isEmpty()) {
                                Text(
                                    "Buscar almendras, nueces, chía...",
                                    style = typography.bodyMedium,
                                    color = colors.outline,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            BasicTextField(
                                value = search,
                                onValueChange = { search = it },
                                singleLine = true,
                                textStyle = typography.bodyMedium.copy(
                                    color = colors.onSurface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        IconButton(
                            onClick = { showFilters = !showFilters }
                        ) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Filtros",
                                tint = colors.primary
                            )
                        }
                    }

                    if (showFilters) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Todas",
                                "Frutos secos",
                                "Semillas",
                                "Deshidratados"
                            ).forEach { category ->
                                FilterChip(
                                    selected = selectedCategory ==
                                            category.takeUnless { it == "Todas" },
                                    onClick = {
                                        selectedCategory =
                                            category.takeUnless { it == "Todas" }
                                    },
                                    label = {
                                        Text(category)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.primary)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = colors.primaryFixed,
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = colors.onPrimaryFixed
                                )

                                Text(
                                    "Cosecha 2025",
                                    style = typography.labelMedium,
                                    color = colors.onPrimaryFixed
                                )
                            }
                        }

                        Text(
                            "Descubre lo mejor de la naturaleza",
                            style = typography.titleLarge,
                            color = colors.onPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Frutos secos, semillas y deshidratados para tu día a día.",
                            style = typography.bodyMedium,
                            color = colors.primaryFixedDim
                        )

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = onCatalogClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.tertiaryFixed,
                                contentColor = colors.onTertiaryFixed
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Explorar productos")

                            Spacer(Modifier.width(6.dp))

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Categorías",
                            style = typography.titleMedium
                        )

                        TextButton(
                            onClick = { selectedCategory = null }
                        ) {
                            Text("Ver todas")

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val categories = listOf(
                            Triple("Frutos secos", "+18 prod.", Icons.Default.Eco),
                            Triple("Semillas", "+12 prod.", Icons.Default.Grain),
                            Triple("Deshidratados", "+10 prod.", Icons.Default.Spa)
                        )

                        categories.forEach { (name, count, icon) ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(125.dp)
                                    .clickable {
                                        selectedCategory = name
                                    },
                                color = colors.surfaceContainerLowest,
                                shape = RoundedCornerShape(14.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                colors.surfaceContainerHigh,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = colors.primary
                                        )
                                    }

                                    Text(
                                        name,
                                        style = typography.labelLarge,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        lineHeight = 15.sp
                                    )

                                    Surface(
                                        color = colors.surfaceContainer,
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            count,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 2.dp
                                            ),
                                            fontSize = 11.sp,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Productos disponibles",
                                style = typography.titleMedium
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(8.dp)
                                        .background(colors.primary, CircleShape)
                                )

                                Text(
                                    "Despacho hoy",
                                    style = typography.labelMedium,
                                    color = colors.primary
                                )
                            }
                        }

                        Text(
                            "Frescos y listos para despacho",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    visibleProducts.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pair.forEach { product ->
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = colors.surfaceContainerLowest,
                                    shape = RoundedCornerShape(14.dp),
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(colors.surfaceContainerLow)
                                        ) {
                                            RemoteProductImage(
                                                url = product.imagenUri.toString(),
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(8.dp),
                                                color = colors.surfaceBright,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(
                                                        horizontal = 7.dp,
                                                        vertical = 4.dp
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(
                                                        Modifier
                                                            .size(6.dp)
                                                            .background(
                                                                colors.primary,
                                                                CircleShape
                                                            )
                                                    )

                                                    Text(
                                                        "Stock: ${product.stock}",
                                                        fontSize = 10.sp,
                                                        color = colors.primary,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        Text(
                                            product.categoria,
                                            style = typography.labelMedium,
                                            color = colors.onSurfaceVariant
                                        )

                                        Text(
                                            product.nombre,
                                            style = typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 2,
                                            minLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    "Precio",
                                                    fontSize = 11.sp,
                                                    color = colors.outline
                                                )

                                                Text(
                                                    "$"+product.precio.toString(),
                                                    style = typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primary
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    viewModel.agregar(product.id, 1)
                                                },
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .background(
                                                        colors.surfaceContainerHigh,
                                                        RoundedCornerShape(12.dp)
                                                    )
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription =
                                                        "Agregar ${product.nombre}",
                                                    tint = colors.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (pair.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }

                    if (visibleProducts.isEmpty()) {
                        Text(
                            "No se encontraron productos",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Surface(
                    color = colors.surfaceContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    colors.primary.copy(alpha = 0.1f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = colors.primary
                            )
                        }

                        Column {
                            Text(
                                "Envío seguro y origen seleccionado",
                                style = typography.labelLarge
                            )

                            Text(
                                "Calidad garantizada del Valle Central directo a tu mesa.",
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteProductImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    LaunchedEffect(url) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                URL(url).openStream().use {
                    BitmapFactory.decodeStream(it)
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}