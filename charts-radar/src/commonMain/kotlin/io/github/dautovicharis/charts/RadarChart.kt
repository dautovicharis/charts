package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.radarchart.RadarChartImpl
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle

/**
 * A composable function that displays a Radar Chart with a single data set.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 * @param interactionEnabled Enables touch interactions (drag selection). Defaults to true.
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 * @param selectedAxisIndex Optional preselected axis index for deterministic rendering (e.g. screenshots).
 */
@Composable
fun RadarChart(
    dataSet: ChartDataSet,
    style: RadarChartStyle = RadarChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
    selectedAxisIndex: Int = NO_SELECTION,
) {
    val data =
        remember(dataSet) {
            MultiChartData(
                items = listOf(dataSet.data),
                categories = dataSet.data.item.labels,
                title = dataSet.data.label,
            )
        }
    RadarChartImpl(
        data = data,
        style = style,
        interactionEnabled = interactionEnabled,
        animateOnStart = animateOnStart,
        selectedAxisIndex = selectedAxisIndex,
    )
}

/**
 * A composable function that displays a Radar Chart with multiple data sets.
 *
 * @param dataSet The data sets to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 * @param interactionEnabled Enables touch interactions (drag selection). Defaults to true.
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 * @param selectedAxisIndex Optional preselected axis index for deterministic rendering (e.g. screenshots).
 */
@Composable
fun RadarChart(
    dataSet: MultiChartDataSet,
    style: RadarChartStyle = RadarChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
    selectedAxisIndex: Int = NO_SELECTION,
) {
    RadarChartImpl(
        data = dataSet.data,
        style = style,
        interactionEnabled = interactionEnabled,
        animateOnStart = animateOnStart,
        selectedAxisIndex = selectedAxisIndex,
    )
}
