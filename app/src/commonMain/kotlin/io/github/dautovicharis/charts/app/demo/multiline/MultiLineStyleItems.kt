package io.github.dautovicharis.charts.app.demo.multiline

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.demo.line.lineChartTableItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems

object MultiLineStyleItems {
    @Composable
    fun default(): StyleItems {
        val style = MultiLineDemoStyle.default()
        return lineChartTableItems(style)
    }

    @Composable
    fun custom(lineColors: List<Color>): StyleItems {
        val style = MultiLineDemoStyle.custom(lineColors = lineColors)
        return lineChartTableItems(style)
    }
}
