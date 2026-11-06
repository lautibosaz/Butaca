package com.butaca.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    primaryContainer = CrimsonDeep,
    onPrimaryContainer = Color.White,
    secondary = MarqueeGold,
    onSecondary = Ink,
    background = Ink,
    onBackground = Paper,
    surface = InkRaised,
    onSurface = Paper,
    surfaceVariant = InkHigh,
    onSurfaceVariant = Smoke,
    surfaceContainer = InkRaised,
    surfaceContainerHigh = InkHigh,
    outline = InkOutline,
    outlineVariant = InkOutline,
    inverseSurface = Paper,
    inverseOnSurface = Ink
)

private val LightColors = lightColorScheme(
    primary = CrimsonLight,
    onPrimary = Color.White,
    primaryContainer = Crimson,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFB07F00),
    onSecondary = Color.White,
    background = Linen,
    onBackground = Charcoal,
    surface = LinenRaised,
    onSurface = Charcoal,
    surfaceVariant = LinenHigh,
    onSurfaceVariant = Graphite,
    surfaceContainer = LinenRaised,
    surfaceContainerHigh = LinenHigh,
    outline = LinenOutline,
    outlineVariant = LinenOutline,
    inverseSurface = Charcoal,
    inverseOnSurface = Linen
)

@Composable
fun ButacaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = ButacaTypography,
        content = content
    )
}
