package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults

private const val BAR_CHART_TITLE = "Bar Chart"
private val BAR_VALUES = listOf(18f, 32f, 26f, 48f, 36f, 28f, 54f)

@Composable
private fun BarChartPreviewContent() {
    BarChart(
        dataSet = BAR_VALUES.toChartDataSet(title = BAR_CHART_TITLE),
        style = BarChartDefaults.style(),
    )
}

@ChartsPreviewLightDark
@Composable
private fun BarChartPreview() {
    ChartsPreviewTheme {
        BarChartPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun BarChartErrorPreview() {
    ChartsPreviewTheme {
        BarChart(
            dataSet = listOf(42f).toChartDataSet(title = BAR_CHART_TITLE),
            style = BarChartDefaults.style(),
        )
    }
}
