package io.github.dautovicharis.charts.internal.stackedareachart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import io.github.dautovicharis.charts.internal.common.model.normalizeStackedAreaValues
import io.github.dautovicharis.charts.internal.linechart.cubicControlPointsForSegment
import io.github.dautovicharis.charts.style.StackedAreaChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun StackedAreaChart(
    data: MultiChartData,
    style: StackedAreaChartStyle,
    areaColors: ImmutableList<Color>,
    lineColors: ImmutableList<Color>,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    onValueChanged: (Int) -> Unit = {},
) {
    val isPreview = LocalInspectionMode.current
    var show by rememberShowState(isPreviewMode = isPreview || !animateOnStart)
    val targetNormalized = remember(data) { data.normalizeStackedAreaValues() }
    val pointsCount = data.getFirstPointsSize()
    val seriesCount = data.items.size
    val valueAnimationSpec = remember { AnimationSpec.lineChart() }
    val revealProgress by animateFloatAsState(
        targetValue = if (show) ANIMATION_TARGET else 0f,
        animationSpec = AnimationSpec.lineChart(),
        label = "stackedAreaReveal",
    )
    val animatedValues =
        remember(seriesCount, pointsCount, isPreview, animateOnStart) {
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
    var selectedIndex by remember { mutableIntStateOf(NO_SELECTION) }

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
                val targetSeries = targetNormalized.getOrNull(seriesIndex).orEmpty()
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
                val targetSeries = targetNormalized.getOrNull(seriesIndex).orEmpty()
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
        hasInitialized.value = true
    }

    val interactionModifier =
        if (interactionEnabled) {
            Modifier.pointerInput(pointsCount) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        selectedIndex =
                            selectedIndexForTouch(
                                touchX = offset.x,
                                width = size.width.toFloat(),
                                pointsCount = pointsCount,
                            )
                        onValueChanged(selectedIndex)
                    },
                    onHorizontalDrag = { change, _ ->
                        selectedIndex =
                            selectedIndexForTouch(
                                touchX = change.position.x,
                                width = size.width.toFloat(),
                                pointsCount = pointsCount,
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
                    },
                )
            }
        } else {
            Modifier
        }

    Canvas(
        modifier =
            style.modifier
                .testTag(TestTags.STACKED_AREA_CHART)
                .then(interactionModifier)
                .onGloballyPositioned { show = true },
    ) {
        val stackedUpperBounds =
            animatedValues.map { series ->
                series.map { value -> value.value * size.height }
            }
        val emptyLower = List(pointsCount) { 0f }

        stackedUpperBounds.forEachIndexed { index, upperSeries ->
            val lowerSeries = stackedUpperBounds.getOrNull(index - 1) ?: emptyLower
            drawStackedAreaSeries(
                upperSeries = upperSeries,
                lowerSeries = lowerSeries,
                fillColor = areaColors[index].copy(alpha = style.fillAlpha),
                bezier = style.bezier,
                revealProgress = revealProgress,
            )
            if (style.lineVisible && style.lineWidth > 0f) {
                drawStackedAreaLine(
                    upperSeries = upperSeries,
                    lineColor = lineColors[index],
                    lineWidth = style.lineWidth,
                    bezier = style.bezier,
                    revealProgress = revealProgress,
                )
            }
        }
    }
}

private fun DrawScope.drawStackedAreaSeries(
    upperSeries: List<Float>,
    lowerSeries: List<Float>,
    fillColor: Color,
    bezier: Boolean,
    revealProgress: Float,
) {
    if (upperSeries.size <= 1 || lowerSeries.size <= 1 || size.width <= 0f) return

    val upperPoints = buildSeriesPoints(upperSeries)
    val lowerPoints = buildSeriesPoints(lowerSeries)

    val areaPath =
        Path().apply {
            moveTo(upperPoints.first().x, upperPoints.first().y)
            appendSeriesPath(points = upperPoints, bezier = bezier)
            if (bezier) {
                lineTo(lowerPoints.last().x, lowerPoints.last().y)
                appendSeriesPath(points = lowerPoints.asReversed(), bezier = true)
            } else {
                for (index in lowerPoints.lastIndex downTo 0) {
                    val point = lowerPoints[index]
                    lineTo(point.x, point.y)
                }
            }
            close()
        }

    val revealX = (size.width * revealProgress).coerceIn(0f, size.width)
    if (revealProgress >= ANIMATION_TARGET) {
        drawPath(
            path = areaPath,
            color = fillColor,
        )
    } else {
        clipRect(left = 0f, top = 0f, right = revealX, bottom = size.height) {
            drawPath(
                path = areaPath,
                color = fillColor,
            )
        }
    }
}

private fun DrawScope.drawStackedAreaLine(
    upperSeries: List<Float>,
    lineColor: Color,
    lineWidth: Float,
    bezier: Boolean,
    revealProgress: Float,
) {
    if (upperSeries.size <= 1 || size.width <= 0f) return

    val linePoints = buildSeriesPoints(upperSeries)
    val linePath =
        Path().apply {
            moveTo(linePoints.first().x, linePoints.first().y)
            appendSeriesPath(points = linePoints, bezier = bezier)
        }

    val revealX = (size.width * revealProgress).coerceIn(0f, size.width)
    if (revealProgress >= ANIMATION_TARGET) {
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = lineWidth),
        )
    } else {
        clipRect(left = 0f, top = 0f, right = revealX + lineWidth, bottom = size.height) {
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = lineWidth),
            )
        }
    }
}

private fun DrawScope.buildSeriesPoints(values: List<Float>): List<Offset> {
    val stepX = size.width / (values.size - 1)
    return values.mapIndexed { index, value ->
        Offset(
            x = index * stepX,
            y = size.height - value.coerceIn(0f, size.height),
        )
    }
}

private fun Path.appendSeriesPath(
    points: List<Offset>,
    bezier: Boolean,
) {
    if (points.size <= 1) return

    if (!bezier) {
        for (index in 1 until points.size) {
            val point = points[index]
            lineTo(point.x, point.y)
        }
    } else {
        for (segmentStart in 0 until points.lastIndex) {
            val controls =
                cubicControlPointsForSegment(
                    points = points,
                    segmentStartIndex = segmentStart,
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
