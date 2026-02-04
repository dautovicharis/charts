package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.max

internal fun getSelectedIndex(
    position: Offset,
    dataSize: Int,
    canvasSize: IntSize,
    spacingPx: Float
): Int {
    if (dataSize <= 0 || canvasSize.width <= 0) return 0

    val totalSpacing = spacingPx * (dataSize - 1)
    val availableWidth = max(1f, canvasSize.width - totalSpacing)
    val barWidth = availableWidth / dataSize
    val unitWidth = barWidth + spacingPx
    val index = (position.x / unitWidth).toInt()
    return index.coerceIn(0, dataSize - 1)
}
