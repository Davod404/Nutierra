package com.example.nutierra

import org.osmdroid.config.Configuration
import android.app.Application

class NutierraApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue =
            "Nutierra/1.0 (contacto: tu-correo@ejemplo.com)"
    }
}