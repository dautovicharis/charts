package io.github.dautovicharis.charts.mock

import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet

internal object MockTest {
    const val TITLE = "Title"

    private val firstItem = listOf(26000.68f, 28000.34f, 32000.57f, 45000.57f)
    private val secondItem = listOf(26000.68f, 28000.34f, 32000.57f, 45000.57f)
    private val thirdItem = listOf(4000.87f, 5000.58f, 30245.81f, 135000.58f)
    private val fourthItem = listOf(1000.87f, 9000.58f, 16544.81f, 100444.87f)

    private val categories = listOf("Jan", "Feb", "Mar", "Apr")
    val colors = listOf(Color.Red, Color.Green, Color.Cyan, Color.Black)

    val dataSet: ChartDataSet =
        listOf(10f, 20f, 30f, 40f).toChartDataSet(
            title = TITLE,
        )

    val multiDataSet: MultiChartDataSet =
        listOf(
            "Item 1" to firstItem,
            "Item 2" to secondItem,
            "Item 3" to thirdItem,
            "Item 4" to fourthItem,
        ).toMultiChartDataSet(
            title = TITLE,
            categories = categories,
        )

    fun invalidMultiDataSet(): MultiChartDataSet =
        listOf(
            "Item 1" to firstItem.dropLast(1),
            "Item 2" to secondItem,
            "Item 3" to thirdItem.dropLast(1),
            "Item 4" to fourthItem,
        ).toMultiChartDataSet(
            title = TITLE,
            categories = categories.dropLast(1),
            prefix = "$",
        )
}
