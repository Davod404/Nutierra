package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import java.io.File

@Composable
fun ProductCameraScreen(
    onBack: () -> Unit,
    onPhotoSelected: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { mutableStateOf<PreviewView?>(null) }
    val cameraPreview = remember { mutableStateOf<Preview?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var frontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var showGrid by remember { mutableStateOf(true) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val file = File.createTempFile(
                    "nutierra_gallery_",
                    ".jpg",
                    context.filesDir
                )

                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IllegalStateException("No se pudo leer la imagen")

                capturedUri = Uri.fromFile(file)
                errorMessage = null
            } catch (_: Exception) {
                errorMessage = "No se pudo cargar la fotografía."
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(cameraGranted, frontCamera) {
        if (!cameraGranted) {
            onDispose { }
        } else {
            val executor = ContextCompat.getMainExecutor(context)
            val providerFuture = ProcessCameraProvider.getInstance(context)
            var provider: ProcessCameraProvider? = null
            var active = true

            val listener = Runnable {
                if (!active) return@Runnable
                try {
                    val cameraProvider = providerFuture.get()
                    provider = cameraProvider

                    val selector = if (frontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(flashMode)
                        .build()

                    val preview = Preview.Builder().build()

                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        capture
                    )

                    imageCapture = capture
                    cameraPreview.value = preview
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = "No se pudo iniciar la cámara: ${e.message}"
                }
            }

            providerFuture.addListener(listener, executor)

            onDispose {
                active = false
                provider?.unbindAll()
                imageCapture = null
                cameraPreview.value = null
            }
        }
    }

    LaunchedEffect(flashMode, imageCapture) {
        imageCapture?.flashMode = flashMode
    }

    fun takePhoto() {
        val capture = imageCapture ?: return
        if (isCapturing) return

        isCapturing = true

        val file = File.createTempFile(
            "nutierra_product_",
            ".jpg",
            context.filesDir
        )

        val options = ImageCapture.OutputFileOptions.Builder(file).build()

        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    capturedUri = Uri.fromFile(file)
                    isCapturing = false
                    errorMessage = null
                }

                override fun onError(
                    exception: androidx.camera.core.ImageCaptureException
                ) {
                    isCapturing = false
                    errorMessage = "No se pudo tomar la fotografía."
                    file.delete()
                }
            }
        )
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
                            contentDescription = "Volver"
                        )
                    }

                    Text(
                        "Fotografía de producto",
                        modifier = Modifier.weight(1f),
                        fontSize = 19.sp,
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
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceContainer)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (capturedUri != null) {
                        "Fotografía capturada"
                    } else {
                        if (frontCamera) "Cámara frontal" else "Cámara trasera"
                    },
                    fontSize = 12.sp,
                    color = colors.primary
                )

                Text(
                    if (cameraGranted) "Permiso activo" else "Sin permiso",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF252C27)),
                contentAlignment = Alignment.Center
            ) {
                if (capturedUri != null) {
                    val bitmap = remember(capturedUri) {
                        try {
                            context.contentResolver
                                .openInputStream(capturedUri!!)
                                ?.use { stream ->
                                    android.graphics.BitmapFactory.decodeStream(stream)
                                }
                        } catch (_: Exception) {
                            null
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Fotografía capturada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else if (cameraGranted) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                previewView.value = this
                            }
                        },
                        update = { view ->
                            cameraPreview.value?.surfaceProvider = view.surfaceProvider
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (showGrid) {
                        CameraGridOverlay()
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp),
                        color = Color(0xCC252C27),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "CATÁLOGO 1:1",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(
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
                            "Autoriza el acceso a la cámara",
                            color = Color.White
                        )

                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            }
                        ) {
                            Text("Dar permiso")
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    errorMessage.orEmpty(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            if (capturedUri == null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = colors.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = colors.primary
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            "Centra la bolsa o frasco dentro de las guías " +
                                    "para conseguir una buena fotografía del producto.",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            flashMode = when (flashMode) {
                                ImageCapture.FLASH_MODE_AUTO ->
                                    ImageCapture.FLASH_MODE_ON
                                ImageCapture.FLASH_MODE_ON ->
                                    ImageCapture.FLASH_MODE_OFF
                                else -> ImageCapture.FLASH_MODE_AUTO
                            }
                        }
                    ) {
                        Icon(
                            when (flashMode) {
                                ImageCapture.FLASH_MODE_ON ->
                                    Icons.Default.FlashOn
                                ImageCapture.FLASH_MODE_OFF ->
                                    Icons.Default.FlashOff
                                else -> Icons.Default.FlashAuto
                            },
                            contentDescription = "Cambiar flash",
                            tint = colors.primary
                        )
                    }

                    IconButton(
                        onClick = { showGrid = !showGrid }
                    ) {
                        Icon(
                            Icons.Default.GridOn,
                            contentDescription = "Cuadrícula",
                            tint = colors.primary
                        )
                    }

                    Icon(
                        Icons.Default.HdrAuto,
                        contentDescription = "Ajuste automático",
                        tint = colors.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilledIconButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier.size(52.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = colors.surfaceContainer,
                                contentColor = colors.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = "Galería"
                            )
                        }

                        Text("Galería", fontSize = 11.sp)
                    }

                    FilledIconButton(
                        onClick = { takePhoto() },
                        enabled = cameraGranted &&
                                imageCapture != null &&
                                !isCapturing,
                        modifier = Modifier.size(76.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Tomar fotografía",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FilledIconButton(
                            onClick = {
                                frontCamera = !frontCamera
                            },
                            modifier = Modifier.size(52.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = colors.surfaceContainer,
                                contentColor = colors.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Cameraswitch,
                                contentDescription = "Cambiar cámara"
                            )
                        }

                        Text("Rotar", fontSize = 11.sp)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Confirmar foto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )

                    Text(
                        "Revisa la imagen antes de utilizarla en el catálogo.",
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            capturedUri?.let {
                                onPhotoSelected(it.toString())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Usar fotografía")
                    }

                    OutlinedButton(
                        onClick = {
                            capturedUri = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null
                        )

                        Spacer(Modifier.width(8.dp))

                        Text("Repetir foto")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CameraGridOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .border(
                2.dp,
                Color(0xFF96D5A6),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.4f))
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.4f))
            )
            Spacer(Modifier.weight(1f))
        }

        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.4f))
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.4f))
            )
            Spacer(Modifier.weight(1f))
        }
    }
}
