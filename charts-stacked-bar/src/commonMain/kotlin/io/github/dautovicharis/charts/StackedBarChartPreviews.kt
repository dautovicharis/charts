package io.github.dautovicharis.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

private const val STACKED_BAR_CHART_TITLE = "Stacked Bar Chart"
private val CATEGORIES = listOf("Jan", "Feb", "Mar")

private val STACKED_VALUES =
    listOf(
        "Item 1" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Item 2" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Item 3" to listOf(1500.87f, 2765.58f, 33245.81f),
        "Item 4" to listOf(5444.87f, 233.58f, 67544.81f),
    )

private val STACKED_INVALID_VALUES =
    listOf(
        "Item 1" to listOf(8261.68f, 8810.34f, 30000.57f),
        "Item 2" to listOf(8261.68f, 8810.34f),
        "Item 3" to listOf(1500.87f, 2765.58f, 33245.81f),
        "Item 4" to listOf(5444.87f, 233.58f),
    )

@Composable
private fun StackedBarChartPreviewContent() {
    StackedBarChart(
        dataSet =
            STACKED_VALUES.toMultiChartDataSet(
                title = STACKED_BAR_CHART_TITLE,
                categories = CATEGORIES,
            ),
        style = StackedBarChartDefaults.style(),
    )
}

@ChartsPreviewLightDark
@Composable
private fun StackedBarChartPreview() {
    ChartsPreviewTheme {
        StackedBarChartPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun StackedBarChartErrorPreview() {
    val style =
        StackedBarChartDefaults.style(
            barColors = listOf(MaterialTheme.colorScheme.primary),
            space = 8.dp,
        )
    ChartsPreviewTheme {
        StackedBarChart(
            dataSet =
                STACKED_INVALID_VALUES.toMultiChartDataSet(
                    title = STACKED_BAR_CHART_TITLE,
                    categories = CATEGORIES.dropLast(1),
                ),
            style = style,
        )
    }
}
