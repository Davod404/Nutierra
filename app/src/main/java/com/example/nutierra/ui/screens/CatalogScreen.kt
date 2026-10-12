package com.example.nutierra.ui.screens

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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

// DATOS DE DEMOSTRACIÓN DEL CATÁLOGO DE FIGMA
private data class CatalogProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val stock: Int,
    val imageUrl: String
)

private val catalogProducts = listOf(
    CatalogProduct(
        1, "Almendras Naturales 250 g", "Frutos secos", 4990, 24,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuAz5dnzOkuHwlp8wpHDfLuIloxHRb2V9LOztdq2tz_MZ31sJ-Np6O-oeQeT_7Lv36cBybBvtBFIGSeA98h41MHYTlci03coveINDXQSglWmSk3U52NvP3N_WJEEJ5l_DgXHxnRrsEXiF4JAEtiUUhMfIFFehz7-GG6k9Hky26wspOnzYhQdRqYCRUXeBcFpfpX5PMUT9KUNv1DQhd5tA2WNmJAVDfKBnaO5jnpA9No"
    ),
    CatalogProduct(
        2, "Nueces Mariposa 250 g", "Frutos secos", 5490, 18,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCvcDOM1-IzpzTYZ9LdzEvaNrmKoLw_bQR55J36C603fQ7JA56yWl4K85h6cmcD2fcGMxs7ItRcjgOT9nmhQRl4AeE56yiUNGJQTg-pyRqLNMYhz2gvyR8Kuf_pyP4oVKy84kTqzmKB7PQMlUSwCIuEy6-1rlfb7htyC4PsyfLOlM2CZPNnNRFmIVcHBXLqDofIQdrlVpV739xzv5dfmA3H-Yu0Y-gr4fLKrJU9gmo"
    ),
    CatalogProduct(
        3, "Maní Tostado 500 g", "Frutos secos", 3290, 32,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBlbJFLgNnJEviDRDzX-I8D0Hh9uEzOLY2GsEhF7eBQNFN-7EK_QMiji-nkyM5-SbhwEb-EOR2VVHiETq3W7sWfOk8EahVIYSSdyoRuNAZSTBQAzxvZsio_RO1BW3UcjEb-98t8nlN-P--5RDfXnh5lJJAAMWs4YRqcMFWkSMam5UpLXnmY9BJ9oHBF3XTxkxhu-BFpkJPtHabELGpPOe865925kSFzgWHUC8LNVcU"
    ),
    CatalogProduct(
        4, "Semillas de Chía 250 g", "Semillas", 2990, 40,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCO4V0PXVODW2kteBFV4_L2nh49HnqEJcGVVJT8iXjWdvP8abQP_ZMBCTDAezTNBtLL0wmOID1IF19ie5K5AsHZzcsMGSeCMO-VYQ9fdxAzCfSdpFLIQAZTFkSHWyoOZPXX0z3_2eudq6m_SnVWrKHw5rPU9DKY96v3T5RvVQTUrANQKhIbMvXFwkKdREBfd0QxDLSDIkOa8DhNx3brvxR8ALRFcYoQy_BnxRqpB98"
    ),
    CatalogProduct(
        5, "Semillas de Linaza 500 g", "Semillas", 2490, 15,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCfovxzO0Ua7AVwgcveHSw9Il5XjqaI3iGw9_wLHQMfAPuZk0Arzr3KiRdSeq_tCUvssHJ_Wt_OYD_35yrPu2S6KYS5d1Kh_SSp3foUMcJDTRD5n5X3lkDyhxMTwo9MtCtfTyV0hjrtzoA4Ffr8kBQl1pdwdPm0QcHA6NUAcKUnKrFfTzOMXf0CX75ZLP1tj1spySxr0k4pSe45SwM8SiVAKZyGRXCmq85dcOEB39U"
    ),
    CatalogProduct(
        6, "Pasas Rubias 250 g", "Deshidratados", 2790, 21,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBlPYN02qcN-0-brRALT0bpbq8bW5fTKwJZKXpV5_MZmteXk3d5xD46Wo9dL5XdmshoTZXYin0syhCz-qnG57Qqljrh4snfW37TI1OSUuuPy6oI7_WJo2RlB2q4LrJdnTXm1Qt7F3FC1chGRgDypi1PwEJ6IbACmluCCtuHrOn_tkaV1IYsWvtvbSaJHfRZvivu67m9nYXts4jy1boryioHdaGbjQOoiONo2bnD1a8"
    ),
    CatalogProduct(
        7, "Cranberries 250 g", "Deshidratados", 4290, 0,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuC2-hJqCt-CTdqksXXJ5-vo2oIw5UIbSZ3zKkraUdZPC7aSR5wakWQ5R0yWjkF7AGqZ3ESKKT71KSr4jJiZ1Cbonetqveol0y-GutywnLRWjA_CbYBG0wApEfAAfZAmD90Sxggy9pFtvr-tDjJnm-1DafYMrRW_sjWyU31PfpyVG5-qszYkLr9EBdjy7e4cU0J-P_bmPVjh5rH5bgdWHvjE_o4NgHQCbakOIR3vafI"
    ),
    CatalogProduct(
        8, "Damascos Deshidratados 250 g", "Deshidratados", 3990, 12,
        "https://lh3.googleusercontent.com/aida-public/AB6AXuA7sllKwRO-DYMPsQJvXsmmjjW4D7sEuRmegHemGm7-csHoScfhHEYR88PJNdYLr0jTH7D72Pn0qkRg7wd6f7gY7-XERs0w3T4ULG1fbjI-dqdh89CbWOUZ-yAuTGryqKtv8GKKzv-AB31P2pRkbNinKgh4QUge8ci6Jooe0M18t-pIFNLLXLa2rsiuTawBaUmJN0NqqZQr9ataGVqrMwaBD8nx4VUWGbsD0eywOEs"
    )
)

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
    onHomeClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todos") }
    var sort by remember { mutableStateOf("Disponibilidad") }
    var sortExpanded by remember { mutableStateOf(false) }

    // Filtrado y ordenamiento reactivo
    val visibleProducts = remember(search, category, sort) {
        val filtered = catalogProducts.filter { product ->
            (category == "Todos" || product.category == category) &&
                    product.name.contains(search.trim(), ignoreCase = true)
        }

        when (sort) {
            "Menor precio" -> filtered.sortedBy { it.price }
            "Mayor precio" -> filtered.sortedByDescending { it.price }
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
                                .background(
                                    colors.primaryFixed,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = "Nutierra",
                                tint = colors.primary
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
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 2.dp
            ) {
                CatalogNavItem(
                    "Inicio",
                    Icons.Default.Home,
                    false,
                    onHomeClick
                )
                CatalogNavItem(
                    "Catálogo",
                    Icons.Default.Storefront,
                    true,
                    {}
                )
                CatalogNavItem(
                    "Mis Pedidos",
                    Icons.AutoMirrored.Filled.ReceiptLong,
                    false,
                    onOrdersClick
                )
                CatalogNavItem(
                    "Perfil",
                    Icons.Default.Person,
                    false,
                    onProfileClick
                )
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

            // TÍTULO Y BUSCADOR
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

            // FILTROS DE CATEGORÍA
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

            // CONTADOR Y ORDENAMIENTO
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

            // RESULTADOS
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
    product: CatalogProduct,
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
                    url = product.imageUrl,
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
                    product.category,
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    product.name,
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
                        catalogPrice(product.price),
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

// IMÁGENES REMOTAS DE FIGMA
@Composable
private fun CatalogRemoteImage(
    url: String,
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

// BARRA DE NAVEGACIÓN INFERIOR
@Composable
private fun RowScope.CatalogNavItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                icon,
                contentDescription = title
            )
        },
        label = {
            Text(title)
        }
    )
}
