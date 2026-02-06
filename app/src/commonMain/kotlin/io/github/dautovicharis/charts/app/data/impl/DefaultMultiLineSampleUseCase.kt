package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.MultiLineSampleData
import io.github.dautovicharis.charts.app.data.MultiLineSampleUseCase
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultMultiLineSampleUseCase : MultiLineSampleUseCase {
    private val multiLineCategories = listOf("Jan", "Feb", "Mar", "Apr")
    private val multiLineItems =
        listOf(
            "Cherry St." to listOf(26000.68f, 28000.34f, 32000.57f, 45000.57f),
            "Strawberry Mall" to listOf(15261.68f, 17810.34f, 40000.57f, 85000f),
            "Lime Av." to listOf(4000.87f, 5000.58f, 30245.81f, 135000.58f),
            "Apple Rd." to listOf(1000.87f, 9000.58f, 16544.81f, 100444.87f),
        )

    override fun initialMultiLineSample(
        title: String,
        prefix: String,
    ): MultiLineSampleData {
        val dataSet =
            multiLineItems.toMultiChartDataSet(
                prefix = prefix,
                categories = multiLineCategories,
                title = title,
            )
        return MultiLineSampleData(
            dataSet = dataSet,
            seriesKeys = multiLineItems.map { it.first },
        )
    }

    override fun multiLineSample(
        range: IntRange,
        title: String,
        prefix: String,
    ): MultiLineSampleData {
        val newItems =
            multiLineItems.map { (name, values) ->
                name to values.map { range.random().toFloat() }
            }
        val dataSet =
            newItems.toMultiChartDataSet(
                prefix = prefix,
                categories = multiLineCategories,
                title = title,
            )
        return MultiLineSampleData(
            dataSet = dataSet,
            seriesKeys = newItems.map { it.first },
        )
    }
}
