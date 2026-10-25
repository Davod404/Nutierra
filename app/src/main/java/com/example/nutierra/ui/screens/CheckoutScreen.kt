package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.CarritoViewModel
import com.example.nutierra.ui.components.FilaError

private data class CheckoutProduct(
    val name: String,
    val format: String,
    val price: Int,
    val quantity: Int
)

private fun checkoutPrice(value: Int): String {
    return "$" + java.text.NumberFormat.getIntegerInstance(
        java.util.Locale.forLanguageTag("es-CL")
    ).format(value)
}

@Composable
fun CheckoutScreen(
    viewModel: CarritoViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOrderSuccess: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val items by viewModel.items.collectAsStateWithLifecycle()
    val procesando by viewModel.procesando.collectAsStateWithLifecycle()
    val mensajeError by viewModel.error.collectAsStateWithLifecycle()

    val checkoutProducts = remember(items) {
        items.map {
            CheckoutProduct(
                name = it.nombre,
                format = it.peso,
                price = it.precio,
                quantity = it.cantidad
            )
        }
    }

    val total = checkoutProducts.sumOf { it.price * it.quantity }
    val totalUnits = checkoutProducts.sumOf { it.quantity }

    var showConfirmation by remember {
        mutableStateOf(false)
    }

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
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Nutierra",
                        style = typography.titleMedium,
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mensajeError?.let { FilaError(it) }

                    Button(
                        onClick = {
                            showConfirmation = true
                        },
                        enabled = checkoutProducts.isNotEmpty() && !procesando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        if (procesando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colors.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                "Enviar solicitud de compra",
                                style = typography.labelLarge
                            )
                        }
                    }

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "Regresar al carrito",
                            style = typography.labelLarge,
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
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "2",
                                    style = typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.onPrimary
                                )
                            }

                            Text(
                                "Paso 2 de 2",
                                style = typography.labelLarge,
                                color = colors.primary
                            )
                        }

                        Text(
                            "Revisión y envío",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    }

                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = colors.primary,
                        trackColor = colors.surfaceContainerHighest
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                "Carrito armado",
                                style = typography.labelMedium,
                                color = colors.primary
                            )
                        }

                        Text(
                            "Confirmación final",
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.primaryFixed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.primary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = colors.surfaceContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                "Sin cobro digital",
                                style = typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                )
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Aviso importante",
                            style = typography.titleMedium
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Tu pedido quedará pendiente de validación " +
                                    "por la tienda antes de la preparación física. " +
                                    "No se realiza ningún cobro en línea. " +
                                    "Te notificaremos una vez confirmada la " +
                                    "disponibilidad de existencias.",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

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
                        tint = colors.primary
                    )

                    Text(
                        "Resumen de artículos",
                        style = typography.titleMedium
                    )
                }

                Surface(
                    color = colors.surfaceContainer,
                    shape = CircleShape
                ) {
                    Text(
                        "${checkoutProducts.size} productos",
                        style = typography.labelMedium,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    checkoutProducts.forEachIndexed { index, product ->

                        CheckoutProductRow(product)

                        if (index < checkoutProducts.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = colors.surfaceContainerHigh
                            )
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CheckoutInfoRow(
                        "Desglose de bultos:",
                        "$totalUnits unidades totales"
                    )

                    CheckoutInfoRow(
                        "Modalidad de pago:",
                        "Al coordinar / En retiro"
                    )

                    HorizontalDivider(
                        color = colors.surfaceContainerHigh
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Total a coordinar",
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Sin cobros adicionales",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Text(
                            "${checkoutPrice(total)} CLP",
                            style = typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }

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
                        Icons.Default.PersonPin,
                        contentDescription = null,
                        tint = colors.primary
                    )

                    Text(
                        "Datos de coordinación",
                        style = typography.titleMedium
                    )
                }

                Text(
                    "Verificado",
                    style = typography.labelMedium,
                    color = colors.primary
                )
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CheckoutContactRow(
                        icon = Icons.Default.Badge,
                        label = "Cliente solicitante",
                        value = "Francisca Morales"
                    )

                    CheckoutContactRow(
                        icon = Icons.Default.PhoneAndroid,
                        label = "Teléfono de contacto",
                        value = "+56 9 8765 4321"
                    )

                    CheckoutContactRow(
                        icon = Icons.Default.Email,
                        label = "Correo electrónico",
                        value = "francisca.morales@correo.cl"
                    )

                    HorizontalDivider(
                        color = colors.surfaceContainerHigh
                    )

                    CheckoutContactRow(
                        icon = Icons.Default.Storefront,
                        label = "Modalidad acordada",
                        value = "Retiro en tienda / Coordinación directa"
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Column {
                        Text(
                            "Atención directa Nutierra",
                            style = typography.labelLarge
                        )

                        Text(
                            "Tienda activa",
                            style = typography.bodyMedium,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showConfirmation = false
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "¿Confirmar tu pedido?",
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    "Se registrará tu orden por " +
                            "${checkoutPrice(total)} CLP " +
                            "($totalUnits unidades) y se descontará del inventario.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmation = false
                        viewModel.confirmarCompra { _ ->
                            // No se entrega el pedido; se usará si se agrega una PayScreen.
                            onOrderSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sí, confirmar compra")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmation = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CheckoutProductRow(
    product: CheckoutProduct
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    colors.surfaceContainer,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )

            Surface(
                modifier = Modifier.align(Alignment.BottomEnd),
                color = colors.primary,
                shape = RoundedCornerShape(topStart = 4.dp)
            ) {
                Text(
                    "x${product.quantity}",
                    style = typography.labelMedium,
                    color = colors.onPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                product.name,
                style = typography.labelLarge,
                color = colors.onSurface
            )

            Text(
                "Formato ${product.format}",
                style = typography.labelMedium,
                color = colors.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "${checkoutPrice(product.price * product.quantity)} CLP",
                style = typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.primary
            )

            Text(
                "${checkoutPrice(product.price)} c/u",
                style = typography.labelMedium,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CheckoutInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CheckoutContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                modifier = Modifier.size(18.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                label,
                style = typography.labelMedium,
                color = colors.onSurfaceVariant
            )

            Text(
                value,
                style = typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
        }
    }
}