package io.github.dautovicharis.charts.app.fixtures

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.app.ui.theme.LocalChartColors
import io.github.dautovicharis.charts.app.ui.theme.seriesColor
import io.github.dautovicharis.charts.app.ui.theme.seriesColors
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.BarChartStyle
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartStyle
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle

/**
 * Shared custom style fixtures used by:
 * - `app/src/commonMain/kotlin/io/github/dautovicharis/charts/app/demo/...`
 * - `androidApp/src/screenshotTest/kotlin/.../ChartScreenshotTest.kt`
 * - `androidApp/src/androidTest/kotlin/io/github/dautovicharis/charts/app/recording/DeterministicGifScenarios.kt`
 */
object ChartTestStyleFixtures {
    @Composable
    fun pieCustomStyle(
        chartViewStyle: ChartViewStyle,
        segmentCount: Int = 6,
    ): PieChartStyle {
        val chartColors = LocalChartColors.current
        return PieChartDefaults.style(
            chartViewStyle = chartViewStyle,
            borderColor = MaterialTheme.colorScheme.surface,
            donutPercentage = 40f,
            borderWidth = 5f,
            legendVisible = true,
            pieColors = chartColors.seriesColors(segmentCount),
        )
    }

    @Composable
    fun lineCustomStyle(chartViewStyle: ChartViewStyle): LineChartStyle {
        val chartColors = LocalChartColors.current
        return LineChartDefaults.style(
            chartViewStyle = chartViewStyle,
            lineColor = chartColors.seriesColor(1),
            pointColor = chartColors.highlight,
            pointSize = 9f,
            bezier = false,
            dragPointVisible = false,
            dragPointColor = chartColors.selection,
            dragPointSize = 8f,
            dragActivePointSize = 15f,
        )
    }

    @Composable
    fun multiLineCustomStyle(
        chartViewStyle: ChartViewStyle,
        seriesCount: Int,
    ): LineChartStyle {
        val chartColors = LocalChartColors.current
        return LineChartDefaults.style(
            chartViewStyle = chartViewStyle,
            lineColors = chartColors.seriesColors(seriesCount),
            bezier = false,
            pointVisible = true,
            dragPointVisible = false,
            pointColor = chartColors.highlight,
            dragPointColor = chartColors.selection,
        )
    }

    @Composable
    fun barCustomStyle(
        chartViewStyle: ChartViewStyle,
        minValue: Float? = null,
        maxValue: Float? = null,
    ): BarChartStyle {
        val chartColors = LocalChartColors.current
        return BarChartDefaults.style(
            chartViewStyle = chartViewStyle,
            barColor = chartColors.seriesColor(4),
            minValue = minValue,
            maxValue = maxValue,
            gridColor = chartColors.gridLine,
            axisColor = chartColors.axisLine,
            xAxisLabelColor = chartColors.axisLabel,
            selectionLineVisible = true,
            selectionLineColor = chartColors.selection,
            selectionLineWidth = 2f,
        )
    }

    @Composable
    fun stackedBarCustomStyle(
        chartViewStyle: ChartViewStyle,
        segmentCount: Int,
    ): StackedBarChartStyle {
        val chartColors = LocalChartColors.current
        return StackedBarChartDefaults.style(
            chartViewStyle = chartViewStyle,
            barColors = chartColors.seriesColors(segmentCount),
            space = 8.dp,
        )
    }

    @Composable
    fun stackedAreaCustomStyle(
        chartViewStyle: ChartViewStyle,
        seriesCount: Int,
    ): StackedAreaChartStyle {
        val chartColors = LocalChartColors.current
        val colors = chartColors.seriesColors(seriesCount)
        return StackedAreaChartDefaults.style(
            chartViewStyle = chartViewStyle,
            areaColors = colors,
            lineColors = colors,
            fillAlpha = 0.3f,
            lineVisible = true,
            lineWidth = 3.5f,
            bezier = false,
        )
    }

    @Composable
    fun radarCustomStyle(
        chartViewStyle: ChartViewStyle,
        seriesKeys: List<String>,
    ): RadarChartStyle {
        val chartColors = LocalChartColors.current
        return RadarChartDefaults.style(
            chartViewStyle = chartViewStyle,
            lineColors = chartColors.seriesColors(seriesKeys),
            lineWidth = 3.5f,
            pointColor = chartColors.highlight,
            pointSize = 5f,
            gridSteps = 6,
            gridLineWidth = 1.4f,
            axisLineColor = chartColors.axisLine,
            axisLineWidth = 1.2f,
            axisLabelColor = chartColors.axisLabel,
            fillAlpha = 0.2f,
            categoryLegendVisible = false,
        )
    }
}
