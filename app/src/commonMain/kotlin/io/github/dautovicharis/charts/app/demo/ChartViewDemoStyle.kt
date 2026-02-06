package io.github.dautovicharis.charts.app.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle

object ChartViewDemoStyle {
    // We are changing the default ChartView style just for demo purposes
    @Composable
    fun custom(width: Dp = chartWidth()): ChartViewStyle {
        return ChartViewDefaults.style(width = width)
    }
}

expect fun chartWidth(): Dp
