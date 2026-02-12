package io.github.dautovicharis.charts.internal.linechart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.ANIMATION_TARGET
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.composable.rememberShowState
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.common.model.minMax
import io.github.dautovicharis.charts.internal.common.model.normalizeByMinMax
import io.github.dautovicharis.charts.style.LineChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val LINE_STROKE_WIDTH = 5f
private const val MARKER_REVEAL_DURATION_MS = 260
private const val MARKER_REVEAL_THRESHOLD = 0.999f
private const val MARKER_REVEAL_START_SCALE = 0.7f

@Composable
fun LineChart(
    data: MultiChartData,
    style: LineChartStyle,
    colors: ImmutableList<Color>,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    onValueChanged: (Int) -> Unit = {},
) {
    val isPreview = LocalInspectionMode.current
    var show by rememberShowState(isPreviewMode = isPreview || !animateOnStart)
    val touchX = remember { mutableFloatStateOf(0f) }
    val dragging = remember { mutableStateOf(false) }
    val reportedSelection = remember { mutableIntStateOf(NO_SELECTION) }
    val valueAnimationSpec = remember { AnimationSpec.lineChart() }

    val lineAnimation by animateFloatAsState(
        targetValue = if (show) ANIMATION_TARGET else 0f,
        animationSpec = AnimationSpec.lineChart(),
        label = "lineAnimation",
    )
    val markerRevealProgress by animateFloatAsState(
        targetValue =
            if (show && lineAnimation >= MARKER_REVEAL_THRESHOLD) {
                ANIMATION_TARGET
            } else {
                0f
            },
        animationSpec =
            tween(
                durationMillis = MARKER_REVEAL_DURATION_MS,
                easing = FastOutSlowInEasing,
            ),
        label = "lineMarkerReveal",
    )

    val minMax = remember(data) { data.minMax() }
    val targetNormalized = remember(data, minMax) { data.normalizeByMinMax(minMax, 0f) }
    val pointsCount = data.getFirstPointsSize()
    val seriesCount = data.items.size
    val bezierTension = LINE_CHART_BEZIER_TENSION
    val animatedValues =
        remember(seriesCount, pointsCount) {
            List(seriesCount) { seriesIndex ->
                List(pointsCount) { pointIndex ->
                    val initialValue =
                        when {
                            isPreview || !animateOnStart ->
                                targetNormalized.getOrNull(seriesIndex)?.getOrNull(pointIndex) ?: 0f
                            else -> 0f
                        }
                    Animatable(initialValue)
                }
            }
        }
    val hasInitialized = remember { mutableStateOf(false) }

    LaunchedEffect(show, targetNormalized) {
        if (pointsCount <= 0 || seriesCount == 0) return@LaunchedEffect
        if (!show && !isPreview) {
            animatedValues.forEach { series ->
                series.forEach { animatable -> animatable.snapTo(0f) }
            }
            hasInitialized.value = false
            return@LaunchedEffect
        }

        if (isPreview || !hasInitialized.value) {
            animatedValues.forEachIndexed { seriesIndex, series ->
                val targetSeries = targetNormalized.getOrNull(seriesIndex) ?: emptyList()
                series.forEachIndexed { pointIndex, animatable ->
                    val target = targetSeries.getOrNull(pointIndex) ?: 0f
                    animatable.snapTo(target)
                }
            }
            hasInitialized.value = true
            return@LaunchedEffect
        }

        coroutineScope {
            animatedValues.forEachIndexed { seriesIndex, series ->
                val targetSeries = targetNormalized.getOrNull(seriesIndex) ?: emptyList()
                series.forEachIndexed { pointIndex, animatable ->
                    val target = targetSeries.getOrNull(pointIndex) ?: 0f
                    launch {
                        val shouldAnimate = !isPreview && (animateOnStart || hasInitialized.value)
                        if (!shouldAnimate) {
                            animatable.snapTo(target)
                        } else {
                            animatable.animateTo(
                                targetValue = target,
                                animationSpec = valueAnimationSpec,
                            )
                        }
                    }
                }
            }
        }
    }

    val interactionModifier =
        if (interactionEnabled) {
            Modifier.pointerInput(pointsCount) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        dragging.value = true
                        touchX.floatValue = offset.x
                        val selectedIndex =
                            selectedIndexForTouch(
                                touchX = offset.x,
                                width = size.width.toFloat(),
                                pointsCount = pointsCount,
                            )
                        if (reportedSelection.intValue != selectedIndex) {
                            reportedSelection.intValue = selectedIndex
                            onValueChanged(selectedIndex)
                        }
                    },
                    onHorizontalDrag = { change, _ ->
                        touchX.floatValue = change.position.x
                        val selectedIndex =
                            selectedIndexForTouch(
                                touchX = change.position.x,
                                width = size.width.toFloat(),
                                pointsCount = pointsCount,
                            )
                        if (reportedSelection.intValue != selectedIndex) {
                            reportedSelection.intValue = selectedIndex
                            onValueChanged(selectedIndex)
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        dragging.value = false
                        if (reportedSelection.intValue != NO_SELECTION) {
                            reportedSelection.intValue = NO_SELECTION
                            onValueChanged(NO_SELECTION)
                        }
                    },
                    onDragCancel = {
                        dragging.value = false
                        if (reportedSelection.intValue != NO_SELECTION) {
                            reportedSelection.intValue = NO_SELECTION
                            onValueChanged(NO_SELECTION)
                        }
                    },
                )
            }
        } else {
            Modifier
        }

    Box(
        modifier =
            style.modifier
                .onGloballyPositioned {
                    show = true
                },
    ) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .testTag(TestTags.LINE_CHART),
            onDraw = {
                data.items.forEachIndexed { index, _ ->
                    val seriesValues = animatedValues.getOrNull(index).orEmpty()
                    val scaledValues = seriesValues.map { value -> value.value * size.height }
                    if (show) {
                        drawChartPath(
                            values = scaledValues,
                            style = style,
                            lineAnimationProgress = lineAnimation,
                            markerRevealProgress = markerRevealProgress,
                            bezierTension = bezierTension,
                            lineColor = colors[index],
                        )
                    }
                }
            },
        )

        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .then(interactionModifier),
            onDraw = {
                if (!dragging.value) return@Canvas
                data.items.forEachIndexed { index, _ ->
                    val seriesValues = animatedValues.getOrNull(index).orEmpty()
                    val scaledValues = seriesValues.map { value -> value.value * size.height }
                    drawDragMarker(
                        touchX = touchX.floatValue,
                        values = scaledValues,
                        style = style,
                        lineColor = colors[index],
                        bezierTension = bezierTension,
                    )
                }
            },
        )
    }
}

