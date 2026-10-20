package com.example.nutierra.ui.screens

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.ui.viewmodel.ProductoFormViewModel
import com.example.nutierra.data.local.entity.ProductoEntity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val editGreen = Color(0xFF13522F)
private val editBackground = Color(0xFFF5FBF3)
private val editSoft = Color(0xFFEAF0E8)
private val editMuted = Color(0xFF404941)
private val editError = Color(0xFFBA1A1A)

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
    // Estado y validaciones viven en ProductoFormViewModel.
    // Cuando el producto llega desde SQLite, el formulario se rellena con sus datos.
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(producto) {
        producto?.let { viewModel.cargarProducto(it) }
    }

    val productName = estado.nombre.valor
    val category = estado.categoria
    val price = estado.precio.valor
    // Solo se muestra como "nueva" si es distinta de la imagen guardada
    val imageUri = estado.imagenUri?.takeIf { it != producto?.imagenUri }

    var showSaveBanner by remember(productId) {
        mutableStateOf(false)
    }


    // Validez en vivo para el resumen inferior
    val validName = estado.nombreValido
    val validPrice = estado.precioValido

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.cambiarImagen(uri.toString())
        }
    }

    fun saveChanges() {
        // El ViewModel revalida todo; al editar se conserva la imagen anterior si no hay nueva
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
        containerColor = editBackground,
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
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { saveChanges() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = editGreen
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
                    containerColor = editBackground
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Aviso de guardado
            if (showSaveBanner) {
                Surface(
                    color = editSoft,
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
                            tint = editGreen
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Modificaciones registradas",
                                color = editGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Los cambios se enviaron al gestor de productos.",
                                color = editMuted,
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

            // SKU y estado
            Surface(
                color = Color.White,
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
                                "SKU: ALM-250",
                                fontSize = 12.sp,
                                color = editMuted,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Creado el 14 Oct, 2024",
                                fontSize = 11.sp,
                                color = editMuted
                            )
                        }

                        Surface(
                            color = editSoft,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "● Activo",
                                modifier = Modifier.padding(
                                    horizontal = 9.dp,
                                    vertical = 6.dp
                                ),
                                color = editGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        "Ficha Maestra del Artículo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Imagen del producto
            Surface(
                color = Color.White,
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
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Resolución óptima 1:1",
                            fontSize = 11.sp,
                            color = editMuted
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
                                    editSoft,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Imagen del producto",
                                tint = editGreen,
                                modifier = Modifier.size(42.dp)
                            )
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
                                color = editMuted
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
                                        tint = editMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Nombre comercial
            Surface(
                color = Color.White,
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
                        color = editMuted
                    )

                }
            }

            // Categoría
            Surface(
                color = Color.White,
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
                                    editGreen
                                } else {
                                    editSoft
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
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        option,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selected) {
                                            Color.White
                                        } else {
                                            editMuted
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Precio unitario
            Surface(
                color = Color.White,
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
                                color = editMuted
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
                            "IVA incluido (19%)",
                            fontSize = 12.sp,
                            color = editMuted
                        )
                        Text(
                            "Margen aprox: 42%",
                            fontSize = 12.sp,
                            color = editGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }
            }

            // Control de inventario
            Surface(
                color = editSoft,
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
                            tint = editGreen
                        )

                        Text(
                            "Control Centralizado de Bodega",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        "Stock disponible: 24 unidades",
                        fontWeight = FontWeight.Bold,
                        color = editGreen
                    )

                    Text(
                        "El inventario físico (24 unidades actuales) se gestiona de forma centralizada desde el módulo Inventario para mantener trazabilidad con la base de datos de lotes y vencimientos.",
                        fontSize = 13.sp,
                        color = editMuted,
                        lineHeight = 19.sp
                    )

                    OutlinedButton(
                        onClick = onInventoryClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Ir a Control de Inventario",
                            color = editGreen
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = editGreen
                        )
                    }
                }
            }

            // Validación
            Surface(
                color = editSoft,
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
                            editGreen
                        } else {
                            editError
                        }
                    )

                    Text(
                        if (validName && validPrice) {
                            "Todos los datos son válidos y cumplen la normativa de etiquetado nacional."
                        } else {
                            "Revisa los campos obligatorios antes de guardar."
                        },
                        fontSize = 12.sp,
                        color = editMuted
                    )
                }
            }

            // Acciones inferiores
            Button(
                onClick = { saveChanges() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = editGreen
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Guardar modificaciones",
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
                Text("Descartar cambios")
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
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun editTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedContainerColor = editBackground,
        unfocusedContainerColor = editBackground,
        focusedBorderColor = editGreen,
        unfocusedBorderColor = Color.Transparent
    )
