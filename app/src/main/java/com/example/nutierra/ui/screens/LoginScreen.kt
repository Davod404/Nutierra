package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.ui.viewmodel.LoginViewModel
import com.example.nutierra.ui.components.textoDeError
import com.example.nutierra.data.local.entity.Roles

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit = {},
    onClientLogin: () -> Unit = {},
    onAdminLogin: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    val email = estado.correo.valor
    val password = estado.contrasena.valor
    val isLoading = estado.cargando
    val errorMessage = estado.errorGeneral
    val detectedRole = estado.rolDetectado

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(estado.rolAutenticado) {
        when (estado.rolAutenticado) {
            Roles.ADMIN -> {
                viewModel.navegacionRealizada()
                onAdminLogin()
            }
            Roles.CLIENTE -> {
                viewModel.navegacionRealizada()
                onClientLogin()
            }
        }
    }

    Scaffold(
        containerColor = colors.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(80.dp)
                    .background(
                        colors.surfaceContainer,
                        CircleShape
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
                        tint = colors.onPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(colors.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Spa,
                        contentDescription = null,
                        tint = colors.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

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
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        "Cosecha & Pureza Chilena",
                        style = typography.labelMedium,
                        color = colors.primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Bienvenido a Nutierra",
                style = typography.headlineLarge,
                color = colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Ingresa a tu cuenta para continuar",
                style = typography.bodyMedium,
                color = colors.outline,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                colors.primary,
                                colors.primaryContainer,
                                colors.secondaryContainer
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .widthIn(max = 240.dp)
                ) {
                    Text(
                        "Comercio Directo",
                        style = typography.labelMedium,
                        color = colors.onPrimary.copy(alpha = 0.8f)
                    )

                    Text(
                        "Frutos secos frescos y seleccionados a tu puerta",
                        style = typography.labelLarge,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                colors.surfaceContainerHighest,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Badge,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Acceso de demostración MVP",
                            style = typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.secondary
                        )

                        Text(
                            "Selecciona una cuenta de prueba:",
                            style = typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )

                        Text(
                            text = "cliente@nutierra.cl",
                            style = typography.labelMedium,
                            color = colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.usarCuentaDemo(
                                    "cliente@nutierra.cl",
                                    "Semillas2025!"
                                )
                            }
                        )

                        Text(
                            text = "admin@nutierra.cl",
                            style = typography.labelMedium,
                            color = colors.primary,
                            modifier = Modifier.clickable {
                                viewModel.usarCuentaDemo(
                                    "admin@nutierra.cl",
                                    "AdminMaster2025!"
                                )
                            }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                "El sistema detecta el rol automáticamente",
                                style = typography.labelMedium,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Correo electrónico",
                    style = typography.labelMedium,
                    color = colors.onSurfaceVariant
                )

                if (detectedRole != null) {
                    Surface(
                        color = if (detectedRole == "Administrador")
                            colors.secondaryContainer
                        else
                            colors.primaryFixed,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "MODO ${detectedRole.uppercase()}",
                            style = typography.labelMedium,
                            color = if (detectedRole == "Administrador")
                                colors.onSecondaryContainer
                            else
                                colors.onPrimaryFixed,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = viewModel::cambiarCorreo,
                isError = estado.correo.error != null,
                supportingText = textoDeError(estado.correo.error),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ejemplo@correo.cl") },
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
                shape = RoundedCornerShape(12.dp),
                colors = loginFieldColors(),
                textStyle = typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Contraseña",
                style = typography.labelMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

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
                            contentDescription = "Mostrar u ocultar contraseña",
                            tint = colors.outline
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
                shape = RoundedCornerShape(12.dp),
                colors = loginFieldColors(),
                textStyle = typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            // ← NUEVO: Checkbox "Recordar sesión"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = estado.recordarSesion,
                    onCheckedChange = viewModel::cambiarRecordarSesion,
                    colors = CheckboxDefaults.colors(checkedColor = colors.primary)
                )
                Text(
                    "Recordar sesión",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    val message = errorMessage ?: ""
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.errorContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = colors.error
                            )

                            Text(
                                text = message,
                                style = typography.labelMedium,
                                color = colors.onErrorContainer
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.SyncAlt,
                    contentDescription = null,
                    tint = colors.outline,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    "El sistema identificará tu rol automáticamente (Cliente o Administrador)",
                    style = typography.labelMedium,
                    color = colors.outline,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = viewModel::iniciarSesion,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colors.onPrimary
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Verificando...")
                } else {
                    Text(
                        "Iniciar sesión",
                        style = typography.labelLarge
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿No tienes una cuenta?",
                    style = typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Regístrate aquí",
                        style = typography.labelLarge,
                        color = colors.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Spa,
                    contentDescription = null,
                    tint = colors.outline,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    "Frutos Secos y Semillas • Calidad Valle Central",
                    style = typography.labelMedium,
                    color = colors.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun loginFieldColors(): TextFieldColors {
    val colors = MaterialTheme.colorScheme

    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colors.surfaceBright,
        unfocusedContainerColor = colors.surfaceContainerLowest,
        focusedBorderColor = colors.primary,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = colors.primary
    )
}