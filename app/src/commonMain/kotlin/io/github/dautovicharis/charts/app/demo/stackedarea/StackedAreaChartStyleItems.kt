package io.github.dautovicharis.charts.app.demo.stackedarea

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults

object StackedAreaChartStyleItems {
    @Composable
    fun custom(): StyleItems =
        ChartStyleItems(
            currentStyle =
                ChartTestStyleFixtures.stackedAreaCustomStyle(
                    chartViewStyle = ChartViewDefaults.style(),
                    seriesCount = 0,
                ),
            defaultStyle = StackedAreaChartDefaults.style(),
        )

    @Composable
    fun customStyle(areaColors: List<Color>) =
        ChartTestStyleFixtures.stackedAreaCustomStyle(
            chartViewStyle = ChartViewDefaults.style(),
            seriesCount = areaColors.size,
        )

    @Composable
    fun custom(areaColors: List<Color>): StyleItems =
        ChartStyleItems(
            currentStyle = customStyle(areaColors),
            defaultStyle = StackedAreaChartDefaults.style(),
        )
}
