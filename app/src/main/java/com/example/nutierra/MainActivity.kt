package com.example.nutierra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nutierra.navigation.NutierraNavigation
import com.example.nutierra.ui.theme.NutierraTheme

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
