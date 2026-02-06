package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.DEFAULT_SCALE
import io.github.dautovicharis.charts.internal.MAX_SCALE
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.internal.common.model.normalizeBarValues
import io.github.dautovicharis.charts.internal.common.model.resolveBarRange
import io.github.dautovicharis.charts.style.BarChartStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun BarChart(
    chartData: ChartData,
    style: BarChartStyle,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    onValueChanged: (Int) -> Unit = {},
) {
    val barColor = style.barColor
    val isPreview = LocalInspectionMode.current
    val valueAnimationSpec = remember { AnimationSpec.barChart(0) }
    val hasFixedRange = style.minValue != null || style.maxValue != null
    val (fixedMin, fixedMax) =
        remember(chartData, style) {
            chartData.resolveBarRange(style.minValue, style.maxValue)
        }
    val targetNormalized =
        remember(chartData, fixedMin, fixedMax, hasFixedRange) {
            chartData.normalizeBarValues(fixedMin, fixedMax, hasFixedRange)
        }
    val initialValues =
        remember(chartData.points.size, isPreview, animateOnStart) {
            if (isPreview || !animateOnStart) targetNormalized else null
        }
    val animatedValues =
        remember(chartData.points.size, isPreview, animateOnStart) {
            chartData.points.mapIndexed { index, _ ->
                Animatable(initialValues?.getOrNull(index) ?: 0f)
            }
        }
    val hasInitialized = remember { mutableStateOf(false) }

    LaunchedEffect(targetNormalized) {
        if (chartData.points.isEmpty()) return@LaunchedEffect
        coroutineScope {
            animatedValues.forEachIndexed { index, animatable ->
                val target = targetNormalized.getOrNull(index) ?: 0f
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
        hasInitialized.value = true
    }

    val maxValue = fixedMax
    val minValue = fixedMin
    var selectedIndex by remember { mutableIntStateOf(NO_SELECTION) }
    val spacingPx = with(LocalDensity.current) { style.space.toPx() }

    val interactionModifier =
        if (interactionEnabled) {
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        selectedIndex =
                            getSelectedIndex(
                                position = offset,
                                dataSize = chartData.points.count(),
                                canvasSize = size,
                                spacingPx = spacingPx,
                            )
                        onValueChanged(selectedIndex)
                    },
                    onHorizontalDrag = { change, _ ->
                        selectedIndex =
                            getSelectedIndex(
                                position = change.position,
                                dataSize = chartData.points.count(),
                                canvasSize = size,
                                spacingPx = spacingPx,
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
                .testTag(TestTags.BAR_CHART)
                .then(interactionModifier),
        onDraw = {
            drawBars(
                style = style,
                size = size,
                normalizedValues = animatedValues.map { it.value },
                selectedIndex = selectedIndex,
                barColor = barColor,
                maxValue = maxValue,
                minValue = minValue,
            )
        },
    )
}

private fun DrawScope.drawBars(
    style: BarChartStyle,
    size: Size,
    normalizedValues: List<Float>,
    selectedIndex: Int,
    barColor: Color,
    maxValue: Double,
    minValue: Double,
) {
    val rangeValue = maxValue - minValue
    val baselineY =
        when {
            rangeValue == 0.0 -> if (maxValue < 0.0) 0f else size.height
            else -> (size.height * (maxValue / rangeValue)).toFloat()
        }
    val clampedBaselineY =
        when {
            baselineY < 0f -> 0f
            baselineY > size.height -> size.height
            else -> baselineY
        }
    val dataSize = normalizedValues.size

    normalizedValues.forEachIndexed { index, value ->
        val spacing = style.space.toPx()
        val barWidth = (size.width - spacing * (dataSize - 1)) / dataSize

        val selectedBarScale = if (index == selectedIndex) MAX_SCALE else DEFAULT_SCALE
        val barHeight = abs(value) * size.height * selectedBarScale

        val top = if (value >= 0f) clampedBaselineY - barHeight else clampedBaselineY
        val left = (barWidth + spacing) * index

        drawRect(
            color = barColor,
            topLeft = Offset(x = left, y = top.toFloat()),
            size =
                Size(
                    width = barWidth * selectedBarScale,
                    height = barHeight,
                ),
        )
    }
}
