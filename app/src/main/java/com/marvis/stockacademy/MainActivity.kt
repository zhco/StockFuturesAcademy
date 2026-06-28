package com.marvis.stockacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.marvis.stockacademy.ui.navigation.AppNavGraph

val UpRed = Color(0xFFE53935)
val DownGreen = Color(0xFF43A047)

private val LightColorScheme = lightColorScheme(
    primary = UpRed,
    secondary = Color(0xFF1E88E5),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = LightColorScheme) { AppNavGraph(rememberNavController()) } }
    }
}
