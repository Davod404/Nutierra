package com.example.nutierra.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Lifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import android.content.Intent
import android.Manifest
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import com.google.android.gms.location.LocationServices
import androidx.core.net.toUri
import com.example.nutierra.data.repository.TiendaRepository

private val ESRI_SAT = object : org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase(
    "Esri World Imagery",
    0, 18, 256, ".jpg",
    arrayOf("https://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl +
                org.osmdroid.util.MapTileIndex.getZoom(pMapTileIndex) + "/" +
                org.osmdroid.util.MapTileIndex.getY(pMapTileIndex) + "/" +
                org.osmdroid.util.MapTileIndex.getX(pMapTileIndex)
    }
}

@Composable
fun StoreMapScreen(
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    val tiendas = remember { TiendaRepository().obtenerTiendas() }
    var tiendaSeleccionada by remember { mutableStateOf(tiendas.firstOrNull()) }

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var satelliteMode by remember { mutableStateOf(false) }

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

    var locationMessage by remember { mutableStateOf<String?>(null) }
    var showDirectionsGuide by remember { mutableStateOf(false) }

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
                            mapViewRef?.controller?.animateTo(
                                GeoPoint(location.latitude, location.longitude),
                                16.0,
                                800L
                            )
                        } else {
                            locationMessage =
                                "No hay una ubicación reciente disponible. " +
                                        "Activa el GPS e inténtalo nuevamente."
                        }
                    }
                    .addOnFailureListener {
                        locationMessage = "No se pudo obtener tu ubicación."
                    }
            } catch (_: SecurityException) {
                locationGranted = false
                locationMessage = "No se concedió permiso de ubicación."
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
                locationMessage = "Permiso denegado. Puedes seguir consultando el mapa."
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
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (_: Exception) {
            locationMessage = "No se encontró una aplicación para abrir el enlace."
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapViewRef?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapViewRef?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapViewRef?.onDetach()
        }
    }

    // Cuando cambia la tienda seleccionada, centra el mapa en esa tienda
    LaunchedEffect(tiendaSeleccionada) {
        tiendaSeleccionada?.let { tienda ->
            mapViewRef?.controller?.animateTo(
                GeoPoint(tienda.lat, tienda.lon),
                16.0,
                800L
            )
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
                        "Nuestras tiendas",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(colors.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = "Nutierra",
                            tint = colors.onPrimary,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            setUseDataConnection(true)

                            val primera = tiendas.firstOrNull()
                            if (primera != null) {
                                controller.setZoom(16.0)
                                controller.setCenter(GeoPoint(primera.lat, primera.lon))
                            }

                            tiendas.forEach { tienda ->
                                val marker = Marker(this).apply {
                                    position = GeoPoint(tienda.lat, tienda.lon)
                                    title = tienda.nombre
                                    snippet = tienda.direccion
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    icon = ContextCompat.getDrawable(
                                        ctx,
                                        org.osmdroid.library.R.drawable.marker_default
                                    )
                                    setOnMarkerClickListener { m, _ ->
                                        tiendaSeleccionada = tienda
                                        m.showInfoWindow()
                                        true
                                    }
                                }
                                overlays.add(marker)
                            }

                            if (locationGranted) {
                                val locationProvider = GpsMyLocationProvider(ctx).apply {
                                    addLocationSource(android.location.LocationManager.NETWORK_PROVIDER)
                                }
                                val myLocationOverlay = MyLocationNewOverlay(locationProvider, this).apply {
                                    enableMyLocation()
                                }
                                overlays.add(myLocationOverlay)
                            }

                            mapViewRef = this
                        }
                    },
                    update = { mapView ->
                        mapView.setTileSource(
                            if (satelliteMode) ESRI_SAT else TileSourceFactory.MAPNIK
                        )
                        mapView.invalidate()
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colors.surfaceContainerLowest,
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
                                tint = colors.primary,
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
                                color = colors.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = {
                            satelliteMode = !satelliteMode
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = colors.surfaceContainerLowest,
                            contentColor = colors.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = "Cambiar tipo de mapa"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    FilledIconButton(
                        onClick = { requestUserLocation() },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = colors.surfaceContainerLowest,
                            contentColor = colors.primary
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
                    color = colors.surfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            locationMessage.orEmpty(),
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = { locationMessage = null }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar"
                            )
                        }
                    }
                }
            }

            // Selector de tiendas
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tiendas, key = { it.id }) { tienda ->
                    val selected = tiendaSeleccionada?.id == tienda.id
                    FilterChip(
                        selected = selected,
                        onClick = { tiendaSeleccionada = tienda },
                        label = { Text(tienda.nombre) },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary,
                            selectedLabelColor = colors.onPrimary,
                            selectedLeadingIconColor = colors.onPrimary,
                            containerColor = colors.surfaceContainer
                        )
                    )
                }
            }

            val tienda = tiendaSeleccionada
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colors.surfaceContainerLowest,
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 40.dp, height = 5.dp)
                        .background(
                            colors.surfaceContainerHighest,
                            RoundedCornerShape(50.dp)
                        )
                )

                Surface(
                    color = colors.surfaceContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Ubicación de demostración",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = colors.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    tienda?.nombre ?: "Nutierra",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Text(
                    "Cosecha seleccionada, frutos secos a granel " +
                            "y deshidratados orgánicos.",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant
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
                    content = tienda?.direccion ?: "No registrada",
                    icon = Icons.Default.PinDrop
                )

                MapInfoCard(
                    title = "Horarios de atención",
                    content = tienda?.horario ?: "No registrado",
                    icon = Icons.Default.Schedule
                )

                MapInfoCard(
                    title = "Atención directa / WhatsApp",
                    content = tienda?.telefono ?: "No registrado",
                    icon = Icons.Default.Call
                )

                Button(
                    onClick = {
                        tienda?.let {
                            openExternalUrl(
                                "https://www.openstreetmap.org/" +
                                        "?mlat=${it.lat}&mlon=${it.lon}" +
                                        "#map=17/${it.lat}/${it.lon}"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Cómo llegar en OpenStreetMap",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            tienda?.let {
                                openExternalUrl(
                                    "https://wa.me/${it.telefono.replace("+", "").replace(" ", "")}"
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("WhatsApp")
                    }

                    OutlinedButton(
                        onClick = {
                            tienda?.let {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${it.nombre}\n${it.direccion}"
                                    )
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Compartir ubicación"
                                    )
                                )
                            }
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

                if (tiendaSeleccionada?.id == 1) {
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
                                    tint = colors.primary
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "¿Sin conexión a internet?",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        "Guía desde Metro Pedro de Valdivia",
                                        fontSize = 11.sp,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        showDirectionsGuide = !showDirectionsGuide
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
                                HorizontalDivider(color = colors.outlineVariant)
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        "1. Salida norte de estación Pedro de Valdivia (Línea 1).",
                                        color = colors.onSurface
                                    )
                                    Text(
                                        "2. Camina hacia el sector de Av. Providencia 1945.",
                                        color = colors.onSurface
                                    )
                                    Text(
                                        "3. Busca el Local 4 en la dirección indicada.",
                                        color = colors.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    "Dirección, horarios, contacto y beneficios " +
                            "mostrados como datos de demostración.",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
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
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        color = colors.surfaceContainer,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = colors.primary
            )
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                description,
                fontSize = 11.sp,
                color = colors.onSurfaceVariant,
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
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surfaceContainer,
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
                        colors.surfaceContainerLowest,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = colors.primary
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    content,
                    fontSize = 13.sp,
                    color = colors.onSurface,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}