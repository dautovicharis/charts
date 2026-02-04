package io.github.dautovicharis.charts.internal.piechart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.ANIMATION_DURATION
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.DEFAULT_SCALE
import io.github.dautovicharis.charts.internal.MAX_SCALE
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.composable.rememberShowState
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.style.ChartViewStyle
import io.github.dautovicharis.charts.style.PieChartStyle

internal data class PieSlice(
    val startDeg: Float,
    val endDeg: Float,
    val sweepAngle: Float,
    val value: Double,
    val normalizedValue: Double
)

@Composable
internal fun PieChart(
    chartData: ChartData,
    style: PieChartStyle,
    chartStyle: ChartViewStyle,
    onSliceTouched: (Int) -> Unit = {},
) {
    var show by rememberShowState()
    val slices = remember(chartData) { createPieSlices(chartData) }
    var selectedIndex by remember { mutableIntStateOf(NO_SELECTION) }

    val selectedSliceAnimation = animateFloatAsState(
        targetValue = if (selectedIndex == NO_SELECTION) DEFAULT_SCALE else MAX_SCALE,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        label = "sliceAnimation"
    )

    val slicesAnimations = List(slices.size) { index ->
        animateFloatAsState(
            targetValue = if (show) DEFAULT_SCALE else 0f,
            animationSpec = AnimationSpec.pieChart(index),
            label = "scaleAnimation"
        )
    }

    val donutHoleAnimation by animateFloatAsState(
        targetValue = if (show) style.donutPercentage else 0f,
        animationSpec = AnimationSpec.pieChartDonut(),
        label = "donutHoleAnimation"
    )

    Box(modifier = style.modifier
        .testTag(TestTags.PIE_CHART)
        .onGloballyPositioned { show = true }
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                selectedIndex =
                    getSelectedIndex(
                        pointX = offset.x,
                        pointY = offset.y,
                        size = size,
                        slices = slices
                    )
                onSliceTouched(selectedIndex)
            }
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    selectedIndex =
                        getSelectedIndex(
                            pointX = offset.x,
                            pointY = offset.y,
                            size = size,
                            slices = slices
                        )
                    onSliceTouched(selectedIndex)
                },
                onDrag = { change, _ ->
                    selectedIndex =
                        getSelectedIndex(
                            pointX = change.position.x,
                            pointY = change.position.y,
                            size = size,
                            slices = slices
                        )
                    onSliceTouched(selectedIndex)
                    change.consume()
                },
                onDragEnd = {
                    selectedIndex = NO_SELECTION
                    onSliceTouched(selectedIndex)
                },
                onDragCancel = {
                    selectedIndex = NO_SELECTION
                    onSliceTouched(selectedIndex)
                }
            )
        }
        .drawWithCache {
            onDrawBehind {
                slices.forEachIndexed { i, slice ->
                    val scale = when (selectedIndex) {
                        NO_SELECTION -> slicesAnimations[i].value
                        i -> selectedSliceAnimation.value
                        else -> DEFAULT_SCALE
                    }

                    scale(scale) {
                        drawArc(
                            color = style.pieColors[i],
                            startAngle = slice.startDeg,
                            sweepAngle = slice.sweepAngle,
                            useCenter = true,
                            style = Fill
                        )
                        drawArc(
                            color = style.borderColor,
                            startAngle = slice.startDeg,
                            sweepAngle = slice.sweepAngle,
                            useCenter = true,
                            style = Stroke(width = style.borderWidth)
                        )
                    }
                }

                if (donutHoleAnimation > 0f) {
                    val totalRadius = size.width / 2
                    val innerRadius = totalRadius * (donutHoleAnimation / 100f)
                    drawCircle(
                        color = chartStyle.backgroundColor,
                        radius = innerRadius,
                        center = Offset(totalRadius, totalRadius)
                    )
                    drawCircle(
                        color = style.borderColor,
                        radius = innerRadius,
                        center = Offset(totalRadius, totalRadius),
                        style = Stroke(width = style.borderWidth)
                    )
                }
            }
        }
    )
}
