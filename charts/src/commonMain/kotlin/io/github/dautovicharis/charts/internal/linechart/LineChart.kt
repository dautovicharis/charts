package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.ANIMATION_TARGET
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.composable.rememberShowState
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.common.model.minMax
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun LineChart(
    data: MultiChartData,
    style: LineChartStyle,
    colors: ImmutableList<Color>,
    onValueChanged: (Int) -> Unit = {}
) {
    var show by rememberShowState()
    val touchX = remember { mutableFloatStateOf(0f) }
    val dragging = remember { mutableStateOf(false) }

    val lineAnimation by animateFloatAsState(
        targetValue = if (show) ANIMATION_TARGET else 0f,
        animationSpec = AnimationSpec.lineChart(),
        label = "lineAnimation"
    )

    val minMax = remember(data) { data.minMax() }
    Canvas(
        modifier = style.modifier
        .testTag(TestTags.LINE_CHART)
        .onGloballyPositioned {
            show = true
        }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragStart = { offset ->
                    dragging.value = true
                    touchX.floatValue = offset.x
                },
                onHorizontalDrag = { change, _ ->
                    touchX.floatValue = change.position.x
                    change.consume()
                },
                onDragEnd = {
                    dragging.value = false
                },
                onDragCancel = {
                    dragging.value = false
                }
            )
        },
        onDraw = {
            data.items.forEachIndexed { index, it ->
                val scaledValues = scaleValues(
                    values = it.item.points,
                    size = size,
                    minValue = minMax.first,
                    maxValue = minMax.second
                )
                if (show) {
                    drawChartPath(
                        values = scaledValues,
                        style = style,
                        lineAnimationProgress = lineAnimation,
                        dragging = dragging,
                        touchX = touchX,
                        lineColor = colors[index],
                        onValueChanged = onValueChanged
                    )
                }
            }
        }
    )
}

private fun DrawScope.drawChartPath(
    values: List<Float>,
    style: LineChartStyle,
    lineAnimationProgress: Float,
    dragging: MutableState<Boolean>,
    touchX: MutableFloatState,
    lineColor: Color,
    onValueChanged: (Int) -> Unit = {}
) {
    val valuesSize = values.size
    val canvasWidth = size.width
    val canvasHeight = size.height
    val valuesLastIndex = valuesSize - 1

    val path = Path().apply {
        val initX = 0f
        val initY = size.height - values.first()
        moveTo(initX, initY)

        for (i in 1 until valuesSize) {
            val x = i * (canvasWidth / valuesLastIndex)
            val y = canvasHeight - values[i]

            val prevX = (i - 1) * (canvasWidth / valuesLastIndex)
            val prevY = canvasHeight - values[i - 1]

            when (style.bezier) {
                true -> drawBezier(
                    prevX = prevX,
                    prevY = prevY,
                    currentX = x,
                    currentY = y
                )

                else -> lineTo(x, y)
            }
        }
    }

    if (lineAnimationProgress == ANIMATION_TARGET) {
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 5f)
        )
    } else {
        val pathMeasure = PathMeasure().apply {
            setPath(path, false)
        }

        val currentLength = lineAnimationProgress * pathMeasure.length
        val partialPath = Path().apply {
            pathMeasure.getSegment(0f, currentLength, this, true)
        }
        drawPath(
            path = partialPath,
            color = lineColor,
            style = Stroke(width = 5f)
        )
    }

    tryDrawPoints(
        touchX = touchX.floatValue,
        values = values,
        style = style,
        dragging = dragging.value,
        lineColor = lineColor,
        lineAnimationProgress = lineAnimationProgress,
        onValueChanged = onValueChanged
    )
}

private fun Path.drawBezier(
    prevX: Float,
    prevY: Float,
    currentX: Float,
    currentY: Float
) {
    val controlPointDiv = 2.2f
    val controlX1 = prevX + (currentX - prevX) / controlPointDiv
    val controlX2 = currentX - (currentX - prevX) / controlPointDiv

    cubicTo(
        controlX1, prevY,
        controlX2, currentY,
        currentX, currentY
    )
}

private fun DrawScope.tryDrawPoints(
    touchX: Float,
    values: List<Float>,
    style: LineChartStyle,
    dragging: Boolean,
    lineColor: Color,
    lineAnimationProgress: Float,
    onValueChanged: (Int) -> Unit
) {

    tryDrawDragPoints(
        touchX = touchX,
        values = values,
        style = style,
        dragging = dragging,
        lineColor = lineColor,
        onValueChanged = onValueChanged
    )

    tryDrawPathPoints(
        values = values,
        style = style,
        lineColor = lineColor,
        dragging = dragging,
        touchX = touchX,
        lineAnimationProgress = lineAnimationProgress
    )
}

private fun DrawScope.tryDrawPathPoints(
    values: List<Float>,
    style: LineChartStyle,
    lineColor: Color,
    dragging: Boolean,
    touchX: Float,
    lineAnimationProgress: Float
) {
    if (!style.pointVisible && !style.dragPointVisible) return

    val selectedIndex = (touchX / size.width * (values.size - 1))
        .toInt()
        .coerceIn(0, values.size - 1)

    val pointColor = when (style.pointColorSameAsLine) {
        true -> lineColor
        else -> style.pointColor
    }

    val dragPointColor = when (style.dragPointColorSameAsLine) {
        true -> lineColor
        else -> style.dragPointColor
    }

    val stepX = size.width / (values.size - 1)
    for (i in values.indices) {
        if (lineAnimationProgress < ANIMATION_TARGET) continue

        val x = i * stepX
        val y = size.height - values[i]

        // Determine circle radius based on dragging state
        val radius =
            if (selectedIndex == i && dragging) style.dragActivePointSize else style.pointSize

        val color = if (selectedIndex == i && dragging) dragPointColor else pointColor

        if (style.dragPointVisible && dragging) {
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )
        }

        if (style.pointVisible) {
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

private fun DrawScope.tryDrawDragPoints(
    touchX: Float,
    values: List<Float>,
    style: LineChartStyle,
    dragging: Boolean,
    lineColor: Color,
    onValueChanged: (Int) -> Unit
) {
    if (dragging) {
        val selectedIndex = (touchX / size.width * (values.size - 1))
            .toInt()
            .coerceIn(0, values.size - 1)
        onValueChanged(selectedIndex)
    } else {
        onValueChanged(NO_SELECTION)
    }

    if (style.dragPointVisible && dragging) {
        val dragPointColor = when (style.dragPointColorSameAsLine) {
            true -> lineColor
            else -> style.dragPointColor
        }

        val nearestPoint = findNearestPoint(
            touchX = touchX,
            scaledValues = values,
            size = size,
            bezier = style.bezier
        )

        val draggingCircleOffset = Offset(
            nearestPoint.x.coerceIn(0f, size.width),
            nearestPoint.y.coerceIn(0f, size.height)
        )

        drawCircle(
            center = draggingCircleOffset,
            radius = style.dragPointSize,
            color = dragPointColor
        )
    }
}
