package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.PieSampleData
import io.github.dautovicharis.charts.app.data.PieSampleUseCase
import io.github.dautovicharis.charts.model.toChartDataSet

class DefaultPieSampleUseCase : PieSampleUseCase {
    private val pieInitialValues = listOf(8, 23, 54, 32, 12, 37, 7, 23, 43)
    private val pieCustomLabels =
        listOf(
            "Public Transport",
            "Fuel",
            "Groceries",
            "Eating out",
            "Taxes",
            "Rent",
            "Entertainment",
            "Other",
        )

    override fun initialPieSample(
        title: String,
        postfix: String,
    ): PieSampleData {
        return buildPieSample(
            values = pieInitialValues,
            labels = emptyList(),
            title = title,
            postfix = postfix,
        )
    }

    override fun pieSample(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
        postfix: String,
    ): PieSampleData {
        val points = numOfPoints.random()
        val values = List(points) { range.random() }
        return buildPieSample(values = values, labels = emptyList(), title = title, postfix = postfix)
    }

    override fun pieCustomSample(
        range: IntRange,
        title: String,
        postfix: String,
    ): PieSampleData {
        val values = List(pieCustomLabels.size) { range.random() }
        return buildPieSample(values = values, labels = pieCustomLabels, title = title, postfix = postfix)
    }

    private fun buildPieSample(
        values: List<Int>,
        labels: List<String>,
        title: String,
        postfix: String,
    ): PieSampleData {
        val dataSet =
            values.toChartDataSet(
                title = title,
                postfix = postfix,
                labels = labels,
            )
        val segmentKeys =
            if (labels.isNotEmpty()) {
                labels
            } else {
                List(values.size) { index -> "Segment ${index + 1}" }
            }
        return PieSampleData(
            dataSet = dataSet,
            segmentKeys = segmentKeys,
        )
    }
}
