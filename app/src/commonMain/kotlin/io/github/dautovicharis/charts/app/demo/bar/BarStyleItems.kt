package io.github.dautovicharis.charts.app.demo.bar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.BarChartStyle

object BarChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return barChartTableItems(BarChartDefaults.style())
    }
}

@Composable
fun barChartTableItems(
    currentStyle: BarChartStyle,
): StyleItems {
    return ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = BarChartDefaults.style(),
    )
}
