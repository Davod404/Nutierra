package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import com.example.nutierra.ui.components.copiarImagenALaApp
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.ui.viewmodel.ProductoFormViewModel
import com.example.nutierra.data.local.entity.ProductoEntity

data class EditedAdminProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val imageUri: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditProductScreen(
    productId: Int = 1,
    onBack: () -> Unit,
    onInventoryClick: () -> Unit,
    onSaveProduct: (EditedAdminProduct) -> Unit,
    producto: ProductoEntity? = null,
    viewModel: ProductoFormViewModel = viewModel()
) {
    LaunchedEffect(producto?.id) {
        producto?.let { viewModel.cargarProducto(it) }
    }

    val colors = MaterialTheme.colorScheme
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val productName = estado.nombre.valor
    val category = estado.categoria
    val price = estado.precio.valor
    val imageUri = estado.imagenUri?.takeIf { it != producto?.imagenUri }
    val validName = estado.nombreValido
    val validPrice = estado.precioValido
    val context = LocalContext.current

    var showSaveBanner by remember(productId) {
        mutableStateOf(false)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.cambiarImagen(copiarImagenALaApp(context, uri) ?: uri.toString())
        }
    }

    fun saveChanges() {
        if (viewModel.validar(exigirImagen = false)) {
            onSaveProduct(
                EditedAdminProduct(
                    id = productId,
                    name = productName.trim(),
                    category = category,
                    price = price.toInt(),
                    imageUri = imageUri
                )
            )
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Producto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = { saveChanges() },
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (showSaveBanner) {
                Surface(
                    color = colors.surfaceContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colors.primary
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Modificaciones registradas",
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Los cambios se enviaron al gestor de productos.",
                                color = colors.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = { showSaveBanner = false }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar aviso"
                            )
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "ID: #${producto?.id ?: productId}",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Producto del catálogo",
                                fontSize = 11.sp,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = colors.surfaceContainer,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "● Activo",
                                modifier = Modifier.padding(
                                    horizontal = 9.dp,
                                    vertical = 6.dp
                                ),
                                color = colors.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        "Ficha Maestra del Artículo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Imagen del producto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                        Text(
                            "Resolución óptima 1:1",
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(
                                    colors.surfaceContainer,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = "Nutierra",
                                    tint = colors.onPrimary
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                if (imageUri == null) {
                                    "Fotografía principal para catálogo móvil y boleta electrónica."
                                } else {
                                    "Nueva imagen seleccionada."
                                },
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        imagePicker.launch("image/*")
                                    },
                                    contentPadding = PaddingValues(
                                        horizontal = 10.dp
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.SyncAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        "Reemplazar",
                                        fontSize = 12.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.cambiarImagen(producto?.imagenUri) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar nueva imagen",
                                        tint = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditFieldLabel("Nombre comercial *")

                    OutlinedTextField(
                        value = productName,
                        onValueChange = viewModel::cambiarNombre,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Ej: Nueces Mariposa Peladas")
                        },
                        singleLine = true,
                        isError = estado.nombre.error != null,
                        supportingText = textoDeError(estado.nombre.error),
                        shape = RoundedCornerShape(12.dp),
                        colors = editTextFieldColors()
                    )

                    Text(
                        "Visible para clientes en la vitrina de compras",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditFieldLabel("Categoría *")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Frutos secos",
                            "Semillas",
                            "Deshidratados"
                        ).forEach { option ->
                            val selected = category == option

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(65.dp)
                                    .clickable {
                                        viewModel.cambiarCategoria(option)
                                    },
                                color = if (selected) {
                                    colors.primary
                                } else {
                                    colors.surfaceContainer
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(5.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = colors.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        option,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selected) {
                                            colors.onPrimary
                                        } else {
                                            colors.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainerLowest,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EditFieldLabel("Precio unitario (CLP) *")

                    OutlinedTextField(
                        value = price,
                        onValueChange = viewModel::cambiarPrecio,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Text(
                                "$",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        trailingIcon = {
                            Text(
                                "CLP",
                                modifier = Modifier.padding(end = 12.dp),
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        isError = estado.precio.error != null,
                        supportingText = textoDeError(estado.precio.error),
                        shape = RoundedCornerShape(12.dp),
                        colors = editTextFieldColors()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Precio final",
                            fontSize = 12.sp,
                            color = colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Surface(
                color = colors.surfaceContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Warehouse,
                            contentDescription = null,
                            tint = colors.primary
                        )

                        Text(
                            "Control Centralizado de Bodega",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )
                    }

                    Text(
                        "Stock disponible: ${producto?.stock ?: 0} unidades",
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )

                    Text(
                        "El inventario físico (${producto?.stock ?: 0} unidades actuales) se gestiona de forma centralizada desde el módulo Inventario para mantener trazabilidad con la base de datos de lotes y vencimientos.",
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant,
                        lineHeight = 19.sp
                    )

                    OutlinedButton(
                        onClick = onInventoryClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Ir a Control de Inventario",
                            color = colors.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = colors.primary
                        )
                    }
                }
            }

            Surface(
                color = colors.surfaceContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        if (validName && validPrice) {
                            Icons.Default.Verified
                        } else {
                            Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = if (validName && validPrice) {
                            colors.primary
                        } else {
                            colors.error
                        }
                    )

                    Text(
                        if (validName && validPrice) {
                            "Todos los datos son válidos y cumplen la normativa de etiquetado nacional."
                        } else {
                            "Revisa los campos obligatorios antes de guardar."
                        },
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EditFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun editTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Transparent
    )