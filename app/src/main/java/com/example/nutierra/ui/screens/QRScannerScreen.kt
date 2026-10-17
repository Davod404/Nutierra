
package com.example.nutierra.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private val qrGreen = Color(0xFF13522F)
private val qrLight = Color(0xFFF5FBF3)
private val qrSoft = Color(0xFFEAF0E8)
private val qrText = Color(0xFF171D18)
private val qrMuted = Color(0xFF404941)

private sealed class QRResult {
    data class Product(val id: Int, val code: String) : QRResult()
    data class Unknown(val code: String) : QRResult()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    onBack: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val context = LocalContext.current

    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var scanResult by remember {
        mutableStateOf<QRResult?>(null)
    }

    var torchEnabled by remember {
        mutableStateOf(false)
    }

    var camera by remember {
        mutableStateOf<Camera?>(null)
    }

    var cameraError by remember {
        mutableStateOf<String?>(null)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            cameraGranted = granted
        }

    LaunchedEffect(Unit) {
        if (!cameraGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(camera, torchEnabled) {
        try {
            camera?.cameraControl?.enableTorch(torchEnabled)
        } catch (_: Exception) {
            torchEnabled = false
        }
    }

    Scaffold(
        containerColor = qrLight,
        topBar = {
            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = qrLight,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = qrText
                        )
                    }

                    Text(
                        "Escanear QR",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = qrText
                    )

                    Icon(
                        Icons.Default.Eco,
                        contentDescription = "Nutierra",
                        tint = qrGreen,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 24.dp,
                            bottomEnd = 24.dp
                        )
                    )
                    .background(Color(0xFF252C27)),
                contentAlignment = Alignment.Center
            ) {
                if (cameraGranted) {
                    QRCameraPreview(
                        enabled = scanResult == null,
                        onCameraReady = {
                            camera = it
                            cameraError = null
                        },
                        onCameraError = {
                            cameraError = it
                        },
                        onQRCode = { code ->
                            val match = Regex(
                                "^NUTIERRA:([1-9][0-9]*)$"
                            ).matchEntire(code.trim())

                            val id = match
                                ?.groupValues
                                ?.getOrNull(1)
                                ?.toIntOrNull()

                            scanResult = if (id == 1) {
                                QRResult.Product(id, code)
                            } else {
                                QRResult.Unknown(code)
                            }

                            torchEnabled = false
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.NoPhotography,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            "Se necesita permiso de cámara",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        ) {
                            Text("Solicitar permiso")
                        }
                    }
                }

                if (cameraGranted) {
                    // Controles superiores
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xDD252C27),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = Color(0xFF96D5A6),
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    when (scanResult) {
                                        null -> "Buscando código..."
                                        is QRResult.Product ->
                                            "Código identificado"
                                        is QRResult.Unknown ->
                                            "Sin coincidencia"
                                    },
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                torchEnabled = !torchEnabled
                            },
                            enabled = camera != null,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                if (torchEnabled) {
                                    Icons.Default.FlashOn
                                } else {
                                    Icons.Default.FlashOff
                                },
                                contentDescription = "Linterna"
                            )
                        }
                    }

                    // Marco central del QR
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(
                                width = 4.dp,
                                color = Color(0xFF96D5A6),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        color = Color(0xDD252C27),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ubica el código QR dentro del recuadro " +
                                    "para ver detalles del producto",
                            modifier = Modifier.padding(12.dp),
                            color = Color.White,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (cameraError != null) {
                Text(
                    cameraError.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = qrGreen,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    if (cameraGranted) {
                        "Permiso Cámara: Concedido"
                    } else {
                        "Permiso Cámara: Denegado"
                    },
                    fontSize = 12.sp,
                    color = qrMuted
                )
            }

            when (val result = scanResult) {
                is QRResult.Product -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = qrGreen
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    "Código detectado: ${result.code}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            HorizontalDivider()

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = qrGreen,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text(
                                        "Frutos secos",
                                        fontSize = 12.sp,
                                        color = qrMuted
                                    )

                                    Text(
                                        "Almendras Naturales 250 g",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )

                                    Text(
                                        "$4.990 CLP",
                                        fontWeight = FontWeight.Bold,
                                        color = qrGreen
                                    )
                                }
                            }

                            Text(
                                "Producto de demostración. " +
                                        "Disponibilidad pendiente de conectar al catálogo.",
                                fontSize = 12.sp,
                                color = qrMuted
                            )

                            Button(
                                onClick = {
                                    onProductClick(result.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = qrGreen
                                )
                            ) {
                                Text("Ver ficha de producto")
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    scanResult = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Escanear otro producto")
                            }
                        }
                    }
                }

                is QRResult.Unknown -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                "Código no registrado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Text(
                                "El código escaneado no corresponde " +
                                        "a un producto reconocido por esta versión.",
                                color = qrMuted,
                                fontSize = 13.sp
                            )

                            Text(
                                "Código: ${result.code.take(100)}",
                                fontSize = 12.sp,
                                color = qrMuted
                            )

                            Button(
                                onClick = {
                                    scanResult = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = qrGreen
                                )
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                null -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = qrSoft,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = qrGreen
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                "Puedes escanear códigos QR desde " +
                                        "bolsas, cajas o exhibidores de Nutierra.",
                                fontSize = 12.sp,
                                color = qrMuted
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QRCameraPreview(
    enabled: Boolean,
    onCameraReady: (Camera) -> Unit,
    onCameraError: (String) -> Unit,
    onQRCode: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnQRCode by rememberUpdatedState(onQRCode)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val currentOnCameraError by rememberUpdatedState(onCameraError)

    val scanningEnabled = rememberUpdatedState(enabled)

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner, enabled) {
        val executor = Executors.newSingleThreadExecutor()

        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(context)

        var provider: ProcessCameraProvider? = null
        var active = true
        var processing = false
        var delivered = false

        val listener = Runnable {
            if (!active) return@Runnable

            try {
                val cameraProvider = cameraProviderFuture.get()
                if (!active) return@Runnable

                provider = cameraProvider

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider =
                            previewView.surfaceProvider
                    }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

                analysis.setAnalyzer(executor) { imageProxy ->
                    if (
                        !scanningEnabled.value ||
                        delivered ||
                        processing
                    ) {
                        imageProxy.close()
                    } else {
                        val mediaImage = imageProxy.image

                        if (mediaImage == null) {
                            imageProxy.close()
                        } else {
                            processing = true

                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    val value = barcodes
                                        .firstOrNull()
                                        ?.rawValue

                                    if (
                                        active &&
                                        scanningEnabled.value &&
                                        !delivered &&
                                        !value.isNullOrBlank()
                                    ) {
                                        delivered = true
                                        currentOnQRCode(value)
                                    }
                                }
                                .addOnFailureListener {
                                    // Se mantiene el escaneo activo.
                                }
                                .addOnCompleteListener {
                                    processing = false
                                    imageProxy.close()
                                }
                        }
                    }
                }

                val cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()

                val boundCamera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis
                )

                currentOnCameraReady(boundCamera)

            } catch (e: Exception) {
                if (active) {
                    currentOnCameraError(
                        "No se pudo iniciar la cámara: " +
                                (e.message ?: "Error desconocido")
                    )
                }
            }
        }

        cameraProviderFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            active = false
            provider?.unbindAll()
            executor.shutdown()
            scanner.close()
        }
    }

    key(enabled) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
