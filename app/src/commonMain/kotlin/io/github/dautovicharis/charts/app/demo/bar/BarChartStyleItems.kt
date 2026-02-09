package io.github.dautovicharis.charts.app.demo.bar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.BarChartDefaults

object BarChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return ChartStyleItems(
            currentStyle = BarDemoStyle.default(),
            defaultStyle = BarChartDefaults.style(),
        )
    }
}
