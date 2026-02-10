package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

internal const val BAR_DENSE_THRESHOLD = 50
private const val AXIS_LABEL_CHAR_WIDTH_FACTOR = 0.58f
private const val AXIS_LABEL_LINE_HEIGHT_FACTOR = 1.2f

internal data class AxisLabelFootprintPx(
    val width: Float,
    val height: Float,
)

internal fun resolveAxisLabel(
    labels: List<String>,
    index: Int,
): String {
    return labels.getOrNull(index).orEmpty().ifBlank { (index + 1).toString() }
}

internal fun estimateXAxisLabelFootprintPx(
    labels: List<String>,
    dataSize: Int,
    fontSizePx: Float,
    tiltDegrees: Float,
): AxisLabelFootprintPx {
    if (dataSize <= 0 || fontSizePx <= 0f) {
        return AxisLabelFootprintPx(width = 1f, height = fontSizePx.coerceAtLeast(1f))
    }

    var longestLabelLength = 1
    repeat(dataSize) { index ->
        val resolvedLabel = resolveAxisLabel(labels = labels, index = index)
        longestLabelLength = max(longestLabelLength, resolvedLabel.length)
    }

    val baseWidth = longestLabelLength * fontSizePx * AXIS_LABEL_CHAR_WIDTH_FACTOR
    val baseHeight = fontSizePx * AXIS_LABEL_LINE_HEIGHT_FACTOR
    val normalizedTilt = tiltDegrees.coerceIn(0f, 75f)
    if (normalizedTilt <= 0f) {
        return AxisLabelFootprintPx(
            width = baseWidth.coerceAtLeast(1f),
            height = baseHeight.coerceAtLeast(1f),
        )
    }

    val theta = normalizedTilt * PI.toFloat() / 180f
    val cosTheta = cos(theta)
    val sinTheta = sin(theta)
    val rotatedWidth = baseWidth * cosTheta + baseHeight * sinTheta
    val rotatedHeight = baseWidth * sinTheta + baseHeight * cosTheta
    return AxisLabelFootprintPx(
        width = rotatedWidth.coerceAtLeast(1f),
        height = rotatedHeight.coerceAtLeast(1f),
    )
}

internal fun estimateYAxisLabelWidthPx(
    ticks: List<YAxisTick>,
    fontSizePx: Float,
): Float {
    if (ticks.isEmpty() || fontSizePx <= 0f) return 0f

    val longestLabelLength = ticks.maxOf { it.label.length }.coerceAtLeast(1)
    return longestLabelLength * fontSizePx * AXIS_LABEL_CHAR_WIDTH_FACTOR
}

internal fun getSelectedIndex(
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

internal fun getSelectedIndexForContentX(
    contentX: Float,
    dataSize: Int,
    unitWidthPx: Float,
): Int {
    if (dataSize <= 0 || unitWidthPx <= 0f) return 0
    return (contentX / unitWidthPx).toInt().coerceIn(0, dataSize - 1)
}

internal fun shouldUseScrollableDensity(pointsCount: Int): Boolean {
    return pointsCount >= BAR_DENSE_THRESHOLD
}

internal fun unitWidth(
    barWidthPx: Float,
    spacingPx: Float,
): Float {
    return max(1f, barWidthPx + spacingPx)
}

internal fun contentWidth(
    dataSize: Int,
    unitWidthPx: Float,
    spacingPx: Float,
): Float {
    if (dataSize <= 0) return 0f
    return max(1f, dataSize * unitWidthPx - spacingPx)
}

internal fun baselineYForRange(
    minValue: Double,
    maxValue: Double,
    heightPx: Float,
): Float {
    if (heightPx <= 0f) return 0f
    val rangeValue = maxValue - minValue
    if (rangeValue == 0.0) {
        return if (maxValue < 0.0) 0f else heightPx
    }

    val normalizedZero = ((0.0 - minValue) / rangeValue).toFloat()
    val baseline = heightPx * (1f - normalizedZero)
    return baseline.coerceIn(0f, heightPx)
}

internal fun visibleIndexRange(
    dataSize: Int,
    viewportWidthPx: Float,
    scrollOffsetPx: Float,
    unitWidthPx: Float,
): IntRange {
    if (dataSize <= 0 || viewportWidthPx <= 0f || unitWidthPx <= 0f) return IntRange.EMPTY
    val firstVisible = (scrollOffsetPx / unitWidthPx).toInt().coerceIn(0, dataSize - 1)
    val lastVisible =
        ((scrollOffsetPx + viewportWidthPx) / unitWidthPx)
            .toInt()
            .coerceIn(firstVisible, dataSize - 1)
    return firstVisible..lastVisible
}

internal fun sampledLabelIndices(
    dataSize: Int,
    maxCount: Int,
    visibleRange: IntRange? = null,
): List<Int> {
    if (dataSize <= 0) return emptyList()
    val safeMaxCount = maxCount.coerceAtLeast(2)
    val fullRange = 0..(dataSize - 1)
    val range =
        if (visibleRange == null || visibleRange.isEmpty()) {
            fullRange
        } else {
            val start = visibleRange.first.coerceIn(0, dataSize - 1)
            val end = visibleRange.last.coerceIn(start, dataSize - 1)
            start..end
        }

    val size = range.last - range.first + 1
    if (size <= safeMaxCount) {
        return range.toList()
    }

    val step = (size - 1).toFloat() / (safeMaxCount - 1).toFloat()
    val sampled =
        (0 until safeMaxCount)
            .map { tick ->
                val raw = range.first + tick * step
                raw.roundToInt().coerceIn(range.first, range.last)
            }
            .distinct()
            .toMutableList()

    if (sampled.firstOrNull() != range.first) sampled.add(0, range.first)
    if (sampled.lastOrNull() != range.last) sampled.add(range.last)
    return sampled.distinct().sorted()
}

internal fun scrollableLabelIndices(
    dataSize: Int,
    maxCount: Int,
    visibleRange: IntRange,
): List<Int> {
    if (dataSize <= 0 || visibleRange.isEmpty()) return emptyList()

    val start = visibleRange.first.coerceIn(0, dataSize - 1)
    val end = visibleRange.last.coerceIn(start, dataSize - 1)
    val safeMaxCount = maxCount.coerceAtLeast(2)
    val visibleCount = end - start + 1
    val stride = ceil(visibleCount.toFloat() / safeMaxCount.toFloat()).toInt().coerceAtLeast(1)

    val firstIndex = (((start - stride).coerceAtLeast(0)) / stride) * stride
    val lastIndex = (end + stride).coerceAtMost(dataSize - 1)

    val indices = mutableListOf<Int>()
    var index = firstIndex
    while (index <= lastIndex) {
        indices.add(index)
        index += stride
    }

    if (start == 0 && indices.firstOrNull() != 0) indices.add(0, 0)
    if (end == dataSize - 1 && indices.lastOrNull() != dataSize - 1) indices.add(dataSize - 1)

    return indices.distinct()
}
