package com.example.nutierra.ui.screens

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.Manifest
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private sealed class QRResult {
    data class Unknown(val code: String) : QRResult()
}

@Composable
fun QRScannerScreen(
    onBack: () -> Unit,
    onProductClick: (Int) -> Unit,
    existeProducto: (Int) -> Boolean = { true }
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

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

    var navegando by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
    }

    LaunchedEffect(Unit) {
        navegando = false
        scanResult = null
    }

    LaunchedEffect(camera, torchEnabled) {
        try {
            camera?.cameraControl?.enableTorch(torchEnabled)
        } catch (_: Exception) {
            torchEnabled = false
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = colors.background,
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
                            tint = colors.onSurface
                        )
                    }

                    Text(
                        "Escanear QR",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = "Nutierra",
                            tint = colors.onPrimary
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
                    .background(colors.inverseSurface),
                contentAlignment = Alignment.Center
            ) {
                if (cameraGranted) {
                    QRCameraPreview(
                        enabled = scanResult == null && !navegando,
                        onCameraReady = {
                            camera = it
                            cameraError = null
                        },
                        onCameraError = {
                            cameraError = it
                        },
                        onQRCode = { code ->
                            if (!navegando) {
                                val match = Regex(
                                    "^NUTIERRA:([1-9][0-9]*)$"
                                ).matchEntire(code.trim())

                                val id = match
                                    ?.groupValues
                                    ?.getOrNull(1)
                                    ?.toIntOrNull()

                                if (id != null && existeProducto(id)) {
                                    navegando = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onProductClick(id)
                                } else {
                                    scanResult = QRResult.Unknown(code)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
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
                            tint = colors.inverseOnSurface,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            "Se necesita permiso de cámara",
                            color = colors.inverseOnSurface,
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
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = colors.inverseSurface.copy(alpha = 0.87f),
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
                                    tint = colors.primaryFixedDim,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    when (scanResult) {
                                        null -> "Buscando código..."
                                        is QRResult.Unknown -> "Sin coincidencia"
                                    },
                                    fontSize = 12.sp,
                                    color = colors.inverseOnSurface
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                torchEnabled = !torchEnabled
                            },
                            enabled = camera != null && scanResult == null,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = colors.inverseOnSurface
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

                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(
                                width = 4.dp,
                                color = colors.primaryFixedDim,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = colors.inverseOnSurface.copy(alpha = 0.35f),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        color = colors.inverseSurface.copy(alpha = 0.87f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ubica el código QR dentro del recuadro " +
                                    "para ver detalles del producto",
                            modifier = Modifier.padding(12.dp),
                            color = colors.inverseOnSurface,
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
                    color = colors.error
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
                    tint = colors.primary,
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
                    color = colors.onSurfaceVariant
                )
            }

            when (val result = scanResult) {
                is QRResult.Unknown -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = colors.surfaceContainerLowest,
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
                                tint = colors.error
                            )

                            Text(
                                "Código no registrado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.onSurface
                            )

                            Text(
                                "El código escaneado no corresponde " +
                                        "a un producto reconocido por esta versión.",
                                color = colors.onSurfaceVariant,
                                fontSize = 13.sp
                            )

                            Text(
                                "Código: ${result.code.take(100)}",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )

                            TextButton(
                                onClick = { scanResult = null },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Escanear otro código",
                                    color = colors.primary
                                )
                            }
                        }
                    }
                }

                null -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = colors.surfaceContainer,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = colors.primary
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                "Puedes escanear códigos QR desde " +
                                        "bolsas, cajas o exhibidores de Nutierra.",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
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
    val marcoPx = with(LocalDensity.current) { 260.dp.toPx() }
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val currentOnCameraError by rememberUpdatedState(onCameraError)
    val scanningEnabled = rememberUpdatedState(enabled)

    var providerRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled) {
            providerRef?.unbindAll()
        }
    }

    DisposableEffect(lifecycleOwner, enabled) {
        val executor = Executors.newSingleThreadExecutor()

        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        var provider: ProcessCameraProvider? = null
        var active = true
        var processing = false
        var ultimoCodigo: String? = null

        val listener = Runnable {
            if (!active) return@Runnable

            try {
                val cameraProvider = cameraProviderFuture.get()

                provider = cameraProvider
                providerRef = cameraProvider

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(
                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                    )
                    .build()

                analysis.setAnalyzer(executor) { imageProxy ->
                    if (!scanningEnabled.value || processing) {
                        imageProxy.close()
                    } else {
                        val mediaImage = imageProxy.image

                        if (mediaImage == null) {
                            imageProxy.close()
                        } else {
                            processing = true

                            val rotacion = imageProxy.imageInfo.rotationDegrees
                            val anchoImagen = if (rotacion == 90 || rotacion == 270) imageProxy.height else imageProxy.width
                            val altoImagen = if (rotacion == 90 || rotacion == 270) imageProxy.width else imageProxy.height

                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    val value = codigoDentroDelMarco(
                                        barcodes = barcodes,
                                        anchoImagen = anchoImagen,
                                        altoImagen = altoImagen,
                                        anchoVista = previewView.width,
                                        altoVista = previewView.height,
                                        marcoPx = marcoPx
                                    )

                                    if (
                                        active &&
                                        scanningEnabled.value &&
                                        !value.isNullOrBlank() &&
                                        value != ultimoCodigo
                                    ) {
                                        ultimoCodigo = value
                                        currentOnQRCode(value)
                                    }
                                }
                                .addOnFailureListener { }
                                .addOnCompleteListener {
                                    processing = false
                                    imageProxy.close()
                                }
                        }
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()

                val boundCamera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis
                )

                currentOnCameraReady(boundCamera)

            } catch (e: Exception) {
                currentOnCameraError(
                    "No se pudo iniciar la cámara: " +
                            (e.message ?: "Error desconocido")
                )
            }
        }

        cameraProviderFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            active = false
            provider?.unbindAll()
            providerRef = null
            executor.shutdown()
            scanner.close()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

private fun codigoDentroDelMarco(
    barcodes: List<Barcode>,
    anchoImagen: Int,
    altoImagen: Int,
    anchoVista: Int,
    altoVista: Int,
    marcoPx: Float
): String? {
    if (anchoImagen <= 0 || altoImagen <= 0 || anchoVista <= 0 || altoVista <= 0) return null

    val escala = maxOf(anchoVista.toFloat() / anchoImagen, altoVista.toFloat() / altoImagen)
    val desplazamientoX = (anchoVista - anchoImagen * escala) / 2f
    val desplazamientoY = (altoVista - altoImagen * escala) / 2f

    val centroX = anchoVista / 2f
    val centroY = altoVista / 2f
    val mitadMarco = marcoPx / 2f

    return barcodes
        .mapNotNull { barcode ->
            val caja = barcode.boundingBox ?: return@mapNotNull null
            val valor = barcode.rawValue?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val x = caja.exactCenterX() * escala + desplazamientoX
            val y = caja.exactCenterY() * escala + desplazamientoY
            val dentro = x in (centroX - mitadMarco)..(centroX + mitadMarco) &&
                    y in (centroY - mitadMarco)..(centroY + mitadMarco)
            if (dentro) {
                val distancia = (x - centroX) * (x - centroX) + (y - centroY) * (y - centroY)
                valor to distancia
            } else {
                null
            }
        }
        .minByOrNull { it.second }
        ?.first
}