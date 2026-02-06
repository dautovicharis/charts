package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.barstackedchart.generateColorShades
import io.github.dautovicharis.charts.internal.common.composable.Chart
import io.github.dautovicharis.charts.internal.common.composable.ChartErrors
import io.github.dautovicharis.charts.internal.common.composable.Legend
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.validateLineData
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun LineChartImpl(
    data: MultiChartData,
    style: LineChartStyle = LineChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true
) {
    val errors = remember(data, style) {
        validateLineData(
            data = data,
            style = style
        )
    }

    if (errors.isEmpty()) {
        var title by remember(data) { mutableStateOf(data.title) }
        var labels by remember(data) {
            mutableStateOf<ImmutableList<String>>(persistentListOf())
        }

        val lineColors = remember(data, style.lineColors, style.lineColor) {
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
                    modifier = style.chartViewStyle.modifierTopTitle
                        .testTag(TestTags.CHART_TITLE),
                    text = title,
                    style = style.chartViewStyle.styleTitle
                )
            }

            LineChart(
                data = data,
                style = style,
                colors = lineColors,
                interactionEnabled = interactionEnabled,
                animateOnStart = animateOnStart
            ) { selectedIndex ->
                title = data.getLabel(selectedIndex)

                if (data.hasCategories()) {
                    labels = when (selectedIndex) {
                        NO_SELECTION -> persistentListOf()
                        else -> data.items.map { it.item.labels[selectedIndex] }.toImmutableList()
                    }
                }
            }

            if (data.hasCategories()) {
                Legend(
                    chartViewsStyle = style.chartViewStyle,
                    legend = data.items.map { it.label }.toImmutableList(),
                    colors = lineColors,
                    labels = labels
                )
            }
        }
    } else {
        ChartErrors(style = style.chartViewStyle, errors =  errors.toImmutableList())
    }
}
