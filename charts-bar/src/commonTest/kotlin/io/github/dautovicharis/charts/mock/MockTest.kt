package io.github.dautovicharis.charts.mock

import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet

internal object MockTest {
    const val TITLE = "Title"

    val dataSet: ChartDataSet =
        listOf(10f, 20f, 30f, 40f).toChartDataSet(
            title = TITLE,
        )
}
