package com.example.nutierra

import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import com.example.nutierra.ui.viewmodel.SimuladorViewModel
import com.example.nutierra.ui.theme.NutierraTheme
import com.example.nutierra.navigation.NutierraNavigation
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            var notificacionesPermitidas by remember { mutableStateOf(false) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                notificacionesPermitidas = granted
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!granted) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000.milliseconds)
                com.example.nutierra.notifications.NotificacionesHelper
                    .mostrarRecordatorioCarrito(context)
            }

            val simulador: SimuladorViewModel = viewModel()
            LaunchedEffect(Unit) { simulador.iniciar() }

            NutierraTheme {
                NutierraNavigation()
            }
        }
    }
}
