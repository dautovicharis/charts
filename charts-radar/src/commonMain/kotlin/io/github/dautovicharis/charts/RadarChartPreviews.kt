package io.github.dautovicharis.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.RadarChartDefaults

private const val RADAR_CHART_TITLE = "Radar Chart"

private val RADAR_CATEGORIES = listOf("Speed", "Strength", "Agility", "Stamina", "Skill", "Luck")

private val RADAR_MULTI_SERIES =
    listOf(
        "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
        "Tiger" to listOf(65f, 88f, 60f, 82f, 55f, 68f),
    )

private val RADAR_SINGLE_SERIES = listOf(78f, 62f, 90f, 55f, 70f, 80f)

@Composable
private fun RadarChartMultiSeriesPreviewContent(
    categoryLegendVisible: Boolean = true,
    categoryPinsVisible: Boolean = true,
) {
    val style =
        RadarChartDefaults.style(
            lineColors =
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                ),
            categoryLegendVisible = categoryLegendVisible,
            categoryPinsVisible = categoryPinsVisible,
        )

    RadarChart(
        dataSet =
            RADAR_MULTI_SERIES.toMultiChartDataSet(
                title = RADAR_CHART_TITLE,
                categories = RADAR_CATEGORIES,
            ),
        style = style,
    )
}

@Composable
private fun RadarChartSingleSeriesPreviewContent() {
    RadarChart(
        dataSet =
            RADAR_SINGLE_SERIES.toChartDataSet(
                title = RADAR_CHART_TITLE,
                labels = RADAR_CATEGORIES,
            ),
        style = RadarChartDefaults.style(lineColors = listOf(MaterialTheme.colorScheme.primary)),
    )
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartMultiPreview() {
    ChartsPreviewTheme {
        RadarChartMultiSeriesPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartSinglePreview() {
    ChartsPreviewTheme {
        RadarChartSingleSeriesPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartHiddenLegendPreview() {
    ChartsPreviewTheme {
        RadarChartMultiSeriesPreviewContent(categoryLegendVisible = false, categoryPinsVisible = false)
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartErrorPreview() {
    ChartsPreviewTheme {
        RadarChart(
            dataSet =
                listOf(10f, 12f).toChartDataSet(
                    title = RADAR_CHART_TITLE,
                    labels = listOf("A", "B"),
                ),
            style = RadarChartDefaults.style(),
        )
    }
}
