package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_LINE_SAMPLE_USE_CASE
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_MULTI_LINE_SAMPLE_USE_CASE
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.style.ChartViewDefaults

@PreviewTest
@ScreenshotPreview
@Composable
fun LineChartDefaultPreview() {
    ScreenshotSurface {
        LineChart(
            dataSet = SCREENSHOT_LINE_SAMPLE_USE_CASE.initialLineDataSet(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun LineChartCustomPreview() {
    ScreenshotSurface {
        LineChart(
            dataSet = SCREENSHOT_LINE_SAMPLE_USE_CASE.initialLineDataSet(),
            style = ChartTestStyleFixtures.lineCustomStyle(chartViewStyle = ChartViewDefaults.style()),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun MultiLineChartDefaultPreview() {
    ScreenshotSurface {
        LineChart(
            dataSet = SCREENSHOT_MULTI_LINE_SAMPLE_USE_CASE.initialMultiLineSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun MultiLineChartCustomPreview() {
    ScreenshotSurface {
        LineChart(
            dataSet = SCREENSHOT_MULTI_LINE_SAMPLE_USE_CASE.initialMultiLineSample().dataSet,
            style =
                ChartTestStyleFixtures.multiLineCustomStyle(
                    chartViewStyle = ChartViewDefaults.style(),
                    seriesCount = 3,
                ),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}
