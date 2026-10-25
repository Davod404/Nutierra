package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import java.net.URL
import com.example.nutierra.data.local.entity.ProductoEntity

private const val DETAIL_PRICE = 4990
private const val DETAIL_STOCK = 24
private const val DETAIL_IMAGE =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuD3OnlEyc_FEvk9heuwg6t0hpJestFndzMrPOjRkx5PFTQ_wL4Db9XfcGR15owYfGY9IJn3Z47v-45KgynewOBYxIlTTJahkNrA2EFV7-XUl9Ysi7yW8AvBLFzXgted016LGt2Ucqw7gfaRUJG7Cb2UlWQw6ADsS1U6ZrNaEX46V88-lxxtkBYnYjfNEQB6i6XELpyuO4Dlm1Uv0oiob8dYTvimm3RQT4TnAq6tnVg"

private fun detailPrice(amount: Int): String {
    val formatter = NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    )
    return "$${formatter.format(amount)}"
}

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit = {},
    onAddToCart: (Int, Int) -> Unit = { _, _ -> },
    producto: ProductoEntity? = null
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current

    val precio = producto?.precio ?: DETAIL_PRICE
    val stock = producto?.stock ?: DETAIL_STOCK
    val nombre = producto?.nombre ?: "Almendras Naturales 250 g"
    val categoria = producto?.categoria ?: "Frutos secos"
    val formato = producto?.peso ?: "250 g"
    val imagen = producto?.imagenUri ?: DETAIL_IMAGE
    val descripcion = producto?.descripcion?.takeIf { it.isNotBlank() }
        ?: ("Almendras nonpareil seleccionadas de la más alta " +
                "calidad, cosechadas en valles centrales chilenos. " +
                "Sin sal añadida, ideales para colaciones y recetas saludables.")



    var quantity by remember(producto?.id) { mutableIntStateOf(1) }
    var isFavorite by remember { mutableStateOf(false) }

    val total = quantity * precio

    Scaffold(
        containerColor = colors.surface,

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
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }

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

                    Text(
                        "Detalle De Producto",
                        style = typography.titleMedium,
                        color = colors.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(colors.primary, CircleShape),
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
            Surface(
                color = colors.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            onAddToCart(producto?.id ?: 1, quantity)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )

                                Text(
                                    "Agregar al carrito",
                                    style = typography.labelLarge
                                )
                            }

                            Text(
                                detailPrice(total),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    TextButton(onClick = onBack) {
                        Text(
                            "Continuar explorando",
                            style = typography.labelMedium,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colors.surfaceContainerHigh,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            "Cosecha 2024",
                            style = typography.labelMedium,
                            color = colors.primary
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailCircleButton(
                        icon = Icons.Default.Share,
                        description = "Compartir producto",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "$nombre - Nutierra. $descripcion"
                                )
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Compartir producto"
                                )
                            )
                        }
                    )

                    DetailCircleButton(
                        icon = if (isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        description = "Guardar en favoritos",
                        iconTint = if (isFavorite)
                            colors.error
                        else
                            colors.onSurface,
                        onClick = {
                            isFavorite = !isFavorite
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surfaceContainerLowest)
            ) {
                DetailRemoteImage(
                    url = imagen,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailImageBadge(
                        categoria,
                        Icons.Default.Category,
                        colors.primaryContainer,
                        colors.onPrimary
                    )

                    DetailImageBadge(
                        "100% Natural",
                        Icons.Default.Verified,
                        colors.surfaceContainerLowest,
                        colors.secondary
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp),
                    color = colors.surfaceContainerLowest.copy(alpha = 0.92f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Spa,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            "Sin sal añadida",
                            style = typography.labelMedium,
                            color = colors.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                nombre,
                style = typography.headlineMedium,
                color = colors.onSurface
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    detailPrice(precio),
                    style = typography.headlineLarge,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "CLP",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                Surface(
                    color = colors.surfaceContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        formato.filter { it.isDigit() }.toIntOrNull()
                            ?.takeIf { it > 0 }
                            ?.let { gramos -> "(${detailPrice(precio * 1000 / gramos)} / kg)" }
                            ?: formato,
                        style = typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                color = colors.primaryFixed.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        if (stock > 0) "En stock: $stock unidades disponibles"
                        else "Sin stock por ahora",
                        style = typography.labelLarge,
                        color = colors.primary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Descripción",
                style = typography.labelLarge,
                color = colors.onSurface
            )

            Spacer(Modifier.height(6.dp))

            Text(
                descripcion,
                style = typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DetailSpecification(
                        icon = Icons.Default.Scale,
                        label = "Formato",
                        value = formato
                    )

                    DetailSpecification(
                        icon = Icons.Default.LocationOn,
                        label = "Origen",
                        value = "Valle Central, Chile"
                    )

                    DetailSpecification(
                        icon = Icons.Default.Inventory2,
                        label = "Empaque",
                        value = "Doypack hermético reutilizable"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                colors.secondaryContainer.copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MonitorHeart,
                            contentDescription = null,
                            tint = colors.secondary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Aporte Nutricional",
                            style = typography.labelLarge,
                            color = colors.onSurface
                        )

                        Text(
                            "Rico en Vitamina E, Magnesio y Fibra",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.outline
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Seleccionar cantidad",
                    style = typography.titleMedium,
                    color = colors.onSurface
                )

                Surface(
                    color = colors.surfaceContainerHigh,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (quantity > 1) quantity--
                            },
                            enabled = quantity > 1,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    colors.surfaceContainerLowest,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Disminuir cantidad"
                            )
                        }

                        Text(
                            quantity.toString(),
                            style = typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(48.dp)
                        )

                        IconButton(
                            onClick = {
                                if (quantity < stock) quantity++
                            },
                            enabled = quantity < stock,
                            modifier = Modifier
                                .size(40.dp)
                                .background(colors.primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar cantidad",
                                tint = colors.onPrimary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = colors.tertiary,
                    modifier = Modifier.size(15.dp)
                )

                Text(
                    "Máximo 24 unidades según existencias actuales",
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailCircleButton(
    icon: ImageVector,
    description: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(colors.surfaceContainer, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = description,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun DetailImageBadge(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}

@Composable
private fun DetailSpecification(
    icon: ImageVector,
    label: String,
    value: String
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(colors.surfaceContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column {
            Text(
                label,
                style = typography.labelMedium,
                color = colors.onSurfaceVariant
            )

            Text(
                value,
                style = typography.titleMedium,
                color = colors.onSurface
            )
        }
    }
}

@Composable
private fun DetailRemoteImage(
    url: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) {
        mutableStateOf<Bitmap?>(null)
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
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Almendras Naturales",
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}
