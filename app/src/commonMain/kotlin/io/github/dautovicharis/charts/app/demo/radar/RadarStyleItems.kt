package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle

object RadarChartStyleItems {
    @Composable
    fun default(): StyleItems {
        val style = RadarChartDefaults.style()
        return radarChartTableItems(style)
    }

    @Composable
    fun custom(lineColors: List<Color>): StyleItems {
        val style = RadarDemoStyle.custom(lineColors)
        return radarChartTableItems(style)
    }
}

@Composable
fun radarChartTableItems(
    currentStyle: RadarChartStyle
): StyleItems {
    return ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = RadarChartDefaults.style()
    )
}
