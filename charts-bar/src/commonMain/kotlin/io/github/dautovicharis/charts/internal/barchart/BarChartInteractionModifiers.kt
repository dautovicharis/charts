package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.roundToInt

private const val ZOOM_DELTA_EPSILON = 0.01f

fun buildFitTapModifier(
    interactionEnabled: Boolean,
    isScrollable: Boolean,
    dataSize: Int,
    spacingPx: Float,
    viewportWidthPx: Float,
    chartHeightPx: Float,
    onTapIndex: (Int) -> Unit,
): Modifier {
    if (!interactionEnabled || isScrollable) return Modifier
    return Modifier.pointerInput(dataSize, spacingPx, viewportWidthPx, chartHeightPx) {
        detectTapGestures(
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
    if (!interactionEnabled || !isScrollable) return Modifier
    return Modifier.pointerInput(dataSize, unitWidthPx) {
        detectTapGestures(
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
}

fun buildPinchModifier(
    isScrollable: Boolean,
    dataSize: Int,
    zoomMin: Float,
    zoomMax: Float,
    getZoomScale: () -> Float,
    setZoomScale: (Float) -> Unit,
): Modifier {
    if (!isScrollable) return Modifier
    return Modifier.pointerInput(dataSize, zoomMin, zoomMax) {
        awaitEachGesture {
            while (true) {
                val event = awaitPointerEvent()
                val pressedPointers = event.changes.count { it.pressed }
                if (pressedPointers == 0) break
                if (pressedPointers < 2) continue

                val zoomChange = event.calculateZoom()
                if (abs(zoomChange - 1f) > ZOOM_DELTA_EPSILON) {
                    val currentScale = getZoomScale()
                    val updatedScale = (currentScale * zoomChange).coerceIn(zoomMin, zoomMax)
                    if (abs(updatedScale - currentScale) <= ZOOM_DELTA_EPSILON) continue
                    setZoomScale(updatedScale)
                    event.changes.forEach { change ->
                        if (change.positionChanged()) {
                            change.consume()
                        }
                    }
                }
            }
        }
    }
}
