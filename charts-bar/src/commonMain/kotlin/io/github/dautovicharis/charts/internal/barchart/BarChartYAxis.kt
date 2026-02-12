package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.internal.TestTags
import kotlin.math.abs
import kotlin.math.roundToInt

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
    Layout(
        modifier = modifier.testTag(TestTags.BAR_CHART_Y_AXIS_LABELS),
        content = {
            ticks.forEach { tick ->
                Text(
                    text = tick.label,
                    style = TextStyle(color = color, fontSize = fontSize),
                    maxLines = 1,
                )
            }
        },
    ) { measurables, constraints ->
        val placeables =
            measurables.map { measurable ->
                measurable.measure(
                    Constraints(
                        minWidth = 0,
                        minHeight = 0,
                        maxWidth = constraints.maxWidth,
                        maxHeight = constraints.maxHeight,
                    ),
                )
            }
        val edgePadding = 4.dp.roundToPx()

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val tick = ticks.getOrNull(index) ?: return@forEachIndexed
                if (!tick.centerY.isFinite()) return@forEachIndexed
                if (tick.centerY < 0f || tick.centerY > constraints.maxHeight.toFloat()) return@forEachIndexed

                val rawY = (tick.centerY - placeable.height / 2f).roundToInt()
                val maxY = (constraints.maxHeight - placeable.height).coerceAtLeast(0)
                val y = rawY.coerceIn(0, maxY)
                val x = (constraints.maxWidth - placeable.width - edgePadding).coerceAtLeast(0)
                placeable.place(x, y)
            }
        }
    }
}

fun buildYAxisTicks(
    minValue: Double,
    maxValue: Double,
    labelCount: Int,
    chartHeightPx: Float,
): List<YAxisTick> {
    if (chartHeightPx <= 0f) return emptyList()
    val safeLabelCount = labelCount.coerceAtLeast(2)
    val steps = (safeLabelCount - 1).coerceAtLeast(1)
    val range = maxValue - minValue

    return (0..steps).map { step ->
        val progress = step / steps.toFloat()
        val value = maxValue - range * progress
        YAxisTick(
            label = formatAxisValue(value),
            centerY = chartHeightPx * progress,
        )
    }
}

fun formatAxisValue(value: Double): String {
    val rounded = ((value * 100.0).roundToInt()) / 100.0
    val normalized = if (abs(rounded) < 0.005) 0.0 else rounded
    return normalized.toString().removeSuffix(".0")
}
