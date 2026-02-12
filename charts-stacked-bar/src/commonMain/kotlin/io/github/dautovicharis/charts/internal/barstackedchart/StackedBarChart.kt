package io.github.dautovicharis.charts.internal.barstackedchart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import io.github.dautovicharis.charts.internal.AnimationSpec
import io.github.dautovicharis.charts.internal.DEFAULT_SCALE
import io.github.dautovicharis.charts.internal.NO_SELECTION
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.common.model.normalizeStackedValues
import io.github.dautovicharis.charts.style.StackedBarChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

private const val STACKED_BAR_SELECTED_SCALE = 1.02f

@Composable
fun StackedBarChart(
    data: MultiChartData,
    style: StackedBarChartStyle,
    colors: ImmutableList<Color>,
    interactionEnabled: Boolean,
    animateOnStart: Boolean,
    onValueChanged: (Int) -> Unit = {},
) {
    val isPreview = LocalInspectionMode.current
    val targetNormalized = remember(data, style) { data.normalizeStackedValues() }
    val initialValues =
        remember(data.items.size, isPreview, animateOnStart) {
            if (isPreview || !animateOnStart) targetNormalized else null
        }
    val animatedValues =
        remember(data.items.size, isPreview, animateOnStart) {
            data.items.mapIndexed { index, _ ->
                Animatable(initialValues?.getOrNull(index) ?: 0f)
            }
        }
    val hasInitialized = remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val spacingPx = with(LocalDensity.current) { style.space.toPx() }

    LaunchedEffect(targetNormalized) {
        if (data.items.isEmpty()) return@LaunchedEffect
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
                            animationSpec = AnimationSpec.stackedBar(0),
                        )
                    }
                }
            }
        }
        hasInitialized.value = true
    }

    val interactionModifier =
        if (interactionEnabled) {
            Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        selectedIndex =
                            getSelectedIndex(
                                position = offset,
                                dataSize = data.items.count(),
                                canvasSize = size,
                                spacingPx = spacingPx,
                            )
                        onValueChanged(selectedIndex)
                    },
                    onHorizontalDrag = { change, _ ->
                        selectedIndex =
                            getSelectedIndex(
                                position = change.position,
                                dataSize = data.items.count(),
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
                .testTag(TestTags.STACKED_BAR_CHART)
                .then(interactionModifier),
        onDraw = {
            drawBars(
                style = style,
                size = size,
                data = data,
                progress = animatedValues,
                selectedIndex = selectedIndex,
                colors = colors,
            )
        },
    )
}

private fun DrawScope.drawBars(
    style: StackedBarChartStyle,
    size: Size,
    data: MultiChartData,
    progress: List<Animatable<Float, AnimationVector1D>>,
    selectedIndex: Int,
    colors: ImmutableList<Color>,
) {
    val spacing = style.space.toPx()
    val barWidth = (size.width - spacing * (data.items.size - 1)) / data.items.size

    data.items.forEachIndexed { index, item ->
        var topOffset = size.height
        val selectedBarScale = if (index == selectedIndex) STACKED_BAR_SELECTED_SCALE else DEFAULT_SCALE
        val barTotal = item.item.points.sum()
        item.item.points.forEachIndexed { dataIndex, value ->
            val segmentShare =
                when {
                    barTotal == 0.0 -> 0f
                    else -> (value / barTotal).toFloat()
                }
            val height =
                stackedSegmentHeight(
                    segmentShare = segmentShare,
                    chartHeight = size.height,
                    barScale = selectedBarScale,
                    progress = progress.getOrNull(index)?.value ?: 0f,
                )
            topOffset -= height

            drawRect(
                color = colors[dataIndex],
                topLeft = Offset(x = index * (barWidth + spacing), y = topOffset),
                size =
                    Size(
                        width = barWidth * selectedBarScale,
                        height = height,
                    ),
            )
        }
    }
}

fun stackedSegmentHeight(
    segmentShare: Float,
    chartHeight: Float,
    barScale: Float,
    progress: Float,
): Float {
    return lerp(0f, segmentShare * chartHeight * barScale, progress)
}

private fun getSelectedIndex(
    position: Offset,
    dataSize: Int,
    canvasSize: IntSize,
    spacingPx: Float,
): Int {
    if (dataSize <= 0 || canvasSize.width <= 0) return 0

    val totalSpacing = spacingPx * (dataSize - 1)
    val availableWidth = max(1f, canvasSize.width - totalSpacing)
    val barWidth = availableWidth / dataSize
    val unitWidth = barWidth + spacingPx
    val index = (position.x / unitWidth).toInt()
    return index.coerceIn(0, dataSize - 1)
}
