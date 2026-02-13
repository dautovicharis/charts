package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    Chart(chartViewsStyle = style.chartViewStyle) {
        BarChart(
            chartData = dataSet.data.item,
            title = dataSet.data.label,
            style = style,
            interactionEnabled = interactionEnabled,
            animateOnStart = animateOnStart,
        )
    }
}
