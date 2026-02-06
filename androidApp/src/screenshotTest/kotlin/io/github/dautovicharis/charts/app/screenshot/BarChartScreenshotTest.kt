package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.barBasicData
import io.github.dautovicharis.charts.app.screenshot.shared.barNegativeData
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotBarStyle

@PreviewTest
@ScreenshotPreview
@Composable
fun BarChartBasicPreview() {
    ScreenshotSurface {
        BarChart(
            dataSet = barBasicData(),
            style = screenshotBarStyle(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun BarChartNegativePreview() {
    ScreenshotSurface {
        BarChart(
            dataSet = barNegativeData(),
            style = screenshotBarStyle(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}
