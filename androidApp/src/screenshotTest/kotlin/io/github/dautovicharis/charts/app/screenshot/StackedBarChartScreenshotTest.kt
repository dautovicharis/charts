package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_STACKED_BAR_SAMPLE_USE_CASE
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.style.ChartViewDefaults

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedBarChartDefaultPreview() {
    ScreenshotSurface {
        StackedBarChart(
            dataSet = SCREENSHOT_STACKED_BAR_SAMPLE_USE_CASE.initialStackedBarSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedBarChartCustomPreview() {
    ScreenshotSurface {
        StackedBarChart(
            dataSet = SCREENSHOT_STACKED_BAR_SAMPLE_USE_CASE.initialStackedBarSample().dataSet,
            style =
                ChartTestStyleFixtures.stackedBarCustomStyle(
                    chartViewStyle = ChartViewDefaults.style(),
                    segmentCount = 4,
                ),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedBarChartNoCategoriesPreview() {
    ScreenshotSurface {
        StackedBarChart(
            dataSet = SCREENSHOT_STACKED_BAR_SAMPLE_USE_CASE.initialStackedBarNoCategoriesDataSet(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}
