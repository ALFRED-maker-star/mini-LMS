package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LearnViolet,
    onPrimary = Color.White,
    primaryContainer = LearnVioletContainer,
    onPrimaryContainer = LearnOnVioletContainer,
    secondary = LearnGreen,
    onSecondary = Color.White,
    secondaryContainer = LearnGreenContainer,
    onSecondaryContainer = LearnOnGreenContainer,
    tertiary = LearnAmber,
    onTertiary = Color.White,
    tertiaryContainer = LearnAmberContainer,
    onTertiaryContainer = LearnOnAmberContainer,
    background = LearnBackground,
    onBackground = LearnTextPrimary,
    surface = LearnSurface,
    onSurface = LearnTextPrimary,
    surfaceVariant = LearnSurfaceVariant,
    onSurfaceVariant = LearnTextSecondary,
    outline = LearnBorder,
    outlineVariant = LearnBorderLight,
    error = LearnRed,
    onError = Color.White,
    errorContainer = LearnRedContainer,
    onErrorContainer = LearnOnRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = LearnVioletLight,
    onPrimary = Color.Black,
    primaryContainer = LearnVioletDark,
    onPrimaryContainer = LearnVioletContainer,
    secondary = LearnGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = LearnGreenDark,
    onSecondaryContainer = LearnGreenContainer,
    tertiary = LearnAmber,
    onTertiary = Color.Black,
    background = Color(0xFF111827),
    onBackground = Color(0xFFF9FAFB),
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF4B5563),
    outlineVariant = Color(0xFF374151)
)

@Composable
fun LearnPathTheme(
    darkTheme: Boolean = false, // Keep the requested clean light palette by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
