package io.github.dautovicharis.charts.app.demo.line

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle

object LineChartStyleItems {
    @Composable
    fun default(): StyleItems {
        val style = LineChartDefaults.style()
        return lineChartTableItems(style)
    }

    @Composable
    fun custom(): StyleItems {
        val style = LineDemoStyle.custom()
        return lineChartTableItems(style)
    }
}

@Composable
fun lineChartTableItems(currentStyle: LineChartStyle): StyleItems {
    return ChartStyleItems(
        currentStyle = currentStyle,
        defaultStyle = LineChartDefaults.style(),
    )
}
