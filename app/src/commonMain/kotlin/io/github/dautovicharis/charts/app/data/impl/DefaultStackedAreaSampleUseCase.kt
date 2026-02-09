package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.StackedAreaSampleData
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultStackedAreaSampleUseCase : StackedAreaSampleUseCase {
    private val stackedAreaCategories = listOf("Jan", "Feb", "Mar", "Apr", "May")
    private val stackedAreaItems =
        listOf(
            "Cherry St." to listOf(16000.68f, 19000.34f, 24000.57f, 28000.57f, 31000.12f),
            "Strawberry Mall" to listOf(11000.68f, 15000.34f, 19000.57f, 23000f, 25000.45f),
            "Lime Av." to listOf(7000.87f, 9000.58f, 13000.81f, 15500.58f, 18000.16f),
            "Apple Rd." to listOf(4000.87f, 6500.58f, 9000.81f, 12000.87f, 14500.22f),
        )

    override fun initialStackedAreaSample(
        title: String,
        prefix: String,
    ): StackedAreaSampleData {
        val dataSet =
            stackedAreaItems.toMultiChartDataSet(
                prefix = prefix,
                categories = stackedAreaCategories,
                title = title,
            )
        return StackedAreaSampleData(
            dataSet = dataSet,
            seriesKeys = stackedAreaItems.map { it.first },
        )
    }

    override fun stackedAreaSample(
        range: IntRange,
        title: String,
        prefix: String,
    ): StackedAreaSampleData {
        val newItems =
            stackedAreaItems.map { (name, values) ->
                name to values.map { range.random().toFloat() }
            }
        val dataSet =
            newItems.toMultiChartDataSet(
                prefix = prefix,
                categories = stackedAreaCategories,
                title = title,
            )
        return StackedAreaSampleData(
            dataSet = dataSet,
            seriesKeys = newItems.map { it.first },
        )
    }
}
