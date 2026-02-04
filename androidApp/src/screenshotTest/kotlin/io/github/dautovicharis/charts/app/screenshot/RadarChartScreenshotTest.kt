package io.github.dautovicharis.charts.app.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotPreview
import io.github.dautovicharis.charts.app.screenshot.shared.ScreenshotSurface
import io.github.dautovicharis.charts.app.screenshot.shared.radarBasicData
import io.github.dautovicharis.charts.app.screenshot.shared.radarEdgeData
import io.github.dautovicharis.charts.app.screenshot.shared.radarMultiData
import io.github.dautovicharis.charts.app.screenshot.shared.radarMultiNoCategoriesData
import io.github.dautovicharis.charts.app.screenshot.shared.screenshotRadarStyle

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartSinglePreview() {
    ScreenshotSurface {
        RadarChart(dataSet = radarBasicData(), style = screenshotRadarStyle())
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartSingleHiddenCategoryLegendPreview() {
    ScreenshotSurface {
        RadarChart(
            dataSet = radarBasicData(),
            style = screenshotRadarStyle(categoryLegendVisible = false)
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartSingleAxisLabelsPreview() {
    ScreenshotSurface {
        RadarChart(
            dataSet = radarBasicData(),
            style = screenshotRadarStyle(
                axisLabelVisible = true,
                categoryLegendVisible = false,
                categoryPinsVisible = false
            )
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartSingleEdgePinsPreview() {
    ScreenshotSurface {
        RadarChart(
            dataSet = radarEdgeData(),
            style = screenshotRadarStyle()
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartMultiPreview() {
    ScreenshotSurface {
        RadarChart(dataSet = radarMultiData(), style = screenshotRadarStyle())
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartMultiNoPinsPreview() {
    ScreenshotSurface {
        RadarChart(
            dataSet = radarMultiData(),
            style = screenshotRadarStyle(categoryPinsVisible = false)
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun RadarChartMultiNoCategoriesPreview() {
    ScreenshotSurface {
        RadarChart(
            dataSet = radarMultiNoCategoriesData(),
            style = screenshotRadarStyle()
        )
    }
}
