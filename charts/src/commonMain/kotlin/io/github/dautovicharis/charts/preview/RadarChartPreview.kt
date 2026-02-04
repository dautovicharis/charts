package io.github.dautovicharis.charts.preview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.RadarChartDefaults

private val PreviewCategories = listOf("Speed", "Strength", "Agility", "Stamina", "Skill", "Luck")

private fun previewMultiDataSet() = listOf(
    "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
    "Tiger" to listOf(65f, 88f, 60f, 82f, 55f, 68f)
).toMultiChartDataSet(
    title = "Radar Chart",
    categories = PreviewCategories
)

private fun previewSingleDataSet() = listOf(78f, 62f, 90f, 55f, 70f, 80f).toChartDataSet(
    title = "Radar Chart",
    labels = PreviewCategories
)

@Composable
private fun RadarChartPreviewContent(
    multiSeries: Boolean = true,
    categoryLegendVisible: Boolean = true,
    categoryPinsVisible: Boolean = true,
) {
    val lineColors = if (multiSeries) {
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    } else {
        listOf(MaterialTheme.colorScheme.primary)
    }

    val style = RadarChartDefaults.style(
        lineColors = lineColors,
        categoryLegendVisible = categoryLegendVisible,
        categoryPinsVisible = categoryPinsVisible
    )

    if (multiSeries) {
        RadarChart(
            dataSet = previewMultiDataSet(),
            style = style
        )
    } else {
        RadarChart(
            dataSet = previewSingleDataSet(),
            style = style
        )
    }
}

@Composable
private fun RadarChartPreviewThemed(
    content: @Composable () -> Unit
) {
    ChartsDefaultTheme(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = false
    ) {
        content()
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartDefault() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent()
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartHiddenCategoryLegend() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent(categoryLegendVisible = false)
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartHiddenCategoryLegendAndPins() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent(
            categoryLegendVisible = false,
            categoryPinsVisible = false
        )
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartSingleSeries() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent(multiSeries = false)
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartSingleSeriesHiddenCategoryLegend() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent(multiSeries = false, categoryLegendVisible = false)
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartSingleSeriesHiddenCategoryLegendAndPins() {
    RadarChartPreviewThemed {
        RadarChartPreviewContent(
            multiSeries = false,
            categoryLegendVisible = false,
            categoryPinsVisible = false
        )
    }
}

@ChartsPreviewLightDark
@Composable
private fun RadarChartError() {
    RadarChartPreviewThemed {
        RadarChart(
            dataSet = listOf(10f, 12f).toChartDataSet(
                title = "Radar Chart",
                labels = listOf("A", "B")
            ),
            style = RadarChartDefaults.style()
        )
    }
}
