package com.example.nutierra

import android.app.Application
import org.osmdroid.config.Configuration

class NutierraApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().userAgentValue =
            "Nutierra/1.0 (contacto: tu-correo@ejemplo.com)"
    }
}