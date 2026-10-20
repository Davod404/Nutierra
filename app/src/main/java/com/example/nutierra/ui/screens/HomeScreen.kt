package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

private data class HomeProduct(
    val name: String,
    val category: String,
    val price: String,
    val stock: Int,
    val imageUrl: String
)

@Composable
fun HomeScreen(
    onCatalogClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    onMapClick: () -> Unit = {},
    onScanClick: () -> Unit = {}
) {

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var search by remember { mutableStateOf("") }
    var cartCount by remember { mutableIntStateOf(2) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val products = remember {
        listOf(
            HomeProduct(
                "Almendras Naturales 250 g",
                "Frutos secos",
                "$4.990",
                24,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAkEKB0nJbha6TZpfbWqnTcU431QzhKUaL6sIx6yLtkDV-RABRxHgAv_QzR1eJzxf8KyU8VHgyZ1eBYADg2zXfnoszLxqSl0sjUBy-xOAp0W0sv6-2tKuVzlYcE_M5NHM-vPHufKXpDRaTsVTYXgoyOKItu76BwsEtbaYwdPrRlJm1KiNGHMjKx424S7iXvrWbF0W5b1SYRothamN0QMfgk4MYSPEWBCb1vK_sdcqo"
            ),
            HomeProduct(
                "Nueces Mariposa 250 g",
                "Frutos secos",
                "$5.490",
                18,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCzn6hRR6IWBMfm8oHFD3K8new1R_P1Szg_SFt1I_auxdhxRLeGSr6HEVeBvrde7raeGn19Edhx4ZsG6GNXu759a6R5Z_CZHjKnR5zwiEjxPH5i01JIGe94uD2EphQYIepDAKXu7Po-g4Lt3jc8KGSoqsLuG6br79FCdl_ptXgRYVwIplpZ9788GRLosZlSrThHSqLDUFHZND5e8aUhWldnS-JixpTcbe1EzyM_wBY"
            ),
            HomeProduct(
                "Semillas de Chía 250 g",
                "Semillas",
                "$2.990",
                40,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAgm8aDiM2iPwf1uXzqgqnxtanu2NS5bC2k1UjlLwyZMQ0h_ce_XmS7NPflU1CL2Es798YQ0u1Z8GvarLa1VLBh1YExBqYPOHUtWNlVWgLCsB_QeKytsVjrcLXQz5jq6ago77EtVJz7VfwvSYDWWruHuJch2EuhtRvsVT0I9ExDXGs-2NYO9Q1XSyojyKECe9z2RMhLTKrxB10Z_gcW9Q5svfHXLXlud4t8Y-XjN78"
            ),
            HomeProduct(
                "Pasas Rubias 250 g",
                "Deshidratados",
                "$2.790",
                21,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCwPy6NWUBlTyWFF-In9fKbr_v6jQ1A4iL_y19tTA-qilAZ11KDiipOVk2aHQTUqCch7hTbw6QJ5ED8l8EDzK2WP_5hKODTe3maHcWnOd-ha4fwl2pxA5Lngn7fWW1BmUDVf2YQkQPKdbL6U8eWzxrqmMhppEfI-Sszo12ms2_R2IspAKsj9kHUlkDkyMwDx3wzjCmpW0qc3odwO3AMbRyaiJoxv-aiV_iiQiLbMDI"
            )
        )
    }

    val visibleProducts = products.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
                (search.isBlank() || it.name.contains(search, ignoreCase = true))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            val result = snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Ver carrito",
                duration = SnackbarDuration.Short
            )
            message = null

            if (result == SnackbarResult.ActionPerformed) {
                onCartClick() // o navController.navigate("cart")
            }
        }
    }

    Scaffold(
        containerColor = colors.surface,

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

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
        },

        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 3.dp
            ) {
                val items = listOf(
                    Triple("Inicio", Icons.Default.Home, 0),
                    Triple("Catálogo", Icons.Default.Storefront, 1),
                    Triple("Mapa", Icons.Default.Map, 2),
                    Triple("Escanear", Icons.Default.QrCodeScanner, 3),
                    Triple("Pedidos", Icons.AutoMirrored.Filled.ReceiptLong, 4),
                    Triple("Perfil", Icons.Default.Person, 5)
                )

                items.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,

                        onClick = {
                            when (index) {
                                0 -> {
                                    selectedTab = 0
                                    selectedCategory = null
                                    search = ""
                                }

                                1 -> onCatalogClick()

                                2 -> onMapClick()

                                3 -> onScanClick()

                                4 -> onOrdersClick()

                                5 -> onProfileClick()
                            }
                        },

                        icon = {
                            Icon(
                                icon,
                                contentDescription = label
                            )
                        },

                        label = {
                            Text(
                                label,
                                style = typography.labelMedium
                            )
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary,
                            selectedTextColor = colors.primary,
                            indicatorColor = colors.surfaceContainerHigh
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

            // 1. BIENVENIDA Y BÚSQUEDA
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
                                text = "¡Hola, Francisca! 👋",
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
                                onClick = {
                                    message = "Carrito: $cartCount productos"
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        colors.surfaceContainer,
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.ShoppingBag,
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
                            modifier = Modifier.weight(1f)
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
                                .horizontalScroll(rememberScrollState()),   // 👈 clave
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

            // 2. BANNER
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

            // 3. CATEGORÍAS
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

            // 4. PRODUCTOS DISPONIBLES
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
                                                url = product.imageUrl,
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
                                                        "Stock: ${product.stock} disp.",
                                                        fontSize = 10.sp,
                                                        color = colors.primary,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        Text(
                                            product.category,
                                            style = typography.labelMedium,
                                            color = colors.onSurfaceVariant
                                        )

                                        Text(
                                            product.name,
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
                                                    product.price,
                                                    style = typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colors.primary
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    cartCount++
                                                    message = "Añadido: ${product.name}"
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
                                                        "Agregar ${product.name}",
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

            // 5. MENSAJE DE CONFIANZA
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

// CARGA DE IMÁGENES REMOTAS SIN LIBRERÍAS ADICIONALES
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
            } catch (e: Exception) {
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
