package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotStackedStyle
import io.github.dautovicharis.charts.app.screenshot.shared.stackedBasicData
import io.github.dautovicharis.charts.app.screenshot.shared.stackedNoCategoriesData

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedBarChartBasicPreview() {
    ScreenshotSurface {
        StackedBarChart(dataSet = stackedBasicData(), style = screenshotStackedStyle())
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun StackedBarChartNoCategoriesPreview() {
    ScreenshotSurface {
        StackedBarChart(dataSet = stackedNoCategoriesData(), style = screenshotStackedStyle())
    }
}
