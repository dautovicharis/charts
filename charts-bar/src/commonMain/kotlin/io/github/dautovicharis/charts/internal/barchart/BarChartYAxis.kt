package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.TextUnit
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.axis.AxisYLabelsLayout
import io.github.dautovicharis.charts.internal.common.axis.AxisYLayoutTick
import io.github.dautovicharis.charts.internal.common.axis.buildNumericYAxisTicks
import io.github.dautovicharis.charts.internal.common.axis.formatNumericAxisValue

data class YAxisTick(
    val label: String,
    val centerY: Float,
)

@Composable
fun BarYAxisLabels(
    ticks: List<YAxisTick>,
    color: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    AxisYLabelsLayout(
        ticks = ticks.map { tick -> AxisYLayoutTick(label = tick.label, centerY = tick.centerY) },
        color = color,
        fontSize = fontSize,
        modifier = modifier.testTag(TestTags.BAR_CHART_Y_AXIS_LABELS),
    )
}

fun buildYAxisTicks(
    minValue: Double,
    maxValue: Double,
    labelCount: Int,
    chartHeightPx: Float,
): List<YAxisTick> =
    buildNumericYAxisTicks(
        minValue = minValue,
        maxValue = maxValue,
        labelCount = labelCount,
        plotHeightPx = chartHeightPx,
        verticalInsetPx = 0f,
    ).map { tick ->
        YAxisTick(
            label = tick.label,
            centerY = tick.centerY,
        )
    }

fun formatAxisValue(value: Double): String = formatNumericAxisValue(value)
