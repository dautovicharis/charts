package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.RadarChartDefaults

object RadarChartStyleItems {
    @Composable
    fun default(): StyleItems =
        ChartStyleItems(
            currentStyle = RadarChartDefaults.style(),
            defaultStyle = RadarChartDefaults.style(),
        )

    @Composable
    fun customStyle(seriesKeys: List<String>) =
        ChartTestStyleFixtures.radarCustomStyle(
            chartViewStyle = ChartViewDefaults.style(),
            seriesKeys = seriesKeys,
        )

    @Composable
    fun custom(seriesKeys: List<String>): StyleItems =
        ChartStyleItems(
            currentStyle = customStyle(seriesKeys),
            defaultStyle = RadarChartDefaults.style(),
        )
}
