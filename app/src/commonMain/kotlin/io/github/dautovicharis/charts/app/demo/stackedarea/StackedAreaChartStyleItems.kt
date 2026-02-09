package io.github.dautovicharis.charts.app.demo.stackedarea

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults

object StackedAreaChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return ChartStyleItems(
            currentStyle = StackedAreaDemoStyle.default(),
            defaultStyle = StackedAreaChartDefaults.style(),
        )
    }

    @Composable
    fun custom(): StyleItems {
        return ChartStyleItems(
            currentStyle = StackedAreaDemoStyle.custom(areaColors = emptyList()),
            defaultStyle = StackedAreaChartDefaults.style(),
        )
    }

    @Composable
    fun custom(areaColors: List<Color>): StyleItems {
        return ChartStyleItems(
            currentStyle = StackedAreaDemoStyle.custom(areaColors = areaColors),
            defaultStyle = StackedAreaChartDefaults.style(),
        )
    }
}
