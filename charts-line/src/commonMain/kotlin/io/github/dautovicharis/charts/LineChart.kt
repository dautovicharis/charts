package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.linechart.LineChartImpl
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.LineChartStyle

/**
 * A composable function that displays a Line Chart with a single data set.
 *
 * @param dataSet The data set to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 * @param interactionEnabled Enables touch interactions (drag selection). Defaults to true.
 * Ignored when [renderMode] is [LineChartRenderMode.Timeline].
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 * @param renderMode Chart transition mode. Defaults to [LineChartRenderMode.Morph].
 * @param animationDurationMillis Timeline transition duration. Used only when [renderMode]
 * is [LineChartRenderMode.Timeline].
 * Dense zoom/scroll style settings are applied in morph mode and ignored in timeline mode.
 */
@Composable
fun LineChart(
    dataSet: ChartDataSet,
    style: LineChartStyle = LineChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
    renderMode: LineChartRenderMode = LineChartRenderMode.Morph,
    animationDurationMillis: Int = 420,
) {
    val data =
        remember(dataSet) {
            MultiChartData(
                items = listOf(dataSet.data),
                title = dataSet.data.label,
            )
        }
    LineChartImpl(
        data = data,
        style = style,
        interactionEnabled = interactionEnabled,
        animateOnStart = animateOnStart,
        renderMode = renderMode,
        animationDurationMillis = animationDurationMillis,
    )
}

/**
 * A composable function that displays a Line Chart with multiple data sets.
 *
 * @param dataSet The data sets to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 * @param interactionEnabled Enables touch interactions (drag selection). Defaults to true.
 * Ignored when [renderMode] is [LineChartRenderMode.Timeline].
 * @param animateOnStart Enables initial chart animations. Defaults to true.
 * @param renderMode Chart transition mode. Defaults to [LineChartRenderMode.Morph].
 * @param animationDurationMillis Timeline transition duration. Used only when [renderMode]
 * is [LineChartRenderMode.Timeline].
 * Dense zoom/scroll style settings are applied in morph mode and ignored in timeline mode.
 */
@Composable
fun LineChart(
    dataSet: MultiChartDataSet,
    style: LineChartStyle = LineChartDefaults.style(),
    interactionEnabled: Boolean = true,
    animateOnStart: Boolean = true,
    renderMode: LineChartRenderMode = LineChartRenderMode.Morph,
    animationDurationMillis: Int = 420,
) {
    LineChartImpl(
        data = dataSet.data,
        style = style,
        interactionEnabled = interactionEnabled,
        animateOnStart = animateOnStart,
        renderMode = renderMode,
        animationDurationMillis = animationDurationMillis,
    )
}
