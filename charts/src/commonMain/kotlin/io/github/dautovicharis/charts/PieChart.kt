package io.github.dautovicharis.charts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
 * @param interactionEnabled Enables touch interactions (tap/drag selection). Defaults to true.
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 * @param selectedSliceIndex Optional preselected slice index for deterministic rendering (e.g. screenshots).
 */
@Composable
fun PieChart(
    dataSet: ChartDataSet,
    style: PieChartStyle = PieChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
    selectedSliceIndex: Int = NO_SELECTION,
) {
    val pieChartColors =
        remember(
            style.pieColors,
            style.pieColor,
            dataSet.data.item.points.size,
        ) {
            if (style.pieColors.isEmpty()) {
                generateColorShades(style.pieColor, dataSet.data.item.points.size)
            } else {
                style.pieColors.toImmutableList()
            }
        }

    val errors =
        remember(dataSet, style) {
            validatePieData(dataSet = dataSet, style = style)
        }

    if (errors.isNotEmpty()) {
        ChartErrors(style = style.chartViewStyle, errors = errors.toImmutableList())
    } else {
        PieChartContent(
            dataSet = dataSet,
            style = style,
            pieChartColors = pieChartColors,
            interactionEnabled = interactionEnabled,
            animateOnStart = animateOnStart,
            selectedSliceIndex = selectedSliceIndex,
        )
    }
}

@Composable
private fun PieChartContent(
    dataSet: ChartDataSet,
    style: PieChartStyle,
    pieChartColors: ImmutableList<Color>,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    selectedSliceIndex: Int,
) {
    val piePercentages =
        remember(dataSet.data.item.points) {
            calculatePercentages(dataSet.data.item.points)
        }
    val forcedSelectedIndex =
        selectedSliceIndex.takeIf { it in dataSet.data.item.points.indices } ?: NO_SELECTION
    var selectedIndex by remember(dataSet) { mutableIntStateOf(NO_SELECTION) }
    val effectiveSelectedIndex =
        when (forcedSelectedIndex) {
            NO_SELECTION -> selectedIndex
            else -> forcedSelectedIndex
        }
    val title =
        when (effectiveSelectedIndex) {
            NO_SELECTION -> dataSet.data.label
            else -> dataSet.data.item.labels[effectiveSelectedIndex]
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
        PieChart(
            chartData = dataSet.data.item,
            colors = pieChartColors,
            style = style,
            chartStyle = style.chartViewStyle,
            interactionEnabled = interactionEnabled,
            animateOnStart = animateOnStart,
            selectedSliceIndex = forcedSelectedIndex,
        ) { index ->
            if (forcedSelectedIndex == NO_SELECTION) {
                selectedIndex = index
            }
        }

        AnimatedVisibility(
            visible = effectiveSelectedIndex != NO_SELECTION,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            if (effectiveSelectedIndex != NO_SELECTION) {
                Text(
                    modifier = style.chartViewStyle.modifierLegend.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "${piePercentages[effectiveSelectedIndex]}%",
                    style = style.chartViewStyle.styleTitle,
                )
            }
        }

        if (style.legendVisible) {
            Legend(
                chartViewsStyle = style.chartViewStyle,
                legend = dataSet.data.item.labels,
                colors = pieChartColors,
            )
        }
    }
}
