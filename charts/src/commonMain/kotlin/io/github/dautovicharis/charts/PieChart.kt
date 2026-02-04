package io.github.dautovicharis.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.barstackedchart.generateColorShades
import io.github.dautovicharis.charts.internal.common.composable.Chart
import io.github.dautovicharis.charts.internal.common.composable.ChartErrors
import io.github.dautovicharis.charts.internal.common.composable.Legend
import io.github.dautovicharis.charts.internal.piechart.PieChart
import io.github.dautovicharis.charts.internal.piechart.calculatePercentages
import io.github.dautovicharis.charts.internal.validatePieData
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * A composable function that displays a Pie Chart.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 */
@Composable
fun PieChart(
    dataSet: ChartDataSet,
    style: PieChartStyle = PieChartDefaults.style(),
) {
    val pieChartColors = remember(
        style.pieColors,
        style.pieColor,
        dataSet.data.item.points.size
    ) {
        if (style.pieColors.isEmpty()) {
            generateColorShades(style.pieColor, dataSet.data.item.points.size)
        } else {
            style.pieColors.toImmutableList()
        }
    }

    val errors = remember(dataSet, style) {
        validatePieData(dataSet = dataSet, style = style)
    }

    if (errors.isNotEmpty()) {
        ChartErrors(style = style.chartViewStyle, errors = errors.toImmutableList())
    } else {
        PieChartContent(dataSet = dataSet, style = style, pieChartColors = pieChartColors)
    }
}

@Composable
private fun PieChartContent(
    dataSet: ChartDataSet,
    style: PieChartStyle,
    pieChartColors: ImmutableList<Color>
) {
    var title by remember(dataSet) { mutableStateOf(dataSet.data.label) }

    val piePercentages = remember(dataSet.data.item.points) {
        calculatePercentages(dataSet.data.item.points)
    }
    var selectedIndex by remember(dataSet) { mutableStateOf(NO_SELECTION) }

    Chart(chartViewsStyle = style.chartViewStyle) {
        Text(
            modifier = style.chartViewStyle.modifierTopTitle
                .testTag(TestTags.CHART_TITLE),
            text = title,
            style = style.chartViewStyle.styleTitle
        )
        PieChart(
            chartData = dataSet.data.item,
            colors = pieChartColors,
            style = style,
            chartStyle = style.chartViewStyle
        ) { index ->
            selectedIndex = index
            title = when (index) {
                NO_SELECTION -> dataSet.data.label
                else -> {
                    dataSet.data.item.labels[index]
                }
            }
        }

        AnimatedVisibility(
            visible = selectedIndex != NO_SELECTION,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            if (selectedIndex != NO_SELECTION) {
                Text(
                    modifier = style.chartViewStyle.modifierLegend.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "${piePercentages[selectedIndex]}%",
                    style = style.chartViewStyle.styleTitle,
                )
            }
        }

        if (style.legendVisible) {
            Legend(
                chartViewsStyle = style.chartViewStyle,
                legend = dataSet.data.item.labels,
                colors = pieChartColors
            )
        }
    }
}
