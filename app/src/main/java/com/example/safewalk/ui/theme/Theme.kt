package com.example.safewalk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    secondary = SoftSage,
    onSecondary = OnSageDark,
    secondaryContainer = SageContainer,
    onSecondaryContainer = OnSageDark,
    background = OffWhite,
    onBackground = InkLight,
    surface = Color.White,
    onSurface = InkLight,
    surfaceVariant = SurfaceVariantLight,
    error = CoralAlert,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = OnCyanDark,
    secondary = EmeraldActive,
    onSecondary = OnEmeraldDark,
    background = NightBg,
    onBackground = InkDark,
    surface = NightSurface,
    onSurface = InkDark,
    surfaceVariant = NightSurfaceVariant,
    error = CoralNight,
    onError = Color.Black
)

private val SafeWalkShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun SafeWalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SafeWalkTypography,
        shapes = SafeWalkShapes,
        content = content
    )
}