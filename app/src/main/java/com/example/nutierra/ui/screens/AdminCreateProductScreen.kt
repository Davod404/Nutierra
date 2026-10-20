package com.example.nutierra.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.components.FilaError
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.ui.viewmodel.ProductoFormViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val createGreen = Color(0xFF13522F)
private val createBackground = Color(0xFFF5FBF3)
private val createSoft = Color(0xFFEAF0E8)
private val createMuted = Color(0xFF404941)
private val createError = Color(0xFFBA1A1A)

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
    // Estado y validaciones viven en ProductoFormViewModel
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

    // Validez en vivo para la "Comprobación de integridad"
    val validName = estado.nombreValido
    val validPrice = estado.precioValido
    val validStock = estado.stockValido

    // Foto tomada con la cámara (recurso nativo) que vuelve desde ProductCameraScreen
    LaunchedEffect(fotoTomada) {
        if (fotoTomada != null) {
            viewModel.cambiarImagen(fotoTomada)
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.cambiarImagen(uri.toString())
        }
    }

    fun saveProduct() {
        // El ViewModel revalida todo; solo se guarda si no hay errores
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
        containerColor = createBackground,
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
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { saveProduct() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = createGreen
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Guardar")
                    }

                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = createBackground
                )
            )
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

            // Banner de identificación
            Surface(
                color = Color(0xFFE4EAE2),
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
                        tint = createGreen,
                        modifier = Modifier.size(30.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "ID autogenerado: #PROD-2024-009",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = createGreen
                        )

                        Text(
                            "Sincronización activa con Room local y backend Spring Boot",
                            fontSize = 12.sp,
                            color = createMuted
                        )
                    }
                }
            }

            // Nombre comercial
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
                        color = createMuted
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
                    color = createMuted
                )

            }

            // Categoría
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

            // Precio
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
                    placeholder = { Text("Ej. 4.990") },
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
                        createError
                    } else {
                        createMuted
                    }
                )
            }

            // Stock inicial
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CreateFieldLabel("Stock inicial (Unidades) *")

                Surface(
                    color = Color.White,
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
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = { viewModel.aumentarStock() }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar stock",
                                tint = createGreen
                            )
                        }
                    }
                }

                Text(
                    "Valor entero mayor o igual a 0. Genera alerta en bodega si es menor a 5.",
                    fontSize = 12.sp,
                    color = createMuted
                )

                estado.errorStock?.let { FilaError(it) }

                if (stock < 5) {
                    Surface(
                        color = Color(0xFFFFDAD6),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Stock bajo: este producto requerirá atención en inventario.",
                            modifier = Modifier.padding(10.dp),
                            fontSize = 12.sp,
                            color = createError
                        )
                    }
                }
            }

            // Formato / peso
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
                                selectedContainerColor = createGreen,
                                selectedLabelColor = Color.White,
                                containerColor = createSoft
                            ),
                            border = null
                        )
                    }
                }
            }

            // Imagen referencial
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreateFieldLabel("Imagen referencial *")

                estado.errorImagen?.let { FilaError(it) }

                Surface(
                    color = Color.White,
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
                                .background(
                                    createSoft,
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Imagen referencial",
                                modifier = Modifier.size(44.dp),
                                tint = createGreen
                            )
                        }

                        Text(
                            if (imageUri == null) {
                                "Imagen de producto"
                            } else {
                                "Imagen seleccionada"
                            },
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            if (imageUri == null) {
                                "380 KB • Formato optimizado"
                            } else {
                                "Archivo seleccionado desde el dispositivo"
                            },
                            fontSize = 12.sp,
                            color = createMuted
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

            // Comprobación
            Surface(
                color = createSoft,
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
                        fontSize = 16.sp
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

            // Botones inferiores
            Button(
                onClick = { saveProduct() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = createGreen
                )
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Guardar producto",
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text("Cancelar")
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
        color = Color(0xFF171D18)
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
    Surface(
        modifier = modifier
            .height(76.dp)
            .clickable(onClick = onClick),
        color = if (selected) createGreen else createSoft,
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
                tint = if (selected) Color.White else createMuted
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) Color.White else createMuted
            )
        }
    }
}

@Composable
private fun CreateValidationRow(
    label: String,
    valid: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            if (valid) Icons.Default.CheckCircle
            else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (valid) createGreen else createMuted
        )

        Text(
            label,
            fontSize = 13.sp,
            color = createMuted
        )
    }
}

@Composable
private fun createFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = createGreen,
        unfocusedBorderColor = Color.Transparent
    )
