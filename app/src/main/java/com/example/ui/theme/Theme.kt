package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AnxAiColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF031424),
    primaryContainer = SpaceNavyLightCard,
    onPrimaryContainer = NeonCyan,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = SpaceNavyCard,
    onSecondaryContainer = Color(0xFFBCE1FF),
    tertiary = NeonPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2A1647),
    onTertiaryContainer = Color(0xFFE8B8FF),
    background = SpaceNavyDark,
    onBackground = TextPrimary,
    surface = SpaceNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = SpaceNavyCard,
    onSurfaceVariant = TextSecondary,
    outline = SpaceNavyBorder,
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AnxAiColorScheme,
        typography = Typography,
        content = content
    )
}
