package io.github.dautovicharis.charts.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
internal fun defaultChartAlpha(
    light: Float = 0.7f,
    dark: Float = 0.6f,
): Float = if (isSystemInDarkTheme()) dark else light
