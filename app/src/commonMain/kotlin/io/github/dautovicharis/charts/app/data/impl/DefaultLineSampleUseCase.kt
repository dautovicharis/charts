package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet

class DefaultLineSampleUseCase : LineSampleUseCase {
    private val lineInitialValues = listOf(10, 14, 19, 26, 33, 37, 34, 30, 35)

    override fun initialLineDataSet(title: String): ChartDataSet {
        return lineInitialValues.toChartDataSet(title = title)
    }

    override fun lineDataSet(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
    ): ChartDataSet {
        val points = numOfPoints.random()
        val values = List(points) { range.random() }
        return values.toChartDataSet(title = title)
    }
}
