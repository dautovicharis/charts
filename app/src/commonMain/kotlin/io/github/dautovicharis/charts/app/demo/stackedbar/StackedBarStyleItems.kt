package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle

object StackedBarChartStyleItems {
    @Composable
    fun custom(barColors: List<Color>): StyleItems {
        val style = StackedBarDemoStyle.custom(barColors)
        return multiBarChartTableItems(style)
    }
}

@Composable
fun multiBarChartTableItems(
    currentStyle: StackedBarChartStyle,
): StyleItems {
    return ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = StackedBarChartDefaults.style(),
    )
}
