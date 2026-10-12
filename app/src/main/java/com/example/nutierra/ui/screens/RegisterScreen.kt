package com.example.nutierra.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }

    val passwordsMatch = password.isNotEmpty() &&
            confirmPassword.isNotEmpty() &&
            password == confirmPassword

    val fieldShape = RoundedCornerShape(12.dp)

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

            // ENCABEZADO DE REGISTRO
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

            // NOMBRE Y APELLIDO
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
                        onValueChange = { firstName = it },
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
                        onValueChange = { lastName = it },
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

            // CORREO ELECTRÓNICO
            Text(
                "Correo electrónico *",
                style = typography.labelMedium,
                color = colors.onSurface
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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

            // CONTRASEÑA
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
                onValueChange = { password = it },
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

            // CONFIRMAR CONTRASEÑA
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
                onValueChange = { confirmPassword = it },
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

            // TÉRMINOS Y CONDICIONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
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
                            acceptedTerms = !acceptedTerms
                        }
                )
            }

            Spacer(Modifier.height(20.dp))

            // MENSAJE DE VALIDACIÓN
            errorMessage?.let {
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

                Spacer(Modifier.height(12.dp))
            }

            if (successMessage) {
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

            // BOTÓN CREAR CUENTA
            Button(
                onClick = {
                    errorMessage = null
                    successMessage = false

                    when {
                        firstName.isBlank() ||
                                lastName.isBlank() ||
                                email.isBlank() ||
                                password.isBlank() ||
                                confirmPassword.isBlank() -> {
                            errorMessage = "Completa todos los campos obligatorios."
                        }

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email.trim()).matches() -> {
                            errorMessage = "Ingresa un correo electrónico válido."
                        }

                        password.length < 8 -> {
                            errorMessage = "La contraseña debe tener al menos 8 caracteres."
                        }

                        password != confirmPassword -> {
                            errorMessage = "Las contraseñas ingresadas no coinciden."
                        }

                        !acceptedTerms -> {
                            errorMessage = "Debes aceptar las condiciones de servicio."
                        }

                        else -> {
                            successMessage = true
                            onRegisterSuccess()
                        }
                    }
                },
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

            // MENSAJE DE SEGURIDAD
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

            // ENLACE A LOGIN
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
