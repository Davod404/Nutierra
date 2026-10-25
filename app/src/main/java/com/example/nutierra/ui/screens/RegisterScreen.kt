package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.RegisterViewModel
import com.example.nutierra.ui.components.textoDeError
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val fieldShape = RoundedCornerShape(12.dp)

    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    val firstName = estado.nombre.valor
    val lastName = estado.apellido.valor
    val email = estado.correo.valor
    val password = estado.contrasena.valor
    val confirmPassword = estado.confirmacion.valor
    val acceptedTerms = estado.aceptaTerminos
    val errorMessage = estado.errorGeneral ?: estado.errorTerminos
    val successMessage = estado.registroExitoso
    val passwordsMatch = estado.contrasenasCoinciden

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(estado.registroExitoso) {
        if (estado.registroExitoso) {
            delay(1200.milliseconds)
            viewModel.registroConsumido()
            onRegisterSuccess()
        }
    }

    Scaffold(
        containerColor = colors.surface,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = colors.onSurfaceVariant
                    )
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
                .padding(bottom = 32.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = fieldShape,
                color = colors.surfaceContainerLow,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = colors.surfaceContainerHighest,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = colors.primary
                            )

                            Text(
                                text = "ROL ASIGNADO: CLIENTE",
                                style = typography.labelMedium,
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Crear cuenta",
                        style = typography.headlineLarge,
                        color = colors.primary
                    )

                    Text(
                        text = "Únete a Nutierra y haz tus pedidos de nueces, almendras y semillas directamente a la tienda.",
                        style = typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                "Cosecha 100% natural",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                "Envíos a todo Chile",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Nombre *",
                        style = typography.labelMedium,
                        color = colors.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = viewModel::cambiarNombre,
                        isError = estado.nombre.error != null,
                        supportingText = textoDeError(estado.nombre.error),
                        placeholder = { Text("Francisca") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = fieldShape,
                        colors = registerFieldColors(),
                        textStyle = typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Apellido *",
                        style = typography.labelMedium,
                        color = colors.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = viewModel::cambiarApellido,
                        isError = estado.apellido.error != null,
                        supportingText = textoDeError(estado.apellido.error),
                        placeholder = { Text("Morales") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = fieldShape,
                        colors = registerFieldColors(),
                        textStyle = typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Correo electrónico *",
                style = typography.labelMedium,
                color = colors.onSurface
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = viewModel::cambiarCorreo,
                isError = estado.correo.error != null,
                supportingText = textoDeError(estado.correo.error),
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("francisca.morales@correo.cl")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                shape = fieldShape,
                colors = registerFieldColors(),
                textStyle = typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Contraseña *",
                    style = typography.labelMedium
                )

                Text(
                    "Mín. 8 caracteres",
                    style = typography.labelMedium,
                    color = colors.outline
                )
            }

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = viewModel::cambiarContrasena,
                isError = estado.contrasena.error != null,
                supportingText = textoDeError(estado.contrasena.error),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                shape = fieldShape,
                colors = registerFieldColors(),
                textStyle = typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Confirmar contraseña *",
                    style = typography.labelMedium
                )

                if (confirmPassword.isNotEmpty()) {
                    Text(
                        text = if (passwordsMatch)
                            "✓ Coinciden"
                        else
                            "No coinciden",
                        style = typography.labelMedium,
                        color = if (passwordsMatch)
                            colors.primary
                        else
                            colors.error
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = viewModel::cambiarConfirmacion,
                isError = estado.confirmacion.error != null,
                supportingText = textoDeError(estado.confirmacion.error),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(
                        Icons.Default.LockClock,
                        contentDescription = null,
                        tint = colors.outline
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                shape = fieldShape,
                colors = registerFieldColors(),
                textStyle = typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = viewModel::cambiarTerminos,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary
                    )
                )

                Text(
                    text = "Acepto las condiciones de servicio y la política de privacidad de Nutierra.",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp)
                        .clickable {
                            viewModel.cambiarTerminos(!acceptedTerms)
                        }
                )
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
              Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.errorContainer,
                    shape = fieldShape
                ) {
                    Text(
                        text = errorMessage ?: "",
                        modifier = Modifier.padding(12.dp),
                        color = colors.onErrorContainer,
                        style = typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(12.dp))
              }
            }

            AnimatedVisibility(
                visible = successMessage,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
              Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.primaryContainer,
                    shape = fieldShape
                ) {
                    Text(
                        text = "¡Cuenta creada con éxito!",
                        modifier = Modifier.padding(12.dp),
                        color = colors.onPrimaryContainer,
                        style = typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(12.dp))
              }
            }

            Button(
                onClick = viewModel::registrar,
                enabled = !estado.guardando && !successMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = fieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryContainer,
                    contentColor = colors.onPrimary
                )
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Crear cuenta",
                    style = typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.EnhancedEncryption,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    "Tus datos y compras están protegidos",
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Ya tienes una cuenta?",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                TextButton(onClick = onLoginClick) {
                    Text(
                        "Iniciar sesión",
                        style = typography.labelLarge,
                        color = colors.primary
                    )
                }
            }
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
        cursorColor = colors.primary
    )
}