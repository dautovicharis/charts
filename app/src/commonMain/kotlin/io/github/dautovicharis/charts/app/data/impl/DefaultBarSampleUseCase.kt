package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet

class DefaultBarSampleUseCase : BarSampleUseCase {
    private val barInitialValues = listOf(100f, 50f, 5f, 60f, -50f, 50f, 60f)

    override fun initialBarDataSet(title: String): ChartDataSet {
        return barInitialValues.toChartDataSet(title = title)
    }

    override fun barDataSet(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
    ): ChartDataSet {
        val points = numOfPoints.random()
        val values = List(points) { range.random().toFloat() }
        return values.toChartDataSet(title = title)
    }
}
