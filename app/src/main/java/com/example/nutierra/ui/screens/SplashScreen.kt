package com.example.nutierra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val splashGreen = Color(0xFF13522F)
private val splashBackground = Color(0xFFF5FBF3)
private val splashSoft = Color(0xFFEAF0E8)
private val splashMuted = Color(0xFF707971)

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    onBack: () -> Unit = {}
) {
    val messages = listOf(
        "Cargando catálogo fresco...",
        "Verificando cosechas del valle...",
        "Preparando tus favoritos..."
    )

    var messageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(3) {
            delay(900)
            messageIndex = (messageIndex + 1) % messages.size
        }

        delay(400)
        onFinished()
    }

    Scaffold(
        containerColor = splashBackground
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(48.dp))

                // Etiqueta superior
                Surface(
                    color = splashSoft,
                    shape = RoundedCornerShape(50.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = splashGreen,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            "Cosecha & Selección Artesanal • Chile",
                            fontSize = 11.sp,
                            color = Color(0xFF404941)
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Emblema botánico
                Surface(
                    modifier = Modifier.size(112.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 5.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(96.dp),
                            color = Color(0xFFEFF5ED),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = "Logo OYL Frutos Secos",
                                    tint = splashGreen,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "OYL Frutos Secos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = splashGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Naturalmente cerca de ti",
                    fontSize = 16.sp,
                    color = splashMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(1f))

                // Indicador de carga
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF2F6B45),
                    trackColor = Color(0xFFDEE4DC),
                    strokeWidth = 3.dp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = messages[messageIndex],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF404941),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "v2.4.0",
                    fontSize = 11.sp,
                    color = splashMuted
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
