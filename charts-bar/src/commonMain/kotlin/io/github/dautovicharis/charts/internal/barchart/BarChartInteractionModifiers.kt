package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import io.github.dautovicharis.charts.internal.common.interaction.buildHorizontalDragGestureModifier
import io.github.dautovicharis.charts.internal.common.interaction.buildPinchZoomModifier
import io.github.dautovicharis.charts.internal.common.interaction.buildTapGestureModifier
import kotlin.math.roundToInt

fun buildFitTapModifier(
    interactionEnabled: Boolean,
    isScrollable: Boolean,
    dataSize: Int,
    spacingPx: Float,
    viewportWidthPx: Float,
    chartHeightPx: Float,
    onTapIndex: (Int) -> Unit,
): Modifier {
    return buildTapGestureModifier(
        interactionEnabled && !isScrollable,
        dataSize,
        spacingPx,
        viewportWidthPx,
        chartHeightPx,
        onTap = { offset ->
            val index =
                getSelectedIndex(
                    position = offset,
                    dataSize = dataSize,
                    canvasSize =
                        IntSize(
                            width = viewportWidthPx.roundToInt(),
                            height = chartHeightPx.roundToInt(),
                        ),
                    spacingPx = spacingPx,
                )
            onTapIndex(index)
        },
    )
}

fun buildFitDragModifier(
    interactionEnabled: Boolean,
    dragSelectionEnabled: Boolean,
    isScrollable: Boolean,
    dataSize: Int,
    spacingPx: Float,
    viewportWidthPx: Float,
    chartHeightPx: Float,
    onDragIndex: (Int) -> Unit,
    onDragFinished: () -> Unit,
): Modifier {
    return buildHorizontalDragGestureModifier(
        interactionEnabled && dragSelectionEnabled && !isScrollable,
        dataSize,
        spacingPx,
        viewportWidthPx,
        chartHeightPx,
        onDragStart = { offset ->
            val index =
                getSelectedIndex(
                    position = offset,
                    dataSize = dataSize,
                    canvasSize =
                        IntSize(
                            width = viewportWidthPx.roundToInt(),
                            height = chartHeightPx.roundToInt(),
                        ),
                    spacingPx = spacingPx,
                )
            onDragIndex(index)
        },
        onHorizontalDrag = { position ->
            val index =
                getSelectedIndex(
                    position = position,
                    dataSize = dataSize,
                    canvasSize =
                        IntSize(
                            width = viewportWidthPx.roundToInt(),
                            height = chartHeightPx.roundToInt(),
                        ),
                    spacingPx = spacingPx,
                )
            onDragIndex(index)
        },
        onDragEnd = { onDragFinished() },
        onDragCancel = { onDragFinished() },
    )
}

fun buildScrollTapModifier(
    interactionEnabled: Boolean,
    isScrollable: Boolean,
    dataSize: Int,
    unitWidthPx: Float,
    scrollState: ScrollState,
    onTapIndex: (Int) -> Unit,
    onDoubleTap: () -> Unit,
): Modifier {
    return buildTapGestureModifier(
        interactionEnabled && isScrollable,
        dataSize,
        unitWidthPx,
        onTap = { offset ->
            val contentX = offset.x + scrollState.value.toFloat()
            val index =
                getSelectedIndexForContentX(
                    contentX = contentX,
                    dataSize = dataSize,
                    unitWidthPx = unitWidthPx,
                )
            onTapIndex(index)
        },
        onDoubleTap = { onDoubleTap() },
    )
}

fun buildPinchModifier(
    isScrollable: Boolean,
    dataSize: Int,
    zoomMin: Float,
    zoomMax: Float,
    getZoomScale: () -> Float,
    setZoomScale: (Float) -> Unit,
): Modifier {
    return buildPinchZoomModifier(
        isScrollable,
        zoomMin,
        zoomMax,
        getZoomScale,
        setZoomScale,
        dataSize,
        zoomMin,
        zoomMax,
    )
}
