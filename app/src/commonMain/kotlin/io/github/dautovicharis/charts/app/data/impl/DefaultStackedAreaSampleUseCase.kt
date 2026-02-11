package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.StackedAreaSampleData
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultStackedAreaSampleUseCase : StackedAreaSampleUseCase {
    companion object {
        private const val DEFAULT_TITLE = "Monthly Active Subscribers by Plan"
        private const val DEFAULT_PREFIX = ""
        private val REFRESH_RANGE = 100..1000
    }

    private val stackedAreaCategories = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    private val stackedAreaItems =
        listOf(
            "Free Plan" to listOf(620f, 650f, 690f, 720f, 760f, 800f),
            "Standard Plan" to listOf(240f, 260f, 285f, 310f, 340f, 365f),
            "Premium Plan" to listOf(90f, 95f, 105f, 118f, 130f, 142f),
        )
    private val noCategoriesItems =
        listOf(
            "Free Plan" to listOf(540f, 600f, 660f, 720f),
            "Standard Plan" to listOf(200f, 230f, 260f, 290f),
            "Premium Plan" to listOf(80f, 92f, 105f, 118f),
        )

    override fun initialStackedAreaSample(): StackedAreaSampleData {
        return StackedAreaSampleData(
            dataSet =
                stackedAreaItems.toMultiChartDataSet(
                    title = DEFAULT_TITLE,
                    prefix = DEFAULT_PREFIX,
                    categories = stackedAreaCategories,
                ),
            seriesKeys = stackedAreaItems.map { it.first },
        )
    }

    override fun initialStackedAreaNoCategoriesDataSet(): MultiChartDataSet =
        noCategoriesItems.toMultiChartDataSet(title = "Subscriber Mix (No Time Labels)")

    override fun stackedAreaRefreshRange(): IntRange = REFRESH_RANGE

    override fun stackedAreaSample(range: IntRange): StackedAreaSampleData {
        val newItems =
            stackedAreaItems.map { (name, values) ->
                name to values.map { range.random().toFloat() }
            }
        val dataSet =
            newItems.toMultiChartDataSet(
                prefix = DEFAULT_PREFIX,
                categories = stackedAreaCategories,
                title = DEFAULT_TITLE,
            )
        return StackedAreaSampleData(
            dataSet = dataSet,
            seriesKeys = newItems.map { it.first },
        )
    }
}
