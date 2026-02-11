package io.github.dautovicharis.charts.app.screenshot.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.dautovicharis.charts.app.data.impl.DefaultBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultMultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultPieSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultRadarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedBarSampleUseCase
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.docsSlate

internal val ScreenshotTheme = docsSlate
internal const val SCREENSHOT_ANIMATE_ON_START = false
internal val SCREENSHOT_PIE_SAMPLE_USE_CASE = DefaultPieSampleUseCase()
internal val SCREENSHOT_LINE_SAMPLE_USE_CASE = DefaultLineSampleUseCase()
internal val SCREENSHOT_MULTI_LINE_SAMPLE_USE_CASE = DefaultMultiLineSampleUseCase()
internal val SCREENSHOT_BAR_SAMPLE_USE_CASE = DefaultBarSampleUseCase()
internal val SCREENSHOT_STACKED_BAR_SAMPLE_USE_CASE = DefaultStackedBarSampleUseCase()
internal val SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE = DefaultStackedAreaSampleUseCase()
internal val SCREENSHOT_RADAR_SAMPLE_USE_CASE = DefaultRadarSampleUseCase()

@Composable
internal fun ScreenshotSurface(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()
    AppTheme(theme = ScreenshotTheme, darkTheme = darkTheme, useDynamicColors = false) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
