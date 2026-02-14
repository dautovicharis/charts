package io.github.dautovicharis.charts.app.demo.pie

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults

object PieChartStyleItems {
    @Composable
    fun customStyle(pieColors: List<Color>) =
        ChartTestStyleFixtures.pieCustomStyle(
            chartViewStyle = ChartViewDefaults.style(),
            segmentCount = pieColors.size,
        )

    @Composable
    fun custom(pieColors: List<Color>): StyleItems =
        ChartStyleItems(
            currentStyle = customStyle(pieColors),
            defaultStyle = PieChartDefaults.style(),
        )
}
