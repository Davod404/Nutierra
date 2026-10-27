package com.example.nutierra

import org.osmdroid.config.Configuration
import android.app.Application
import android.app.NotificationManager
import android.app.NotificationChannel
class NutierraApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue =
            "Nutierra/1.0 (contacto: tu-correo@ejemplo.com)"

        val channel = NotificationChannel(
            "recordatorios_carrito",
            "Recordatorios de carrito",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos para volver a comprar"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}