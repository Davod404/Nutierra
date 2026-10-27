package com.example.nutierra.ui.screens

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutierra.ui.components.FilaError
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.ui.viewmodel.ProductoFormViewModel
import com.example.nutierra.ui.components.copiarImagenALaApp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

data class NewAdminProduct(
    val name: String,
    val category: String,
    val price: Int,
    val stock: Int,
    val weight: String,
    val imageUri: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateProductScreen(
    onBack: () -> Unit,
    onSaveProduct: (NewAdminProduct) -> Unit,
    onTakePhotoClick: () -> Unit = {},
    fotoTomada: String? = null,
    viewModel: ProductoFormViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme

    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.iniciarNuevo()
    }

    val name = estado.nombre.valor
    val category = estado.categoria
    val price = estado.precio.valor
    val stock = estado.stock
    val weight = estado.peso
    val imageUri = estado.imagenUri

    val validName = estado.nombreValido
    val validPrice = estado.precioValido
    val validStock = estado.stockValido

    LaunchedEffect(fotoTomada) {
        if (fotoTomada != null) {
            viewModel.cambiarImagen(fotoTomada)
        }
    }

    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.cambiarImagen(copiarImagenALaApp(context, uri) ?: uri.toString())
        }
    }

    fun saveProduct() {
        if (viewModel.validar(exigirImagen = true)) {
            onSaveProduct(
                NewAdminProduct(
                    name = name.trim(),
                    category = category,
                    price = price.toInt(),
                    stock = stock,
                    weight = weight,
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
                        "Crear Producto",
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
                            .weight(35f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            "Cancelar",
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Button(
                        onClick = { saveProduct() },
                        modifier = Modifier
                            .weight(65f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Guardar producto",
                            maxLines = 1,
                            softWrap = false
                        )
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
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Surface(
                color = colors.surfaceContainerHigh,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Storage,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(30.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "ID autogenerado: #PROD-2024-009",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )

                        Text(
                            "Sincronización activa con Room local y backend Spring Boot",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CreateFieldLabel("Nombre comercial *")

                    Text(
                        "${name.length}/60",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = viewModel::cambiarNombre,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Ej. Nueces Chandler Peladas 500 g")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = estado.nombre.error != null,
                    supportingText = textoDeError(estado.nombre.error),
                    colors = createFieldColors()
                )

                Text(
                    "Indica el tipo de fruto, variedad y gramaje visible para el cliente chileno.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreateFieldLabel("Categoría *")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CreateCategoryButton(
                        modifier = Modifier.weight(1f),
                        text = "Frutos secos",
                        icon = Icons.Default.Eco,
                        selected = category == "Frutos secos",
                        onClick = { viewModel.cambiarCategoria("Frutos secos") }
                    )

                    CreateCategoryButton(
                        modifier = Modifier.weight(1f),
                        text = "Semillas",
                        icon = Icons.Default.Spa,
                        selected = category == "Semillas",
                        onClick = { viewModel.cambiarCategoria("Semillas") }
                    )

                    CreateCategoryButton(
                        modifier = Modifier.weight(1f),
                        text = "Deshidratados",
                        icon = Icons.Default.WbSunny,
                        selected = category == "Deshidratados",
                        onClick = { viewModel.cambiarCategoria("Deshidratados") }
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CreateFieldLabel("Precio unitario (CLP) *")

                OutlinedTextField(
                    value = price,
                    onValueChange = viewModel::cambiarPrecio,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Text(
                            "$",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    placeholder = {
                        Text("Ej. 4.990")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = estado.precio.error != null,
                    supportingText = textoDeError(estado.precio.error),
                    colors = createFieldColors()
                )

                Text(
                    "No se permiten precios negativos ni cero.",
                    fontSize = 12.sp,
                    color = if (estado.precio.error != null) {
                        colors.error
                    } else {
                        colors.onSurfaceVariant
                    }
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CreateFieldLabel("Stock inicial (Unidades) *")

                Surface(
                    color = colors.surfaceContainerLowest,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.disminuirStock()
                            }
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Disminuir stock"
                            )
                        }

                        Text(
                            stock.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        IconButton(
                            onClick = { viewModel.aumentarStock() }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar stock",
                                tint = colors.primary
                            )
                        }
                    }
                }

                Text(
                    "Valor entero mayor o igual a 0. Genera alerta en bodega si es menor a 5.",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )

                estado.errorStock?.let { FilaError(it) }

                if (stock < 5) {
                    Surface(
                        color = colors.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Stock bajo: este producto requerirá atención en inventario.",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            color = colors.onErrorContainer
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreateFieldLabel("Formato / Peso neto")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("250 g", "500 g", "1 kg").forEach { option ->
                        FilterChip(
                            selected = weight == option,
                            onClick = { viewModel.cambiarPeso(option) },
                            label = { Text(option) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary,
                                selectedLabelColor = colors.onPrimary,
                                containerColor = colors.surfaceContainer
                            ),
                            border = null
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreateFieldLabel("Imagen referencial *")

                estado.errorImagen?.let { FilaError(it) }

                Surface(
                    color = colors.surfaceContainerLowest,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.surfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Imagen del producto",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
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
                        }

                        Text(
                            if (imageUri == null) {
                                "Imagen de producto"
                            } else {
                                "Imagen seleccionada"
                            },
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface
                        )

                        Text(
                            if (imageUri == null) {
                                "380 KB • Formato optimizado"
                            } else {
                                "Archivo seleccionado desde el dispositivo"
                            },
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )

                        OutlinedButton(onClick = onTakePhotoClick) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Tomar foto con la cámara")
                        }

                        OutlinedButton(
                            onClick = {
                                imagePicker.launch("image/*")
                            }
                        ) {
                            Icon(
                                Icons.Default.FileUpload,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Cambiar imagen")
                        }
                    }
                }
            }

            Surface(
                color = colors.surfaceContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Comprobación de integridad",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = colors.onSurface
                    )

                    CreateValidationRow(
                        "Campos obligatorios completos",
                        validName
                    )

                    CreateValidationRow(
                        "Precio válido en CLP (> $0)",
                        validPrice
                    )

                    CreateValidationRow(
                        "Stock inicial válido (≥ 0 unidades)",
                        validStock
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun CreateCategoryButton(
    modifier: Modifier,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .height(76.dp)
            .clickable(onClick = onClick),
        color = if (selected) colors.primary else colors.surfaceContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (selected) colors.onPrimary else colors.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) colors.onPrimary else colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CreateValidationRow(
    label: String,
    valid: Boolean
) {
    val colors = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            if (valid) Icons.Default.CheckCircle
            else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (valid) colors.primary else colors.onSurfaceVariant
        )

        Text(
            label,
            fontSize = 13.sp,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun createFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.outlineVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.outlineVariant
    )