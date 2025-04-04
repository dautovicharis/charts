package io.github.dautovicharis.charts.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

internal fun lightColors(colors: ThemeColors) = lightColorScheme(
    primary = colors.md_theme_light_primary,
    onPrimary = colors.md_theme_light_onPrimary,
    primaryContainer = colors.md_theme_light_primaryContainer,
    onPrimaryContainer = colors.md_theme_light_onPrimaryContainer,
    secondary = colors.md_theme_light_secondary,
    onSecondary = colors.md_theme_light_onSecondary,
    secondaryContainer = colors.md_theme_light_secondaryContainer,
    onSecondaryContainer = colors.md_theme_light_onSecondaryContainer,
    tertiary = colors.md_theme_light_tertiary,
    onTertiary = colors.md_theme_light_onTertiary,
    tertiaryContainer = colors.md_theme_light_tertiaryContainer,
    onTertiaryContainer = colors.md_theme_light_onTertiaryContainer,
    error = colors.md_theme_light_error,
    errorContainer = colors.md_theme_light_errorContainer,
    onError = colors.md_theme_light_onError,
    onErrorContainer = colors.md_theme_light_onErrorContainer,
    background = colors.md_theme_light_background,
    onBackground = colors.md_theme_light_onBackground,
    surface = colors.md_theme_light_surface,
    onSurface = colors.md_theme_light_onSurface,
    surfaceVariant = colors.md_theme_light_surfaceVariant,
    onSurfaceVariant = colors.md_theme_light_onSurfaceVariant,
    outline = colors.md_theme_light_outline,
    inverseOnSurface = colors.md_theme_light_inverseOnSurface,
    inverseSurface = colors.md_theme_light_inverseSurface,
    inversePrimary = colors.md_theme_light_inversePrimary,
    surfaceTint = colors.md_theme_light_surfaceTint,
    outlineVariant = colors.md_theme_light_outlineVariant,
    scrim = colors.md_theme_light_scrim
)

internal fun darkColors(colors: ThemeColors) = darkColorScheme(
    primary = colors.md_theme_dark_primary,
    onPrimary = colors.md_theme_dark_onPrimary,
    primaryContainer = colors.md_theme_dark_primaryContainer,
    onPrimaryContainer = colors.md_theme_dark_onPrimaryContainer,
    secondary = colors.md_theme_dark_secondary,
    onSecondary = colors.md_theme_dark_onSecondary,
    secondaryContainer = colors.md_theme_dark_secondaryContainer,
    onSecondaryContainer = colors.md_theme_dark_onSecondaryContainer,
    tertiary = colors.md_theme_dark_tertiary,
    onTertiary = colors.md_theme_dark_onTertiary,
    tertiaryContainer = colors.md_theme_dark_tertiaryContainer,
    onTertiaryContainer = colors.md_theme_dark_onTertiaryContainer,
    error = colors.md_theme_dark_error,
    errorContainer = colors.md_theme_dark_errorContainer,
    onError = colors.md_theme_dark_onError,
    onErrorContainer = colors.md_theme_dark_onErrorContainer,
    background = colors.md_theme_dark_background,
    onBackground = colors.md_theme_dark_onBackground,
    surface = colors.md_theme_dark_surface,
    onSurface = colors.md_theme_dark_onSurface,
    surfaceVariant = colors.md_theme_dark_surfaceVariant,
    onSurfaceVariant = colors.md_theme_dark_onSurfaceVariant,
    outline = colors.md_theme_dark_outline,
    inverseOnSurface = colors.md_theme_dark_inverseOnSurface,
    inverseSurface = colors.md_theme_dark_inverseSurface,
    inversePrimary = colors.md_theme_dark_inversePrimary,
    surfaceTint = colors.md_theme_dark_surfaceTint,
    outlineVariant = colors.md_theme_dark_outlineVariant,
    scrim = colors.md_theme_dark_scrim
)

val LocalHasDynamicColorFeature = compositionLocalOf { false }

@Composable
fun AppTheme(
    theme: Theme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    useDynamicColors: Boolean = false,
    content: @Composable () -> Unit
) {
    val dynamicColors = dynamicColors()
    val colorScheme = when {
        useDynamicColors && dynamicColors != null -> dynamicColors
        darkTheme -> darkColors(theme.colors)
        else -> lightColors(theme.colors)
    }

    CompositionLocalProvider(
        LocalHasDynamicColorFeature provides (dynamicColors != null)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
expect fun dynamicColors(): ColorScheme?
