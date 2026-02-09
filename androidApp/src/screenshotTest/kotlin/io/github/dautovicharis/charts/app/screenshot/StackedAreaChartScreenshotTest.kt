package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotStackedAreaStyle
import io.github.dautovicharis.charts.app.screenshot.shared.stackedAreaBasicData
import io.github.dautovicharis.charts.app.screenshot.shared.stackedAreaNoCategoriesData

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedAreaChartBasicPreview() {
    ScreenshotSurface {
        StackedAreaChart(
            dataSet = stackedAreaBasicData(),
            style = screenshotStackedAreaStyle(),
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
            dataSet = stackedAreaNoCategoriesData(),
            style = screenshotStackedAreaStyle(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START,
        )
    }
}
