
package com.example.nutierra.ui.screens

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.nutierra.data.local.entity.ProductoEntity
import com.example.nutierra.ui.components.formatoPesos
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
    onProductClick: (Int) -> Unit,
    // Indica si el id leído corresponde a un producto guardado en SQLite
    existeProducto: (Int) -> Boolean = { true },
    // Devuelve el producto guardado en SQLite para mostrar sus datos reales
    buscarProducto: (Int) -> ProductoEntity? = { null }
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

    // Vibración corta al reconocer un código nuevo
    val haptic = LocalHapticFeedback.current

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
                        // Escaneo continuo: la cámara sigue leyendo aunque ya haya un resultado
                        enabled = true,
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

                            scanResult = if (id != null && existeProducto(id)) {
                                QRResult.Product(id, code)
                            } else {
                                QRResult.Unknown(code)
                            }

                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                    val producto = buscarProducto(result.id)

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
                                        producto?.categoria ?: "Producto",
                                        fontSize = 12.sp,
                                        color = qrMuted
                                    )

                                    Text(
                                        producto?.nombre ?: "Producto #${result.id}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )

                                    Text(
                                        producto?.let { formatoPesos(it.precio) + " CLP" } ?: "",
                                        fontWeight = FontWeight.Bold,
                                        color = qrGreen
                                    )
                                }
                            }

                            Text(
                                when {
                                    producto == null -> "Producto registrado en el catálogo."
                                    producto.stock > 0 -> "Disponible: ${producto.stock} unidades en stock."
                                    else -> "Sin stock por ahora."
                                },
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

                            // Escaneo continuo: basta con apuntar a otro código
                            Text(
                                "Apunta la cámara a otro código para escanearlo.",
                                fontSize = 12.sp,
                                color = qrMuted,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
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

                            Text(
                                "Apunta la cámara a un código de producto Nutierra.",
                                fontSize = 12.sp,
                                color = qrMuted,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
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

    // Tamaño del encuadre verde (260.dp, centrado sobre la cámara) en píxeles.
    // Solo se aceptan códigos cuyo centro quede dentro de este cuadro.
    val marcoPx = with(LocalDensity.current) { 260.dp.toPx() }
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
        // Último código entregado: solo se avisa cuando se lee uno distinto
        var ultimoCodigo: String? = null

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
                        processing
                    ) {
                        imageProxy.close()
                    } else {
                        val mediaImage = imageProxy.image

                        if (mediaImage == null) {
                            imageProxy.close()
                        } else {
                            processing = true

                            // Tamaño de la imagen "derecha" (ya rotada) que analiza ML Kit
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

    // La vista de la cámara se crea una sola vez. Antes se recreaba con key(enabled)
    // reutilizando la misma PreviewView, que seguía agregada a la vista anterior,
    // y Android cerraba la app al detectar un QR.
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Devuelve el código QR cuyo centro está dentro del encuadre verde (centrado en la vista),
 * o null si ninguno lo está. Si hay varios dentro, elige el más cercano al centro.
 * Convierte las coordenadas de la imagen de la cámara a la pantalla igual que la vista
 * previa (escala FILL_CENTER: llena la vista y recorta los bordes).
 */
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
