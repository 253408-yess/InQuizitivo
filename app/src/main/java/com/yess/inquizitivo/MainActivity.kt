package com.yess.inquizitivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yess.inquizitivo.ui.theme.InQuizitivoTheme

// MAIN ACTIVITY: Punto de entrada de la aplicación
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InQuizitivoTheme {
                AppNavigation()
            }
        }
    }
}
