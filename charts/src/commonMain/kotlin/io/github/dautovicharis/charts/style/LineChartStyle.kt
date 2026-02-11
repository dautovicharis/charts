package io.github.dautovicharis.charts.style

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A class that defines the style for a Line Chart.
 *
 * @property modifier The modifier to be applied to the chart.
 * @property chartViewStyle The style to be applied to the chart view.
 * @property dragPointColorSameAsLine A boolean indicating whether the color of the drag point is the same as the line color.
 * @property pointColorSameAsLine A boolean indicating whether the color of the point is the same as the line color.
 * @property pointColor The color of the points on the line chart.
 * @property pointVisible A boolean indicating whether the points on the line chart are visible.
 * @property pointSize The size of the points on the line chart.
 * @property lineColor The color of the line in the line chart.
 * @property lineAlpha The alpha value applied to rendered line colors.
 * @property lineColors The colors of the lines in the line chart.
 * @property bezier A boolean indicating whether the line chart should be drawn with bezier curves.
 * @property dragPointSize The size of the drag point on the line chart.
 * @property dragPointVisible A boolean indicating whether the drag point on the line chart is visible.
 * @property dragActivePointSize The size of the active drag point on the line chart.
 * @property dragPointColor The color of the drag point on the line chart.
 */
@Immutable
class LineChartStyle internal constructor(
    internal val modifier: Modifier,
    internal val chartViewStyle: ChartViewStyle,
    internal val dragPointColorSameAsLine: Boolean,
    internal val pointColorSameAsLine: Boolean,
    val pointColor: Color,
    val pointVisible: Boolean,
    val pointSize: Float,
    val lineColor: Color,
    val lineAlpha: Float,
    val lineColors: List<Color>,
    val bezier: Boolean,
    val dragPointSize: Float,
    val dragPointVisible: Boolean,
    val dragActivePointSize: Float,
    val dragPointColor: Color,
) : Style {
    /**
     * Returns a list of the properties of the LineChartStyle.
     */
    override fun getProperties(): List<Pair<String, Any>> {
        return listOf(
            LineChartStyle::pointColor.name to pointColor,
            LineChartStyle::pointVisible.name to pointVisible,
            LineChartStyle::pointSize.name to pointSize,
            LineChartStyle::lineColor.name to lineColor,
            LineChartStyle::lineAlpha.name to lineAlpha,
            LineChartStyle::lineColors.name to lineColors,
            LineChartStyle::bezier.name to bezier,
            LineChartStyle::dragPointSize.name to dragPointSize,
            LineChartStyle::dragPointVisible.name to dragPointVisible,
            LineChartStyle::dragActivePointSize.name to dragActivePointSize,
            LineChartStyle::dragPointColor.name to dragPointColor,
        )
    }
}

/**
 * An object that provides default styles for a Line Chart.
 */
object LineChartDefaults {
    /**
     * Returns the default color for points on a Line Chart.
     */
    @Composable
    private fun defaultPointColor() = MaterialTheme.colorScheme.tertiary

    /**
     * Returns the default color for drag points on a Line Chart.
     */
    @Composable
    private fun defaultDragPointColor() = MaterialTheme.colorScheme.tertiary

    /**
     * Returns a LineChartStyle with the provided parameters or their default values.
     *
     * @param pointColor The color of the points on the line chart. Defaults to the tertiary color of the MaterialTheme.
     * @param pointSize The size of the points on the line chart. Defaults to 10f.
     * @param pointVisible A boolean indicating whether the points on the line chart are visible. Defaults to true.
     * @param lineColor The color of the line in the line chart. Defaults to the primary color of the MaterialTheme.
     * @param lineAlpha The alpha value applied to rendered line colors. Defaults to 0.4f in light theme and 0.6f in dark theme.
     * @param lineColors The colors of the lines in the line chart. Defaults to an empty list.
     * @param bezier A boolean indicating whether the line chart should be drawn with bezier curves. Defaults to true.
     * @param dragPointSize The size of the drag point on the line chart. Defaults to 7f.
     * @param dragPointVisible A boolean indicating whether the drag point on the line chart is visible. Defaults to true.
     * @param dragActivePointSize The size of the active drag point on the line chart. Defaults to 12f.
     * @param dragPointColor The color of the drag point on the line chart. Defaults to the tertiary color of the MaterialTheme.
     * @param chartViewStyle The style to be applied to the chart view. Defaults to the default style of ChartViewDefaults.
     */
    @Composable
    fun style(
        pointColor: Color = defaultPointColor(),
        pointSize: Float = 10f,
        pointVisible: Boolean = true,
        lineColor: Color = MaterialTheme.colorScheme.primary,
        lineAlpha: Float = defaultChartAlpha(),
        lineColors: List<Color> = emptyList(),
        bezier: Boolean = true,
        dragPointSize: Float = 7f,
        dragPointVisible: Boolean = true,
        dragActivePointSize: Float = 12f,
        dragPointColor: Color = defaultDragPointColor(),
        chartViewStyle: ChartViewStyle = ChartViewDefaults.style(),
    ): LineChartStyle {
        val padding = chartViewStyle.innerPadding
        val modifier: Modifier =
            Modifier
                .wrapContentSize()
                .padding(padding)
                .aspectRatio(1f)

        val pointColorSameAsLine = pointColor == defaultPointColor()
        val dragPointColorSameAsLine = pointColor == defaultDragPointColor()

        return LineChartStyle(
            modifier = modifier,
            pointColor = pointColor,
            lineColor = lineColor,
            lineAlpha = lineAlpha.coerceIn(0f, 1f),
            bezier = bezier,
            pointVisible = pointVisible,
            lineColors = lineColors,
            dragPointSize = dragPointSize,
            dragPointVisible = dragPointVisible,
            pointSize = pointSize,
            dragActivePointSize = dragActivePointSize,
            pointColorSameAsLine = pointColorSameAsLine,
            dragPointColor = dragPointColor,
            dragPointColorSameAsLine = dragPointColorSameAsLine,
            chartViewStyle = chartViewStyle,
        )
    }
}
