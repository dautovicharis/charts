package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.SCREENSHOT_ANIMATE_ON_START
import io.github.dautovicharis.charts.app.screenshot.shared.pieBasicData
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotPieStyle

@PreviewTest
@ScreenshotPreview
@Composable
fun PieChartBasicPreview() {
    ScreenshotSurface {
        PieChart(
            dataSet = pieBasicData(),
            style = screenshotPieStyle(),
            animateOnStart = SCREENSHOT_ANIMATE_ON_START
        )
    }
}
