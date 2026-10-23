package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemePreset

private fun getLightColorScheme(preset: AppThemePreset) = lightColorScheme(
    primary = Color(preset.primaryHex),
    onPrimary = Color.White,
    primaryContainer = Color(preset.primaryHex).copy(alpha = 0.15f),
    onPrimaryContainer = Color(preset.primaryHex),
    secondary = Color(preset.secondaryHex),
    onSecondary = Color.White,
    secondaryContainer = Color(preset.secondaryHex).copy(alpha = 0.15f),
    onSecondaryContainer = Color(preset.secondaryHex),
    background = SurfaceLight,
    surface = CardSurfaceLight,
    onBackground = Color(0xFF191C1A),
    onSurface = Color(0xFF191C1A)
)

private fun getDarkColorScheme(preset: AppThemePreset) = darkColorScheme(
    primary = when (preset) {
        AppThemePreset.EMERALD -> EmeraldPrimaryDark
        AppThemePreset.ROYAL_GOLD -> RoyalPrimaryDark
        AppThemePreset.MIDNIGHT_BLUE -> MidnightPrimaryDark
        AppThemePreset.DESERT_ROSE -> DesertPrimaryDark
        AppThemePreset.FOREST_SERENITY -> Color(0xFF6EE7B7)
    },
    onPrimary = Color(0xFF003820),
    primaryContainer = Color(preset.primaryHex).copy(alpha = 0.35f),
    onPrimaryContainer = Color(0xFFC7F0DB),
    secondary = Color(preset.secondaryHex),
    onSecondary = Color(0xFF3F3000),
    secondaryContainer = Color(preset.secondaryHex).copy(alpha = 0.25f),
    onSecondaryContainer = Color(0xFFFFEFA8),
    background = SurfaceDark,
    surface = CardSurfaceDark,
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF)
)

@Composable
fun SalahTheme(
    preset: AppThemePreset = AppThemePreset.EMERALD,
    darkModePreference: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (darkModePreference) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) getDarkColorScheme(preset) else getLightColorScheme(preset)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
