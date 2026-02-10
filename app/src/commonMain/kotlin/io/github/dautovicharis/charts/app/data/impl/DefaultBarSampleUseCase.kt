package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet

class DefaultBarSampleUseCase : BarSampleUseCase {
    override fun barDataSet(
        title: String,
        points: Int,
        range: IntRange,
    ): ChartDataSet {
        val safePoints = points.coerceAtLeast(2)
        val values = List(safePoints) { range.random().toFloat() }
        val labels = List(safePoints) { index -> (index + 1).toString() }
        return values.toChartDataSet(
            title = title,
            labels = labels,
        )
    }
}
