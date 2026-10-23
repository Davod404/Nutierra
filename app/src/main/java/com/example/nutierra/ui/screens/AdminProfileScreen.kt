package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.PerfilViewModel
import com.example.nutierra.ui.viewmodel.aPerfilUi

private val accountGreen = Color(0xFF13522F)
private val accountBackground = Color(0xFFF5FBF3)
private val accountSurface = Color.White
private val accountMuted = Color(0xFF404941)
private val accountMint = Color(0xFFB1F1C1)
private val accountRed = Color(0xFFBA1A1A)


@Composable
fun AdminProfileScreen(
    onBack: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    totalProductos: Int = 0,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val usuario by perfilViewModel.usuario.collectAsStateWithLifecycle()
    val perfil = usuario.aPerfilUi()

    var mostrarDialogoSalida by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = accountBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = accountGreen
                    )
                }
                Text(
                    "Mi cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    color = accountGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Surface(
                color = accountSurface,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(accountGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            perfil.iniciales,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        perfil.nombreCompleto,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = accountMuted
                    )

                    Spacer(Modifier.height(8.dp))

                    Surface(color = accountMint, shape = CircleShape) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = accountGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Rol: ${perfil.rol}",
                                style = MaterialTheme.typography.labelMedium,
                                color = accountGreen
                            )
                        }
                    }
                }
            }
            Surface(
                color = accountSurface,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    FilaDato(Icons.Default.Email, "Correo", perfil.correo)
                    HorizontalDivider(color = accountBackground)
                    FilaDato(
                        Icons.Default.Inventory2,
                        "Productos registrados",
                        totalProductos.toString()
                    )
                }
            }

            Text(
                "Gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accountMuted
            )

            Surface(
                color = accountSurface,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    FilaAcceso(Icons.Default.Storefront, "Productos", onProductsClick)
                    HorizontalDivider(color = accountBackground)
                    FilaAcceso(Icons.Default.Inventory, "Inventario", onInventoryClick)
                    HorizontalDivider(color = accountBackground)
                    FilaAcceso(Icons.AutoMirrored.Filled.ReceiptLong, "Pedidos", onOrdersClick)
                }
            }

            OutlinedButton(
                onClick = { mostrarDialogoSalida = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accountRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (mostrarDialogoSalida) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalida = false },
            icon = {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = accountRed)
            },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrás que ingresar nuevamente para administrar la tienda.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSalida = false
                        perfilViewModel.cerrarSesion()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accountRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoSalida = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = accountGreen)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = accountMuted)
            Text(
                valor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = accountMuted
            )
        }
    }
}

@Composable
private fun FilaAcceso(icono: ImageVector, texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = accountGreen)
        Spacer(Modifier.width(16.dp))
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            color = accountMuted,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = accountMuted
        )
    }
}
