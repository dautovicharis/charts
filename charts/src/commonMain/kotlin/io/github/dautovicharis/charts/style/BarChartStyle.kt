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
 * A class that defines the style for a Bar Chart.
 *
 * @property modifier The modifier to be applied to the chart.
 * @property chartViewStyle The style to be applied to the chart view.
 * @property barColor The color to be used for the bars in the chart.
 * @property space The space between the bars in the chart.
 * @property minValue Optional fixed minimum value for the chart scale.
 * @property maxValue Optional fixed maximum value for the chart scale.
 */
@Immutable
class BarChartStyle internal constructor(
    internal val modifier: Modifier,
    internal val chartViewStyle: ChartViewStyle,
    val barColor: Color,
    val space: Dp,
    val minValue: Float?,
    val maxValue: Float?,
) : Style {
    /**
     * Returns a list of the properties of the BarChartStyle.
     */
    override fun getProperties(): List<Pair<String, Any>> {
        return listOf(
            BarChartStyle::barColor.name to barColor,
            BarChartStyle::space.name to space,
            BarChartStyle::minValue.name to (minValue ?: "auto"),
            BarChartStyle::maxValue.name to (maxValue ?: "auto"),
        )
    }
}

/**
 * An object that provides default styles for a Bar Chart.
 */
object BarChartDefaults {
    /**
     * Returns a BarChartStyle with the provided parameters or their default values.
     *
     * @param barColor The color to be used for the bars in the chart. Defaults to the primary color of the MaterialTheme.
     * @param space The space between the bars in the chart. Defaults to 10.dp.
     * @param chartViewStyle The style to be applied to the chart view. Defaults to the default style of ChartViewDefaults.
     */
    @Composable
    fun style(
        barColor: Color = MaterialTheme.colorScheme.primary,
        space: Dp = 10.dp,
        minValue: Float? = null,
        maxValue: Float? = null,
        chartViewStyle: ChartViewStyle = ChartViewDefaults.style(),
    ): BarChartStyle {
        val padding = chartViewStyle.innerPadding
        val modifier: Modifier =
            Modifier
                .padding(padding)
                .aspectRatio(1f)
                .fillMaxSize()

        return BarChartStyle(
            modifier = modifier,
            barColor = barColor,
            space = space,
            minValue = minValue,
            maxValue = maxValue,
            chartViewStyle = chartViewStyle,
        )
    }
}
