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
import com.example.nutierra.data.remote.ApiRepository
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.data.session.SessionPreferences
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
                SessionPreferences.leerUsuarioId(context).collect { id ->
                    if (id != null && SesionManager.usuarioActual.value == null) {
                        val usuarioGuardado = com.example.nutierra.data.local.NutierraDatabase
                            .getDatabase(context)
                            .usuarioDao()
                            .buscarPorId(id)
                        if (usuarioGuardado != null) {
                            SesionManager.iniciarSesion(usuarioGuardado)
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000.milliseconds)
                com.example.nutierra.notifications.NotificacionesHelper
                    .mostrarRecordatorioCarrito(context)
            }

            LaunchedEffect(Unit) {
                try {
                    val apiRepository = ApiRepository()
                    val resultado = apiRepository.getProductos()
                    resultado.onSuccess { productos ->
                        android.util.Log.e("API_TEST", "Productos: ${productos.size}")
                        productos.forEach { android.util.Log.e("API_TEST", "  - ${it.nombre}") }
                    }.onFailure { error ->
                        android.util.Log.e("API_TEST", "Error: ${error.message}")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("API_TEST", "Excepción: ${e.message}", e)
                }
            }

            val simulador: SimuladorViewModel = viewModel()
            LaunchedEffect(Unit) { simulador.iniciar() }

            NutierraTheme {
                NutierraNavigation()
            }
        }
    }
}