package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.util.lerp
import io.github.dautovicharis.charts.internal.ANIMATION_TARGET
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.DEFAULT_SCALE
import io.github.dautovicharis.charts.internal.MAX_SCALE
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.composable.rememberAnimationState
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.style.BarChartStyle
import kotlin.math.abs

@Composable
internal fun BarChart(
    chartData: ChartData,
    style: BarChartStyle,
    onValueChanged: (Int) -> Unit = {}
) {
    val barColor = style.barColor
    val animationState = rememberAnimationState()
    val progress = remember(chartData) {
        chartData.points.map { animationState }
    }

    chartData.points.forEachIndexed { index, _ ->
        LaunchedEffect(chartData, index) {
            progress[index].animateTo(
                targetValue = ANIMATION_TARGET,
                animationSpec = AnimationSpec.barChart(index)
            )
        }
    }

    val maxValue = remember(chartData) { chartData.points.max() }
    val minValue = remember(chartData) { chartData.points.min() }
    var selectedIndex by remember { mutableIntStateOf(NO_SELECTION) }
    val spacingPx = with(LocalDensity.current) { style.space.toPx() }

    Canvas(
        modifier = style.modifier
        .testTag(TestTags.BAR_CHART)
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragStart = { offset ->
                    selectedIndex =
                        getSelectedIndex(
                            position = offset,
                            dataSize = chartData.points.count(),
                            canvasSize = size,
                            spacingPx = spacingPx
                        )
                    onValueChanged(selectedIndex)
                },
                onHorizontalDrag = { change, _ ->
                    selectedIndex =
                        getSelectedIndex(
                            position = change.position,
                            dataSize = chartData.points.count(),
                            canvasSize = size,
                            spacingPx = spacingPx
                        )
                    onValueChanged(selectedIndex)
                    change.consume()
                },
                onDragEnd = {
                    selectedIndex = NO_SELECTION
                    onValueChanged(NO_SELECTION)
                },
                onDragCancel = {
                    selectedIndex = NO_SELECTION
                    onValueChanged(NO_SELECTION)
                }
            )
        }, onDraw = {
        drawBars(
            style = style,
            size = size,
            chartData = chartData,
            progress = progress,
            selectedIndex = selectedIndex,
            barColor = barColor,
            maxValue = maxValue,
            minValue = minValue
        )
    })
}

private fun DrawScope.drawBars(
    style: BarChartStyle,
    size: Size,
    chartData: ChartData,
    progress: List<Animatable<Float, AnimationVector1D>>,
    selectedIndex: Int,
    barColor: Color,
    maxValue: Double,
    minValue: Double
) {
    val baselineY = size.height * (maxValue / (maxValue - minValue))
    val dataSize = chartData.points.size

    chartData.points.forEachIndexed { index, value ->
        val spacing = style.space.toPx()
        val barWidth = (size.width - spacing * (dataSize - 1)) / dataSize

        val selectedBarScale = if (index == selectedIndex) MAX_SCALE else DEFAULT_SCALE
        val finalBarHeight = size.height * selectedBarScale * (abs(value) / (maxValue - minValue))
        val barHeight = lerp(0f, finalBarHeight.toFloat(), progress[index].value)

        val top = if (value >= 0) baselineY - barHeight else baselineY
        val left = (barWidth + spacing) * index

        drawRect(
            color = barColor,
            topLeft = Offset(x = left, y = top.toFloat()),
            size = Size(
                width = barWidth * selectedBarScale,
                height = barHeight
            )
        )
    }
}
