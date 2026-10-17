
package com.example.nutierra.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

private val mapGreen = Color(0xFF13522F)
private val mapBackground = Color(0xFFF5FBF3)
private val mapSoft = Color(0xFFEAF0E8)
private val mapText = Color(0xFF171D18)
private val mapMuted = Color(0xFF404941)

// Coordenadas aproximadas de demostración.
// Sustituir por las coordenadas reales de la tienda.
private val storeLocation = LatLng(-33.4255, -70.6110)

private const val storeAddress =
    "Av. Providencia 1945, Local 4, Providencia, Santiago"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreMapScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            storeLocation,
            16f
        )
    }

    var satelliteMode by remember {
        mutableStateOf(false)
    }

    var locationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var locationMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showDirectionsGuide by remember {
        mutableStateOf(false)
    }

    val locationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun centerOnUser() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            locationMessage = "Necesitas autorizar el acceso a la ubicación."
        } else {
            locationGranted = true

            try {
                locationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            scope.launch {
                                cameraPositionState.animate(
                                    com.google.android.gms.maps.CameraUpdateFactory
                                        .newLatLngZoom(
                                            LatLng(
                                                location.latitude,
                                                location.longitude
                                            ),
                                            16f
                                        )
                                )
                            }
                        } else {
                            locationMessage =
                                "No hay una ubicación reciente disponible. " +
                                        "Activa el GPS e inténtalo nuevamente."
                        }
                    }
                    .addOnFailureListener {
                        locationMessage =
                            "No se pudo obtener tu ubicación."
                    }
            } catch (e: SecurityException) {
                locationGranted = false
                locationMessage =
                    "No se concedió permiso de ubicación."
            }
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            locationGranted = permissions.values.any { it }

            if (locationGranted) {
                centerOnUser()
            } else {
                locationMessage =
                    "Permiso denegado. Puedes seguir consultando el mapa."
            }
        }

    fun requestUserLocation() {
        if (locationGranted) {
            centerOnUser()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    fun openExternalUrl(url: String) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
            )
        } catch (e: Exception) {
            locationMessage =
                "No se encontró una aplicación para abrir el enlace."
        }
    }

    Scaffold(
        containerColor = mapBackground,
        topBar = {
            Surface(
                modifier = Modifier.statusBarsPadding(),
                color = mapBackground,
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
                            tint = mapText
                        )
                    }

                    Text(
                        "Nuestra ubicación",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = mapText
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(mapGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = "Nutierra",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // MAPA REAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = if (satelliteMode) {
                            MapType.SATELLITE
                        } else {
                            MapType.NORMAL
                        },
                        isMyLocationEnabled = locationGranted
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = true
                    )
                ) {
                    Marker(
                        state = MarkerState(
                            position = storeLocation
                        ),
                        title = "Tienda Nutierra",
                        snippet = "Ubicación de demostración",
                        icon = BitmapDescriptorFactory
                            .defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN
                            )
                    )
                }

                // Controles superiores
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(50.dp),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 9.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = mapGreen,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(5.dp))

                            Text(
                                if (locationGranted) {
                                    "Ubicación autorizada"
                                } else {
                                    "Ubicación no autorizada"
                                },
                                fontSize = 11.sp,
                                color = mapGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = {
                            satelliteMode = !satelliteMode
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White,
                            contentColor = mapGreen
                        )
                    ) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = "Cambiar tipo de mapa"
                        )
                    }
                }

                // Controles inferiores
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Storefront,
                                contentDescription = null,
                                tint = mapGreen
                            )

                            Spacer(Modifier.width(8.dp))

                            Column {
                                Text(
                                    "Tienda Nutierra",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Ubicación de ejemplo",
                                    fontSize = 10.sp,
                                    color = mapMuted
                                )
                            }
                        }
                    }

                    FilledIconButton(
                        onClick = { requestUserLocation() },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White,
                            contentColor = mapGreen
                        )
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación"
                        )
                    }
                }
            }

            if (locationMessage != null) {
                Surface(
                    color = mapSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = mapGreen
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            locationMessage.orEmpty(),
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp
                        )

                        IconButton(
                            onClick = {
                                locationMessage = null
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar"
                            )
                        }
                    }
                }
            }

            // INFORMACIÓN DE LA TIENDA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 40.dp, height = 5.dp)
                        .background(
                            Color(0xFFDEE4DC),
                            RoundedCornerShape(50.dp)
                        )
                )

                Surface(
                    color = mapSoft,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Ubicación de demostración",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = mapGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    "Nutierra — Tienda Principal Providencia",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = mapText
                )

                Text(
                    "Cosecha seleccionada, frutos secos a granel " +
                            "y deshidratados orgánicos.",
                    fontSize = 14.sp,
                    color = mapMuted
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MapFeatureCard(
                        title = "Granel Zero-Waste",
                        description = "Trae tus frascos y obtén 10% dcto.",
                        icon = Icons.Default.Eco,
                        modifier = Modifier.weight(1f)
                    )

                    MapFeatureCard(
                        title = "Retiro Pick-Up",
                        description = "Listo en tienda en 30 minutos.",
                        icon = Icons.Default.LocalShipping,
                        modifier = Modifier.weight(1f)
                    )
                }

                MapInfoCard(
                    title = "Dirección física",
                    content = "Av. Providencia 1945, Local 4\n" +
                            "Providencia, Región Metropolitana\n" +
                            "Metro Pedro de Valdivia",
                    icon = Icons.Default.PinDrop
                )

                MapInfoCard(
                    title = "Horarios de atención",
                    content = "Lun a Vie: 09:30 - 20:00\n" +
                            "Sábados: 10:00 - 15:00\n" +
                            "Domingos y festivos: Cerrado",
                    icon = Icons.Default.Schedule
                )

                MapInfoCard(
                    title = "Atención directa / WhatsApp",
                    content = "+56 9 8765 4321\nhola@nutierra.cl",
                    icon = Icons.Default.Call
                )

                Button(
                    onClick = {
                        val destination = Uri.encode(storeAddress)

                        openExternalUrl(
                            "https://www.google.com/maps/dir/?api=1" +
                                    "&destination=$destination"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mapGreen
                    )
                ) {
                    Icon(
                        Icons.Default.Directions,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Cómo llegar con Google Maps",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            openExternalUrl(
                                "https://wa.me/56987654321"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))
                        Text("WhatsApp")
                    }

                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(
                                Intent.ACTION_SEND
                            ).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Tienda Nutierra\n$storeAddress"
                                )
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Compartir ubicación"
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))
                        Text("Compartir")
                    }
                }

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = mapGreen
                            )

                            Spacer(Modifier.width(10.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "¿Sin conexión a internet?",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "Guía desde Metro Pedro de Valdivia",
                                    fontSize = 11.sp,
                                    color = mapMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    showDirectionsGuide =
                                        !showDirectionsGuide
                                }
                            ) {
                                Icon(
                                    if (showDirectionsGuide) {
                                        Icons.Default.ExpandLess
                                    } else {
                                        Icons.Default.ExpandMore
                                    },
                                    contentDescription = "Mostrar guía"
                                )
                            }
                        }

                        if (showDirectionsGuide) {
                            HorizontalDivider()

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "1. Salida norte de estación " +
                                            "Pedro de Valdivia (Línea 1)."
                                )
                                Text(
                                    "2. Camina hacia el sector " +
                                            "de Av. Providencia 1945."
                                )
                                Text(
                                    "3. Busca el Local 4 en la " +
                                            "dirección indicada."
                                )
                            }
                        }
                    }
                }

                Text(
                    "Dirección, horarios, contacto y beneficios " +
                            "mostrados como datos de demostración.",
                    fontSize = 11.sp,
                    color = mapMuted
                )
            }
        }
    }
}

@Composable
private fun MapFeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = mapSoft,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = mapGreen
            )

            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                description,
                fontSize = 11.sp,
                color = mapMuted,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun MapInfoCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = mapSoft,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = mapGreen
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    fontSize = 12.sp,
                    color = mapMuted
                )

                Text(
                    content,
                    fontSize = 13.sp,
                    color = mapText,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
