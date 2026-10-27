package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.PerfilViewModel
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.model.Validaciones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: PerfilViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val usuario by SesionManager.usuarioActual.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var nombre by remember(usuario) { mutableStateOf(usuario?.nombre.orEmpty()) }
    var apellido by remember(usuario) { mutableStateOf(usuario?.apellido.orEmpty()) }
    var correo by remember(usuario) { mutableStateOf(usuario?.correo.orEmpty()) }
    var telefono by remember(usuario) { mutableStateOf(usuario?.telefono.orEmpty()) }
    var direccion by remember(usuario) { mutableStateOf(usuario?.direccion.orEmpty()) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var apellidoError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }

    val fieldShape = RoundedCornerShape(12.dp)

    fun guardar() {
        nombreError = Validaciones.texto(nombre, "El nombre")
        apellidoError = Validaciones.texto(apellido, "El apellido")
        correoError = Validaciones.correo(correo)

        val valido = nombreError == null &&
                apellidoError == null &&
                correoError == null

        if (valido) {
            viewModel.actualizarPerfil(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                telefono = telefono,
                direccion = direccion,
                onExito = { onSaved() }
            )
        }
    }

    Scaffold(
        containerColor = colors.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar perfil",
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface
                )
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
                        shape = fieldShape
                    ) {
                        Text(
                            "Cancelar",
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Button(
                        onClick = { guardar() },
                        modifier = Modifier
                            .weight(65f)
                            .height(52.dp),
                        shape = fieldShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Guardar cambios",
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
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                color = colors.surfaceContainerLow,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Actualiza tus datos personales",
                        style = typography.titleMedium,
                        color = colors.primary
                    )
                    Text(
                        "Tu correo se usa para iniciar sesión. Si lo cambias, " +
                                "deberás usar el nuevo correo la próxima vez.",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; nombreError = null },
                isError = nombreError != null,
                supportingText = textoDeError(nombreError),
                placeholder = { Text("Francisca") },
                label = { Text("Nombre") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                colors = registerFieldColors()
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it; apellidoError = null },
                isError = apellidoError != null,
                supportingText = textoDeError(apellidoError),
                placeholder = { Text("Morales") },
                label = { Text("Apellido") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                colors = registerFieldColors()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono móvil") },
                placeholder = { Text("+56 9 1234 5678") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                colors = registerFieldColors()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = {
                    Text("Dirección de entrega")
                },
                placeholder = {
                    Text("Providencia, Santiago")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                colors = registerFieldColors()
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; correoError = null },
                isError = correoError != null,
                supportingText = textoDeError(correoError),
                placeholder = {
                    Text("francisca.morales@correo.cl")
                },
                label = {
                    Text("Correo electrónico")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                colors = registerFieldColors()
            )

            error?.let {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.errorContainer,
                    shape = fieldShape
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(12.dp),
                        color = colors.onErrorContainer,
                        style = typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun registerFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme

    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colors.surfaceBright,
        unfocusedContainerColor = colors.surfaceContainerLowest,
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = colors.primary,
        focusedPlaceholderColor = colors.outlineVariant,
        unfocusedPlaceholderColor = colors.outlineVariant
    )
}