package com.example.proyecto.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BlueDeep,
    onPrimary = White,
    secondary = OrangeVibrant,
    onSecondary = White,
    tertiary = BluishGrey,
    background = Color(0xFF121212), // Fondo oscuro estándar
    surface = Color(0xFF1E1E1E),
    onBackground = PaleBlue,
    onSurface = PaleBlue
)

private val LightColorScheme = lightColorScheme(
    primary = BlueDeep,
    onPrimary = White,
    secondary = OrangeVibrant,
    onSecondary = White,
    tertiary = BluishGrey,
    background = PaleBlue,
    surface = White,
    onPrimaryContainer = BlueDeep,
    onSecondaryContainer = OrangeVibrant,
    onTertiaryContainer = BluishGrey,
    onBackground = BlueDeep,
    onSurface = BlueDeep,
    onSurfaceVariant = BluishGrey
)

@Composable
fun PROYECTOTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivamos Dynamic Color para forzar la paleta institucional solicitada
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
