package com.example.nutierra

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutierra.data.local.NutierraDatabase
import com.example.nutierra.data.remote.ApiRepository
import com.example.nutierra.data.session.SesionManager
import com.example.nutierra.data.session.SessionPreferences
import com.example.nutierra.navigation.NutierraNavigation
import com.example.nutierra.notifications.NotificacionesHelper
import com.example.nutierra.ui.theme.NutierraTheme
import com.example.nutierra.ui.viewmodel.SimuladorViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

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
                        val usuarioGuardado = NutierraDatabase
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
                delay(3000.milliseconds)
                NotificacionesHelper.mostrarRecordatorioCarrito(context)
            }

            LaunchedEffect(Unit) {
                val repo = ApiRepository()

                repo.getProductos().onSuccess { productos ->
                    Log.e("API_TEST", "Productos: ${productos.size}")
                    productos.forEach { Log.e("API_TEST", "  - ${it.nombre}") }
                }.onFailure { Log.e("API_TEST", "Error productos: ${it.message}") }

                repo.getPedidos().onSuccess { pedidos ->
                    Log.e("API_TEST", "Pedidos: ${pedidos.size}")
                }.onFailure { Log.e("API_TEST", "Error pedidos: ${it.message}") }

                repo.getUsuarios().onSuccess { usuarios ->
                    Log.e("API_TEST", "Usuarios: ${usuarios.size}")
                }.onFailure { Log.e("API_TEST", "Error usuarios: ${it.message}") }

                repo.login("cliente@nutierra.cl", "Semillas2025!").onSuccess { usuario ->
                    Log.e("API_TEST", "Login: ${usuario.nombre} ${usuario.apellido}")
                }.onFailure { Log.e("API_TEST", "Error login: ${it.message}") }
            }

            val simulador: SimuladorViewModel = viewModel()
            LaunchedEffect(Unit) { simulador.iniciar() }

            NutierraTheme {
                NutierraNavigation()
            }
        }
    }
}