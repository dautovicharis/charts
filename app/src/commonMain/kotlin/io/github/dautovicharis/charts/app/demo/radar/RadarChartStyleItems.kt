package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.RadarChartDefaults

object RadarChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return ChartStyleItems(
            currentStyle = RadarDemoStyle.default(),
            defaultStyle = RadarChartDefaults.style(),
        )
    }

    @Composable
    fun custom(lineColors: List<Color>): StyleItems {
        return ChartStyleItems(
            currentStyle = RadarDemoStyle.custom(lineColors = lineColors),
            defaultStyle = RadarChartDefaults.style(),
        )
    }
}
