package io.github.dautovicharis.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle

private const val PIE_CHART_TITLE = "Pie Chart"
private val PIE_VALUES = listOf(32f, 21f, 24f, 14f, 9f)
private val PIE_LABELS = listOf("North", "East", "South", "West", "Other")

@Composable
private fun PieChartPreviewContent() {
    val style: PieChartStyle =
        PieChartDefaults.style(
            pieColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.surface,
            donutPercentage = 0f,
            chartViewStyle =
                ChartViewDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    cornerRadius = 20.dp,
                    shadow = 15.dp,
                    innerPadding = 15.dp,
                ),
        )
    PieChart(
        dataSet =
            PIE_VALUES.toChartDataSet(
                title = PIE_CHART_TITLE,
                labels = PIE_LABELS,
            ),
        style = style,
    )
}

@ChartsPreviewLightDark
@Composable
private fun PieChartPreview() {
    ChartsPreviewTheme {
        PieChartPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun PieChartErrorPreview() {
    ChartsPreviewTheme {
        PieChart(
            dataSet = listOf(42f).toChartDataSet(title = PIE_CHART_TITLE),
            style = PieChartDefaults.style(),
        )
    }
}
