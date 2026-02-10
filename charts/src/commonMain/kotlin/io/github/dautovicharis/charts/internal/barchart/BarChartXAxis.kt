package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.model.ChartData
import kotlin.math.roundToInt

internal data class AxisTick(
    val label: String,
    val centerX: Float,
)

@Composable
internal fun BarXAxisLabels(
    ticks: List<AxisTick>,
    color: Color,
    fontSize: TextUnit,
    tiltDegrees: Float,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier.testTag(TestTags.BAR_CHART_X_AXIS_LABELS),
        content = {
            ticks.forEach { tick ->
                Text(
                    text = tick.label,
                    style = TextStyle(color = color, fontSize = fontSize),
                    maxLines = 1,
                    modifier =
                        if (tiltDegrees <= 0f) {
                            Modifier
                        } else {
                            Modifier.graphicsLayer {
                                // Positive tilt means labels rise to the right.
                                rotationZ = -tiltDegrees
                                transformOrigin = TransformOrigin(0f, 1f)
                            }
                        },
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
                if (!tick.centerX.isFinite()) return@forEachIndexed
                if (tick.centerX < 0f || tick.centerX > constraints.maxWidth.toFloat()) return@forEachIndexed
                val rawX = (tick.centerX - placeable.width / 2f).roundToInt()
                val maxX =
                    (constraints.maxWidth - placeable.width - edgePadding).coerceAtLeast(edgePadding)
                val x = rawX.coerceIn(edgePadding, maxX)
                val y = (constraints.maxHeight - placeable.height).coerceAtLeast(0)
                placeable.place(x, y)
            }
        }
    }
}

internal fun buildAxisTicks(
    chartData: ChartData,
    labelIndices: List<Int>,
    barWidthPx: Float,
    unitWidthPx: Float,
    scrollOffsetPx: Float,
): List<AxisTick> {
    return labelIndices.map { index ->
        AxisTick(
            label = resolveAxisLabel(labels = chartData.labels, index = index),
            centerX = index * unitWidthPx + barWidthPx / 2f - scrollOffsetPx,
        )
    }
}
