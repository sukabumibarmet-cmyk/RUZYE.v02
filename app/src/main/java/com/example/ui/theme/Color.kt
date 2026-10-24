package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Colors
val RuziePurple = Color(0xFF6C5CE7)
val RuziePurpleLight = Color(0xFFA78BFA)
val RuziePurpleDark = Color(0xFF4834DF)

// Light Theme Palette
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF8F8FC)
val LightCard = Color(0xFFF0F0F5)
val LightTextPrimary = Color(0xFF1A1A2E)
val LightTextSecondary = Color(0xFF6B6B7B)
val LightBorder = Color(0xFFE2E2EC)

// Dark Theme Palette
val DarkBackground = Color(0xFF0F0F1A)
val DarkSurface = Color(0xFF161626)
val DarkCard = Color(0xFF1A1A2E)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFA0A0B0)
val DarkBorder = Color(0xFF2E2E44)

// Cyber Theme Palette
val CyberBackground = Color(0xFF000000)
val CyberSurface = Color(0xFF001100)
val CyberCard = Color(0xFF051A05)
val CyberNeonGreen = Color(0xFF00FF00)
val CyberNeonGreenSecondary = Color(0xFF00FF88)
val CyberTextDim = Color(0xFF00AA00)
val CyberBorder = Color(0xFF00DD44)

enum class AppThemeMode(val title: String, val subtitle: String) {
    LIGHT("Light Mode", "Tampilan terang & bersih"),
    DARK("Dark Mode", "Nuansa gelap & elegan"),
    CYBER("Cyber Mode", "Futuristik & neon terminal")
}
