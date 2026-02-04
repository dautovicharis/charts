package io.github.dautovicharis.charts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 */
@Composable
fun RadarChart(
    dataSet: ChartDataSet,
    style: RadarChartStyle = RadarChartDefaults.style()
) {
    val data = remember(dataSet) {
        MultiChartData(
            items = listOf(dataSet.data),
            categories = dataSet.data.item.labels,
            title = dataSet.data.label
        )
    }
    RadarChartImpl(
        data = data,
        style = style
    )
}

/**
 * A composable function that displays a Radar Chart with multiple data sets.
 *
 * @param dataSet The data sets to be displayed in the chart.
 * @param style The style to be applied to the chart. If not provided, the default style will be used.
 */
@Composable
fun RadarChart(
    dataSet: MultiChartDataSet,
    style: RadarChartStyle = RadarChartDefaults.style()
) {
    RadarChartImpl(
        data = dataSet.data,
        style = style
    )
}
