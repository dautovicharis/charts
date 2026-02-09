package io.github.dautovicharis.charts.app.demo.pie

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.PieChartDefaults

object PieChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return ChartStyleItems(
            currentStyle = PieChartDemoStyle.default(),
            defaultStyle = PieChartDefaults.style(),
        )
    }

    @Composable
    fun custom(pieColors: List<Color>): StyleItems {
        return ChartStyleItems(
            currentStyle = PieChartDemoStyle.custom(pieColors = pieColors),
            defaultStyle = PieChartDefaults.style(),
        )
    }
}
