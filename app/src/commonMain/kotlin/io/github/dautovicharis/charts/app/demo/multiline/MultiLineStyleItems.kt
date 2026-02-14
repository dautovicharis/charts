package io.github.dautovicharis.charts.app.demo.multiline

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.demo.line.lineChartTableItems
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults

object MultiLineStyleItems {
    @Composable
    fun customStyle(lineColors: List<Color>) =
        ChartTestStyleFixtures.multiLineCustomStyle(
            chartViewStyle = ChartViewDefaults.style(),
            seriesCount = lineColors.size,
        )

    @Composable
    fun custom(lineColors: List<Color>): StyleItems = lineChartTableItems(customStyle(lineColors))
}
