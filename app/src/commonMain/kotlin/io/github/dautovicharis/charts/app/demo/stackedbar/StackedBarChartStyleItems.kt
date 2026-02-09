package io.github.dautovicharis.charts.app.demo.stackedbar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

object StackedBarChartStyleItems {
    @Composable
    fun custom(): StyleItems {
        return ChartStyleItems(
            currentStyle = StackedBarDemoStyle.custom(),
            defaultStyle = StackedBarChartDefaults.style(),
        )
    }
}
