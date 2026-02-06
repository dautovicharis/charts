package io.github.dautovicharis.charts.style

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Returns a BarChartStyle with the provided parameters or their default values.
 *
 * @param barColor The color to be used for the bars in the chart. Defaults to the primary color of the MaterialTheme.
 * @param space The space between the bars in the chart. Defaults to 10.dp.
 * @param chartViewStyle The style to be applied to the chart view. Defaults to the default style of ChartViewDefaults.
 */
@Immutable
class StackedBarChartStyle internal constructor(
    internal val modifier: Modifier,
    internal val chartViewStyle: ChartViewStyle,
    val barColor: Color,
    val space: Dp,
    val barColors: List<Color>,
) : Style {
    /**
     * Returns a list of the properties of the StackedBarChartStyle.
     */
    override fun getProperties(): List<Pair<String, Any>> {
        return listOf(
            StackedBarChartStyle::barColor.name to barColor,
            StackedBarChartStyle::space.name to space,
            StackedBarChartStyle::barColors.name to barColors,
        )
    }
}

/**
 * An object that provides default styles for a Stacked Bar Chart.
 */
object StackedBarChartDefaults {
    /**
     * Returns a StackedBarChartStyle with the provided parameters or their default values.
     *
     * @param barColor The color to be used for the bars in the chart. Defaults to the primary color of the MaterialTheme.
     * @param space The space between the bars in the chart. Defaults to 10.dp.
     * @param barColors The colors to be used for the bars in the chart. Defaults to an empty list.
     * @param chartViewStyle The style to be applied to the chart view. Defaults to the default style of ChartViewDefaults.
     */
    @Composable
    fun style(
        barColor: Color = MaterialTheme.colorScheme.primary,
        space: Dp = 10.dp,
        barColors: List<Color> = emptyList(),
        chartViewStyle: ChartViewStyle = ChartViewDefaults.style(),
    ): StackedBarChartStyle {
        val padding = chartViewStyle.innerPadding
        val modifier: Modifier =
            Modifier
                .padding(padding)
                .aspectRatio(1f)
                .fillMaxSize()

        return StackedBarChartStyle(
            modifier = modifier,
            barColor = barColor,
            space = space,
            barColors = barColors,
            chartViewStyle = chartViewStyle,
        )
    }
}
