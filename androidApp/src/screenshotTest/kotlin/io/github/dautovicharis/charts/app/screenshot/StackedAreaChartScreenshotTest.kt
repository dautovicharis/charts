package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.style.ChartViewDefaults

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedAreaChartDefaultPreview() {
    ScreenshotSurface {
        StackedAreaChart(
            dataSet = SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE.initialStackedAreaSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedAreaChartCustomPreview() {
    ScreenshotSurface {
        StackedAreaChart(
            dataSet = SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE.initialStackedAreaSample().dataSet,
            style =
                ChartTestStyleFixtures.stackedAreaCustomStyle(
                    chartViewStyle = ChartViewDefaults.style(),
                    seriesCount = 3,
                ),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedAreaChartNoCategoriesPreview() {
    ScreenshotSurface {
        StackedAreaChart(
            dataSet = SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE.initialStackedAreaNoCategoriesDataSet(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedAreaChartSelectedPointPreview() {
    ScreenshotSurface {
        StackedAreaChart(
            dataSet = SCREENSHOT_STACKED_AREA_SAMPLE_USE_CASE.initialStackedAreaSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
            interactionEnabled = false,
            selectedPointIndex = 1,
        )
    }
}
