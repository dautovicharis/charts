package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.StackedBarSampleData
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultStackedBarSampleUseCase : StackedBarSampleUseCase {
    companion object {
        private const val DEFAULT_TITLE = "Quarterly Revenue by Region"
        private const val DEFAULT_PREFIX = "$"
        private val REFRESH_RANGE = 100..1000
    }

    private val stackedCategories = listOf("Q1", "Q2", "Q3", "Q4")
    private val stackedItems =
        listOf(
            "North America" to listOf(320f, 340f, 360f, 390f),
            "Europe" to listOf(210f, 230f, 245f, 260f),
            "Asia Pacific" to listOf(180f, 205f, 225f, 250f),
        )
    private val noCategoriesItems =
        listOf(
            "Online" to listOf(220f, 260f, 300f),
            "Retail" to listOf(180f, 210f, 240f),
            "Enterprise" to listOf(140f, 160f, 190f),
        )

    override fun initialStackedBarSample(): StackedBarSampleData {
        return StackedBarSampleData(
            dataSet =
                stackedItems.toMultiChartDataSet(
                    title = DEFAULT_TITLE,
                    prefix = DEFAULT_PREFIX,
                    categories = stackedCategories,
                ),
            segmentKeys = stackedCategories,
        )
    }

    override fun initialStackedBarNoCategoriesDataSet(): MultiChartDataSet =
        noCategoriesItems.toMultiChartDataSet(title = "Revenue Streams (No Period Labels)")

    override fun stackedBarRefreshRange(): IntRange = REFRESH_RANGE

    override fun stackedBarSample(range: IntRange): StackedBarSampleData {
        val newItems =
            stackedItems.map { (name, values) ->
                name to values.map { range.random().toFloat() }
            }
        val dataSet =
            newItems.toMultiChartDataSet(
                title = DEFAULT_TITLE,
                prefix = DEFAULT_PREFIX,
                categories = stackedCategories,
            )
        return StackedBarSampleData(
            dataSet = dataSet,
            segmentKeys = stackedCategories,
        )
    }
}
