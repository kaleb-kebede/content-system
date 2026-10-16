package com.example.creatorgrowthos.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBluePrimary,
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = ElectricBluePrimary,
    secondary = MintProgressLight,
    onSecondary = Slate950,
    secondaryContainer = Slate850,
    onSecondaryContainer = MintProgressLight,
    tertiary = VioletSecondary,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate850,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    outline = Slate700,
    error = CrimsonCriticalLight,
    onError = Slate950
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Slate100,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPrimary,
    secondary = MintProgress,
    onSecondary = Slate100,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = MintProgress,
    tertiary = VioletSecondary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = CrimsonCritical,
    onError = Slate100
)

@Composable
fun CreatorGrowthOSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
