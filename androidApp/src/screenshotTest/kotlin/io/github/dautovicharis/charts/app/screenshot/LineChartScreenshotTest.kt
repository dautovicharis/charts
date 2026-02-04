package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.lineMultiData
import io.github.dautovicharis.charts.app.screenshot.shared.lineSingleData
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotLineStyle

@PreviewTest
@ScreenshotPreview
@Composable
fun LineChartSinglePreview() {
    ScreenshotSurface {
        LineChart(dataSet = lineSingleData(), style = screenshotLineStyle())
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun LineChartMultiPreview() {
    ScreenshotSurface {
        LineChart(dataSet = lineMultiData(), style = screenshotLineStyle())
    }
}
