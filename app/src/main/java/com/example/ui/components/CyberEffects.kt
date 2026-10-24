package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CyberNeonGreen
import com.example.ui.theme.LocalThemeMode

@Composable
fun CyberScanlineOverlay(modifier: Modifier = Modifier) {
    val themeMode = LocalThemeMode.current
    if (themeMode != AppThemeMode.CYBER) return

    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val sweepY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepY"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Subtle horizontal CRT lines
        val lineSpacing = 6f
        var y = 0f
        while (y < height) {
            drawLine(
                color = Color(0x1500FF00),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += lineSpacing
        }

        // Sweeping beam
        val beamCenter = sweepY * height
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x1F00FF00),
                    Color(0x3500FF88),
                    Color(0x1F00FF00),
                    Color.Transparent
                ),
                startY = beamCenter - 80f,
                endY = beamCenter + 80f
            ),
            topLeft = Offset(0f, beamCenter - 80f),
            size = androidx.compose.ui.geometry.Size(width, 160f)
        )
    }
}

fun Modifier.cyberBorder(
    themeMode: AppThemeMode,
    width: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(12.dp)
): Modifier {
    return if (themeMode == AppThemeMode.CYBER) {
        this.border(width, CyberNeonGreen.copy(alpha = 0.85f), shape)
    } else {
        this
    }
}
