package io.github.dautovicharis.charts.internal.radarchart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.barstackedchart.generateColorShades
import io.github.dautovicharis.charts.internal.common.composable.Chart
import io.github.dautovicharis.charts.internal.common.composable.ChartErrors
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.validateRadarData
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun RadarChartImpl(
    data: MultiChartData,
    style: RadarChartStyle = RadarChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
) {
    val errors =
        remember(data, style) {
            validateRadarData(
                data = data,
                style = style,
            )
        }

    if (errors.isEmpty()) {
        var title by remember(data) { mutableStateOf(data.title) }

        val lineColors =
            remember(data, style.lineColors, style.lineColor) {
                if (data.hasSingleItem()) {
                    persistentListOf(style.lineColor)
                } else if (style.lineColors.isEmpty()) {
                    generateColorShades(style.lineColor, data.items.size)
                } else {
                    style.lineColors.toImmutableList()
                }
            }

        Chart(chartViewsStyle = style.chartViewStyle) {
            if (title.isNotBlank()) {
                Text(
                    modifier =
                        style.chartViewStyle.modifierTopTitle
                            .testTag(TestTags.CHART_TITLE),
                    text = title,
                    style = style.chartViewStyle.styleTitle,
                )
            }

            val categories = if (data.hasCategories()) data.categories else persistentListOf()
            val legendCategories =
                when (style.categoryLegendVisible) {
                    true -> categories
                    else -> persistentListOf()
                }
            val categoryColorsList =
                remember(categories, style.categoryColors) {
                    categoryColors(style, categories.size)
                }

            RadarChart(
                data = data,
                style = style,
                colors = lineColors,
                categoryColors = categoryColorsList,
                axisLabels = categories,
                interactionEnabled = interactionEnabled,
                animateOnStart = animateOnStart,
                onValueChanged = { selectedIndex ->
                    title = data.getLabel(selectedIndex)
                },
            )

            val series =
                if (data.hasSingleItem()) {
                    persistentListOf()
                } else {
                    data.items.map { it.label }.toImmutableList()
                }

            if (series.isNotEmpty() || legendCategories.isNotEmpty()) {
                RadarLegend(
                    chartViewsStyle = style.chartViewStyle,
                    series = series,
                    seriesColors = lineColors,
                    categories = legendCategories,
                    categoryColors = categoryColorsList,
                )
            }
        }
    } else {
        ChartErrors(style = style.chartViewStyle, errors = errors.toImmutableList())
    }
}
