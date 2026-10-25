package com.example.nutierra.ui.screens

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val balanceo = rememberInfiniteTransition(label = "balanceo")
    val logoEscala = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val textosProgreso = remember { Animatable(0f) }
    val cargaProgreso = remember { Animatable(0f) }
    val salidaAlpha = remember { Animatable(1f) }
    var messageIndex by remember { mutableIntStateOf(0) }

    val messages = listOf(
        "Cargando catálogo fresco...",
        "Verificando cosechas del valle...",
        "Preparando tus favoritos..."
    )

    val rotacionHoja by balanceo.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacionHoja"
    )
    
    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(durationMillis = 500)) }
        logoEscala.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        launch { textosProgreso.animateTo(1f, tween(durationMillis = 500)) }
        delay(250.milliseconds)
        cargaProgreso.animateTo(1f, tween(durationMillis = 400))

        repeat(3) {
            delay(800.milliseconds)
            messageIndex = (messageIndex + 1) % messages.size
        }

        salidaAlpha.animateTo(0f, tween(durationMillis = 350))
        onFinished()
    }

    Scaffold(
        containerColor = colors.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .graphicsLayer { alpha = salidaAlpha.value }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))

                Surface(
                    color = colors.surfaceContainer,
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
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Text(
                            "Cosecha & Selección Artesanal • Chile",
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Surface(
                    modifier = Modifier
                        .size(112.dp)
                        .graphicsLayer {
                            scaleX = logoEscala.value
                            scaleY = logoEscala.value
                            alpha = logoAlpha.value
                        },
                    color = colors.surfaceContainerLowest ,
                    shape = RoundedCornerShape(28.dp),
                    shadowElevation = 5.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(96.dp),
                            color = colors.surfaceContainerLow,
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = "Logo OYL Frutos Secos",
                                    tint = colors.primary,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .rotate(rotacionHoja)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        alpha = textosProgreso.value
                        translationY = (1f - textosProgreso.value) * 40.dp.toPx()
                    }
                ) {
                Text(
                    text = "OYL Frutos Secos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Naturalmente cerca de ti",
                    fontSize = 16.sp,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                }

                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        alpha = cargaProgreso.value
                        translationY = (1f - cargaProgreso.value) * 24.dp.toPx()
                    }
                ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = colors.primaryContainer,
                    trackColor = colors.surfaceContainerHighest,
                    strokeWidth = 3.dp
                )

                Spacer(Modifier.height(16.dp))

                AnimatedContent(
                    targetState = messageIndex,
                    transitionSpec = {
                        (fadeIn(tween(300)) + slideInVertically(tween(300)) { it }) togetherWith
                                (fadeOut(tween(300)) + slideOutVertically(tween(300)) { -it })
                    },
                    label = "mensajeCarga"
                ) { indice ->
                    Text(
                        text = messages[indice],
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "v2.4.0",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}