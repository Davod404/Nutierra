package com.example.nutierra.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.PerfilViewModel
import com.example.nutierra.ui.viewmodel.aPerfilUi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    perfilViewModel: PerfilViewModel = viewModel()
) {

    val usuario by perfilViewModel.usuario.collectAsStateWithLifecycle()
    val perfil = usuario.aPerfilUi()

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var showLogoutDialog by remember { mutableStateOf(false) }

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
                                text = "Mi Perfil",
                                style = typography.titleLarge,
                                color = colors.onSurface
                            )
                        }
                    }

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
            NavigationBar(
                containerColor = colors.surface,
                tonalElevation = 2.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onCatalogClick,
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Catálogo") },
                    label = { Text("Catálogo") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMapClick,
                    icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onScanClick,
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear") },
                    label = { Text("Escanear") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        colors.primaryFixed.copy(alpha = 0.5f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = perfil.iniciales,
                                    style = typography.titleLarge,
                                    color = colors.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(22.dp)
                                    .background(colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = colors.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = perfil.nombreCompleto,
                                style = typography.titleLarge,
                                color = colors.onSurface
                            )

                            Text(
                                text = perfil.correo,
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )

                            Spacer(Modifier.height(4.dp))

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
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = colors.onPrimaryFixed,
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Text(
                                        "Rol: ${perfil.rol}",
                                        style = typography.labelMedium,
                                        color = colors.onPrimaryFixed
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = colors.surfaceContainerLow
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 10.dp
                            ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )

                                Text(
                                    "Cliente Verificado Valle Central",
                                    style = typography.labelMedium,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            Text(
                                "Activo",
                                style = typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "DATOS DE LA CUENTA",
                    style = typography.labelLarge,
                    color = colors.onSurfaceVariant
                )

                Text(
                    "Detalle",
                    style = typography.labelMedium,
                    color = colors.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            ProfileInfoCard(
                icon = Icons.Default.Phone,
                label = "Teléfono móvil",
                value = "+56 9 8765 4321",
                trailingIcon = Icons.Default.Lock
            )

            Spacer(Modifier.height(8.dp))

            ProfileInfoCard(
                icon = Icons.Default.LocationOn,
                label = "Dirección de entrega",
                value = "Providencia, Santiago",
                trailingIcon = Icons.Default.ChevronRight
            )

            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOrdersClick),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                colors.secondaryContainer.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = colors.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Historial de compras",
                            style = typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )

                        Text(
                            "3 pedidos registrados",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                    }

                    Surface(
                        color = colors.surfaceContainer,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                "1 en ruta",
                                style = typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "ACERCA DEL SISTEMA",
                style = typography.labelLarge,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    colors.primaryFixed,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = colors.onPrimaryFixed,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Nutierra",
                                    style = typography.titleMedium,
                                    color = colors.onSurface
                                )

                                Surface(
                                    color = colors.surfaceContainerHigh,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "v1.0.0 (MVP)",
                                        style = typography.labelMedium,
                                        color = colors.onSurfaceVariant,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                "Plataforma directa para comercialización local, reserva de cosechas y sincronización de stock con bodega central.",
                                style = typography.bodyMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    HorizontalDivider(
                        color = colors.surfaceContainer
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                "Inventario sincronizado",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Text(
                            "Región Metropolitana",
                            style = typography.labelMedium,
                            color = colors.outline
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    showLogoutDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.surfaceContainerLowest,
                    contentColor = colors.error
                )
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Cerrar sesión",
                    style = typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            icon = {
                Icon(
                    Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "¿Deseas cerrar tu sesión?",
                    style = typography.titleLarge
                )
            },
            text = {
                Text(
                    "Tendrás que ingresar nuevamente tu correo y contraseña para realizar solicitudes o revisar tus despachos.",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        perfilViewModel.cerrarSesion()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.error,
                        contentColor = colors.onError
                    )
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = colors.surfaceContainerLowest
        )
    }
}

@Composable
private fun ProfileInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        colors.surfaceContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
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

            Icon(
                trailingIcon,
                contentDescription = null,
                tint = colors.outlineVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
