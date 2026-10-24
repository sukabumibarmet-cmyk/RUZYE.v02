package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalThemeMode = staticCompositionLocalOf { AppThemeMode.LIGHT }

val LightColorScheme = lightColorScheme(
    primary = RuziePurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECEBFA),
    onPrimaryContainer = Color(0xFF2C247A),
    secondary = RuziePurpleLight,
    onSecondary = Color(0xFF1A1A2E),
    secondaryContainer = LightCard,
    onSecondaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder
)

val DarkColorScheme = darkColorScheme(
    primary = RuziePurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF382F72),
    onPrimaryContainer = Color(0xFFECEBFA),
    secondary = RuziePurpleLight,
    onSecondary = Color(0xFF0F0F1A),
    secondaryContainer = DarkCard,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder
)

val CyberColorScheme = darkColorScheme(
    primary = CyberNeonGreen,
    onPrimary = Color.Black,
    primaryContainer = CyberCard,
    onPrimaryContainer = CyberNeonGreen,
    secondary = CyberNeonGreenSecondary,
    onSecondary = Color.Black,
    secondaryContainer = CyberSurface,
    onSecondaryContainer = CyberNeonGreenSecondary,
    background = CyberBackground,
    onBackground = CyberNeonGreen,
    surface = CyberSurface,
    onSurface = CyberNeonGreen,
    surfaceVariant = CyberCard,
    onSurfaceVariant = CyberTextDim,
    outline = CyberBorder
)

@Composable
fun RuzieAiTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.CYBER -> CyberColorScheme
    }

    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