private fun DrawScope.drawChartPath(
    values: List<Float>,
    style: LineChartStyle,
    lineAnimationProgress: Float,
    markerRevealProgress: Float,
    bezierTension: Float,
    lineColor: Color,
) {
    val valuesSize = values.size
    val canvasWidth = size.width
    val canvasHeight = size.height
    val valuesLastIndex = valuesSize - 1
    val stepX = canvasWidth / valuesLastIndex

    val path =
        Path().apply {
            val initX = 0f
            val initY = canvasHeight - values.first()
            moveTo(initX, initY)

            if (!style.bezier) {
                for (i in 1 until valuesSize) {
                    val x = i * stepX
                    val y = canvasHeight - values[i]
                    lineTo(x, y)
                }
            } else {
                val points =
                    List(valuesSize) { index ->
                        Offset(
                            x = index * stepX,
                            y = canvasHeight - values[index],
                        )
                    }
                for (segmentStart in 0 until points.lastIndex) {
                    val controls =
                        cubicControlPointsForSegment(
                            points = points,
                            segmentStartIndex = segmentStart,
                            tension = bezierTension,
                        )
                    val segmentEnd = points[segmentStart + 1]
                    cubicTo(
                        controls.first.x,
                        controls.first.y,
                        controls.second.x,
                        controls.second.y,
                        segmentEnd.x,
                        segmentEnd.y,
                    )
                }
            }
        }

    val lineStroke =
        Stroke(
            width = LINE_STROKE_WIDTH,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

    if (lineAnimationProgress >= ANIMATION_TARGET) {
        drawPath(
            path = path,
            color = lineColor,
            style = lineStroke,
        )
    } else {
        val revealX = (canvasWidth * lineAnimationProgress).coerceIn(0f, canvasWidth)
        // Reveal from left to right to keep perceived speed steady across steep curves.
        clipRect(left = 0f, top = 0f, right = revealX + LINE_STROKE_WIDTH, bottom = canvasHeight) {
            drawPath(
                path = path,
                color = lineColor,
                style = lineStroke,
            )
        }
    }

    tryDrawPathPoints(
        values = values,
        style = style,
        lineColor = lineColor,
        markerRevealProgress = markerRevealProgress,
    )
}

private fun DrawScope.tryDrawPathPoints(
    values: List<Float>,
    style: LineChartStyle,
    lineColor: Color,
    markerRevealProgress: Float,
) {
    if (!style.pointVisible || values.size <= 1 || size.width <= 0f || markerRevealProgress <= 0f) return

    val pointColor =
        when (style.pointColorSameAsLine) {
            true -> lineColor
            else -> style.pointColor
        }
    val progress = markerRevealProgress.coerceIn(0f, 1f)
    val animatedColor = pointColor.copy(alpha = pointColor.alpha * progress)
    val animatedRadius = style.pointSize * (MARKER_REVEAL_START_SCALE + (1f - MARKER_REVEAL_START_SCALE) * progress)

    val stepX = size.width / (values.size - 1)
    for (i in values.indices) {
        val x = i * stepX
        val y = size.height - values[i]
        drawCircle(
            color = animatedColor,
            radius = animatedRadius,
            center = Offset(x, y),
        )
    }
}

private fun DrawScope.drawDragMarker(
    touchX: Float,
    values: List<Float>,
    style: LineChartStyle,
    lineColor: Color,
    bezierTension: Float,
) {
    if ((!style.dragPointVisible && !style.pointVisible) || values.size <= 1 || size.width <= 0f) return

    val selectedIndex =
        selectedIndexForTouch(
            touchX = touchX,
            width = size.width,
            pointsCount = values.size,
        )
    if (selectedIndex == NO_SELECTION) return

    val dragPointColor =
        when (style.dragPointColorSameAsLine) {
            true -> lineColor
            else -> style.dragPointColor
        }

    if (style.pointVisible) {
        val stepX = size.width / (values.size - 1)
        val selectedX = selectedIndex * stepX
        val selectedY = size.height - values[selectedIndex]
        drawCircle(
            center = Offset(selectedX, selectedY),
            radius = style.dragActivePointSize,
            color = dragPointColor,
        )
    }

    if (style.dragPointVisible) {
        val nearestPoint =
            findNearestPoint(
                touchX = touchX,
                scaledValues = values,
                size = size,
                bezier = style.bezier,
                bezierTension = bezierTension,
            )

        val draggingCircleOffset =
            Offset(
                nearestPoint.x.coerceIn(0f, size.width),
                nearestPoint.y.coerceIn(0f, size.height),
            )

        drawCircle(
            center = draggingCircleOffset,
            radius = style.dragPointSize,
            color = dragPointColor,
        )
    }
}

private fun selectedIndexForTouch(
    touchX: Float,
    width: Float,
    pointsCount: Int,
): Int {
    if (pointsCount <= 1 || width <= 0f) return NO_SELECTION
    return ((touchX / width) * (pointsCount - 1))
        .toInt()
        .coerceIn(0, pointsCount - 1)
}
