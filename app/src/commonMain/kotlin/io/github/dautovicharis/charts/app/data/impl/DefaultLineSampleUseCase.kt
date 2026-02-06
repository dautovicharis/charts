package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet

class DefaultLineSampleUseCase : LineSampleUseCase {
    private val lineInitialValues = listOf(8, 23, 54, 32, 12, 37, 7, 23, 43)

    override fun initialLineDataSet(title: String): ChartDataSet {
        return lineInitialValues.toChartDataSet(title = title)
    }

    override fun lineDataSet(range: IntRange, numOfPoints: IntRange, title: String): ChartDataSet {
        val points = numOfPoints.random()
        val values = List(points) { range.random() }
        return values.toChartDataSet(title = title)
    }
}
