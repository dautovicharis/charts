package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_PIE_SAMPLE_USE_CASE
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.style.ChartViewDefaults

@PreviewTest
@ScreenshotPreview
@Composable
fun PieChartDefaultPreview() {
    ScreenshotSurface {
        PieChart(
            dataSet = SCREENSHOT_PIE_SAMPLE_USE_CASE.initialPieSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun PieChartCustomPreview() {
    ScreenshotSurface {
        PieChart(
            dataSet = SCREENSHOT_PIE_SAMPLE_USE_CASE.initialPieCustomSample().dataSet,
            style =
                ChartTestStyleFixtures.pieCustomStyle(
                    chartViewStyle = ChartViewDefaults.style(),
                    segmentCount = 6,
                ),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun PieChartSelectedSlicePreview() {
    ScreenshotSurface {
        PieChart(
            dataSet = SCREENSHOT_PIE_SAMPLE_USE_CASE.initialPieSample().dataSet,
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
            interactionEnabled = false,
            selectedSliceIndex = 1,
        )
    }
}
