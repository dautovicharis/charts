package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

object StackedBarChartStyleItems {
    @Composable
    fun default(): StyleItems =
        ChartStyleItems(
            currentStyle = StackedBarChartDefaults.style(),
            defaultStyle = StackedBarChartDefaults.style(),
        )

    @Composable
    fun customStyle(barColors: List<Color>) =
        ChartTestStyleFixtures.stackedBarCustomStyle(
            chartViewStyle = ChartViewDefaults.style(),
            segmentCount = barColors.size,
        )

    @Composable
    fun custom(barColors: List<Color>): StyleItems =
        ChartStyleItems(
            currentStyle = customStyle(barColors),
            defaultStyle = StackedBarChartDefaults.style(),
        )
}
