package io.github.dautovicharis.charts.app.demo.line

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle

object LineChartStyleItems {
    @Composable
    fun customStyle() = ChartTestStyleFixtures.lineCustomStyle(chartViewStyle = ChartViewDefaults.style())

    @Composable
    fun custom(): StyleItems = lineChartTableItems(customStyle())
}

@Composable
fun lineChartTableItems(currentStyle: LineChartStyle): StyleItems =
    ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = LineChartDefaults.style(),
    )
