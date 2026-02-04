package io.github.dautovicharis.charts

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.barstackedchart.StackedBarChart
import io.github.dautovicharis.charts.internal.barstackedchart.generateColorShades
import io.github.dautovicharis.charts.internal.common.composable.Chart
import io.github.dautovicharis.charts.internal.common.composable.ChartErrors
import io.github.dautovicharis.charts.internal.common.composable.Legend
import io.github.dautovicharis.charts.internal.validateBarData
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * A composable function that displays a Stacked Bar Chart.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 */
@Composable
fun StackedBarChart(
    dataSet: MultiChartDataSet,
    style: StackedBarChartStyle = StackedBarChartDefaults.style()
) {
    val errors = remember(dataSet, style) {
        validateBarData(
            data = dataSet.data,
            style = style
        )
    }

    if (errors.isEmpty()) {
        StackedBarChartContent(dataSet = dataSet, style = style)
    } else {
        ChartErrors(style = style.chartViewStyle, errors = errors.toImmutableList())
    }
}

@Composable
private fun StackedBarChartContent(dataSet: MultiChartDataSet, style: StackedBarChartStyle) {
    var title by remember(dataSet) { mutableStateOf(dataSet.data.title) }
    var labels by remember(dataSet) {
        mutableStateOf<ImmutableList<String>>(persistentListOf())
    }

    val colors: ImmutableList<androidx.compose.ui.graphics.Color> = remember(
        dataSet,
        style.barColors,
        style.barColor
    ) {
        if (style.barColors.isEmpty()) {
            generateColorShades(style.barColor, dataSet.data.getFirstPointsSize())
        } else {
            style.barColors.toImmutableList()
        }
    }

    Chart(chartViewsStyle = style.chartViewStyle) {
        Text(
            modifier = style.chartViewStyle.modifierTopTitle
                .testTag(TestTags.CHART_TITLE),
            text = title,
            style = style.chartViewStyle.styleTitle
        )

        StackedBarChart(
            data = dataSet.data,
            style = style,
            colors = colors
        ) { selectedIndex ->
            title = when (selectedIndex) {
                NO_SELECTION -> dataSet.data.title
                else -> {
                    dataSet.data.items[selectedIndex].label
                }
            }

            if (dataSet.data.hasCategories()) {
                labels = when (selectedIndex) {
                    NO_SELECTION -> persistentListOf()
                    else -> dataSet.data.items[selectedIndex].item.labels
                }
            }
        }

        if (dataSet.data.hasCategories()) {
            Legend(
                chartViewsStyle = style.chartViewStyle,
                colors = colors,
                legend = dataSet.data.categories,
                labels = labels
            )
        }
    }
}
