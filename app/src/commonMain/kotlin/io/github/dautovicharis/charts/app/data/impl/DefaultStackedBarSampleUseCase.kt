package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.StackedBarSampleData
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultStackedBarSampleUseCase : StackedBarSampleUseCase {
    private val stackedCategories = listOf("Jan", "Feb", "Mar")
    private val stackedItems = listOf(
        "Cherry St." to listOf(8261.68f, 8810.34f, 30000.57f),
        "Strawberry Mall" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Lime Av." to listOf(1500.87f, 2765.58f, 33245.81f),
        "Apple Rd." to listOf(5444.87f, 233.58f, 67544.81f)
    )

    override fun initialStackedBarSample(title: String, prefix: String): StackedBarSampleData {
        val dataSet = stackedItems.toMultiChartDataSet(
            title = title,
            prefix = prefix,
            categories = stackedCategories
        )
        return StackedBarSampleData(
            dataSet = dataSet,
            segmentKeys = stackedCategories
        )
    }

    override fun stackedBarSample(
        range: IntRange,
        title: String,
        prefix: String
    ): StackedBarSampleData {
        val newItems = stackedItems.map { (name, values) ->
            name to values.map { range.random().toFloat() }
        }
        val dataSet = newItems.toMultiChartDataSet(
            title = title,
            prefix = prefix,
            categories = stackedCategories
        )
        return StackedBarSampleData(
            dataSet = dataSet,
            segmentKeys = stackedCategories
        )
    }
}
