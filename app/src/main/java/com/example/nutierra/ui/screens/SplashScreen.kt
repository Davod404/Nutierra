package com.example.nutierra.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // ===== ANIMACIONES =====
    // 1. Logo: aparece creciendo con un pequeño rebote (spring)
    val logoEscala = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    // 2. Títulos y 3. zona de carga: suben y aparecen de forma escalonada
    val textosProgreso = remember { Animatable(0f) }
    val cargaProgreso = remember { Animatable(0f) }
    // 5. Salida: toda la pantalla se desvanece antes de ir al login
    val salidaAlpha = remember { Animatable(1f) }

    // 4. La hoja del logo se balancea suavemente mientras carga (animación infinita)
    val balanceo = rememberInfiniteTransition(label = "balanceo")
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
        // Entrada del logo: opacidad y escala al mismo tiempo
        launch { logoAlpha.animateTo(1f, tween(durationMillis = 500)) }
        logoEscala.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Entrada escalonada de textos y carga
        launch { textosProgreso.animateTo(1f, tween(durationMillis = 500)) }
        delay(250)
        cargaProgreso.animateTo(1f, tween(durationMillis = 400))

        // Mensajes de carga (cambian con animación, ver AnimatedContent)
        repeat(3) {
            delay(800)
            messageIndex = (messageIndex + 1) % messages.size
        }

        // Salida suave y navegación
        salidaAlpha.animateTo(0f, tween(durationMillis = 350))
        onFinished()
    }

    Scaffold(
        containerColor = splashBackground
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
                    modifier = Modifier
                        .size(112.dp)
                        .graphicsLayer {
                            scaleX = logoEscala.value
                            scaleY = logoEscala.value
                            alpha = logoAlpha.value
                        },
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
                }

                Spacer(Modifier.weight(1f))

                // Indicador de carga
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.graphicsLayer {
                        alpha = cargaProgreso.value
                        translationY = (1f - cargaProgreso.value) * 24.dp.toPx()
                    }
                ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF2F6B45),
                    trackColor = Color(0xFFDEE4DC),
                    strokeWidth = 3.dp
                )

                Spacer(Modifier.height(16.dp))

                // Cada mensaje nuevo entra desde abajo y el anterior sale hacia arriba
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
                        color = Color(0xFF404941),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "v2.4.0",
                    fontSize = 11.sp,
                    color = splashMuted
                )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
