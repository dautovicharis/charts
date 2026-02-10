package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.barchart.BarChart
import io.github.dautovicharis.charts.internal.common.composable.Chart
import io.github.dautovicharis.charts.internal.common.composable.ChartErrors
import io.github.dautovicharis.charts.internal.validateBarData
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.BarChartStyle
import kotlinx.collections.immutable.toImmutableList

/**
 * A composable function that displays a Bar Chart.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 * @param interactionEnabled Enables touch interactions (tap selection and scroll/zoom). Defaults to true.
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 */
@Composable
fun BarChart(
    dataSet: ChartDataSet,
    style: BarChartStyle = BarChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
) {
    val errors =
        remember(dataSet) {
            validateBarData(
                data = dataSet.data.item,
            )
        }

    if (errors.isEmpty()) {
        BarChartContent(
            dataSet = dataSet,
            style = style,
            interactionEnabled = interactionEnabled,
            animateOnStart = animateOnStart,
        )
    } else {
        ChartErrors(style = style.chartViewStyle, errors = errors.toImmutableList())
    }
}

@Composable
private fun BarChartContent(
    dataSet: ChartDataSet,
    style: BarChartStyle,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
) {
    var title by remember(dataSet) { mutableStateOf(dataSet.data.label) }
    Chart(chartViewsStyle = style.chartViewStyle) {
        BarChart(
            chartData = dataSet.data.item,
            title = title,
            style = style,
            interactionEnabled = interactionEnabled,
            animateOnStart = animateOnStart,
        ) {
            title =
                when (it) {
                    NO_SELECTION -> dataSet.data.label
                    else -> resolveSelectedBarTitle(dataSet, it)
                }
        }
    }
}

private fun resolveSelectedBarTitle(
    dataSet: ChartDataSet,
    index: Int,
): String {
    val label = dataSet.data.item.labels.getOrNull(index).orEmpty().ifBlank { (index + 1).toString() }
    val value = dataSet.data.item.points.getOrNull(index) ?: return label
    return "$label: $value"
}
