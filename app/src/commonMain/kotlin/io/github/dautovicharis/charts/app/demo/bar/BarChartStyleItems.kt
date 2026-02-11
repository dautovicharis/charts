package io.github.dautovicharis.charts.app.demo.bar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleItems
import io.github.dautovicharis.charts.app.ui.composable.StyleItems
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.ChartViewDefaults

object BarChartStyleItems {
    @Composable
    fun default(): StyleItems {
        return ChartStyleItems(
            currentStyle = BarChartDefaults.style(),
            defaultStyle = BarChartDefaults.style(),
        )
    }

    @Composable
    fun customStyle(
        minValue: Float,
        maxValue: Float,
    ) = ChartTestStyleFixtures.barCustomStyle(
        chartViewStyle = ChartViewDefaults.style(),
        minValue = minValue,
        maxValue = maxValue,
    )

    @Composable
    fun custom(
        minValue: Float,
        maxValue: Float,
    ): StyleItems {
        return ChartStyleItems(
            currentStyle = customStyle(minValue = minValue, maxValue = maxValue),
            defaultStyle = BarChartDefaults.style(),
        )
    }
}
