package io.github.dautovicharis.charts.internal.barstackedchart

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
import io.github.dautovicharis.charts.internal.barchart.getSelectedIndex
import io.github.dautovicharis.charts.internal.common.composable.rememberAnimationState
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.style.StackedBarChartStyle
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun StackedBarChart(
    data: MultiChartData,
    style: StackedBarChartStyle,
    colors: ImmutableList<Color>,
    onValueChanged: (Int) -> Unit = {}
) {
    val animationState = rememberAnimationState()
    val progress = remember {
        data.items.map { animationState }
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val spacingPx = with(LocalDensity.current) { style.space.toPx() }

    progress.forEachIndexed { index, _ ->
        LaunchedEffect(index) {
            progress[index].animateTo(
                targetValue = ANIMATION_TARGET,
                animationSpec = AnimationSpec.stackedBar(index)
            )
        }
    }

    Canvas(
        modifier = style.modifier
            .testTag(TestTags.STACKED_BAR_CHART)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        selectedIndex =
                            getSelectedIndex(
                                position = offset,
                                dataSize = data.items.count(),
                                canvasSize = size,
                                spacingPx = spacingPx
                            )
                        onValueChanged(selectedIndex)
                    },
                    onHorizontalDrag = { change, _ ->
                        selectedIndex =
                            getSelectedIndex(
                                position = change.position,
                                dataSize = data.items.count(),
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
                data = data,
                progress = progress,
                selectedIndex = selectedIndex,
                colors = colors
            )
        }
    )
}

private fun DrawScope.drawBars(
    style: StackedBarChartStyle,
    size: Size,
    data: MultiChartData,
    progress: List<Animatable<Float, AnimationVector1D>>,
    selectedIndex: Int,
    colors: ImmutableList<Color>
) {
    val totalMaxValue = data.items.maxOf { it.item.points.sum() }
    val spacing = style.space.toPx()
    val barWidth = (size.width - spacing * (data.items.size - 1)) / data.items.size

    data.items.forEachIndexed { index, item ->
        var topOffset = size.height
        val selectedBarScale = if (index == selectedIndex) MAX_SCALE else DEFAULT_SCALE
        item.item.points.forEachIndexed { dataIndex, value ->
            val height = lerp(
                0f,
                (value.toFloat() * selectedBarScale / totalMaxValue.toFloat()) * size.height,
                progress[index].value
            )
            topOffset -= height

            drawRect(
                color = colors[dataIndex],
                topLeft = Offset(x = index * (barWidth + spacing), y = topOffset),
                size = Size(
                    width = barWidth * selectedBarScale,
                    height = height
                )
            )
        }
    }
}
