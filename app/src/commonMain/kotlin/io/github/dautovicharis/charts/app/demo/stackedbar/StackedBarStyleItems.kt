package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle

object StackedBarChartStyleItems {
    @Composable
    fun custom(): StyleItems {
        val style = StackedBarDemoStyle.custom()
        return multiBarChartTableItems(style)
    }
}

@Composable
fun multiBarChartTableItems(currentStyle: StackedBarChartStyle): StyleItems {
    return ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = StackedBarChartDefaults.style(),
    )
}
