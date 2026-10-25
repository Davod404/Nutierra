package com.example.nutierra

import com.example.nutierra.navigation.NutierraNavigation
import com.example.nutierra.ui.theme.NutierraTheme
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import android.os.Bundle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutierraTheme {
                NutierraNavigation()
            }
        }
    }
}
