package io.github.dautovicharis.charts.internal.barchart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.github.dautovicharis.charts.internal.common.axis.AxisLabelFootprintPx
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.internal.common.model.toChartData
import kotlin.math.max
import io.github.dautovicharis.charts.internal.common.axis.baselineYForRange as baselineYForRangeCore
import io.github.dautovicharis.charts.internal.common.axis.centeredLabelIndexRange as centeredLabelIndexRangeCore
import io.github.dautovicharis.charts.internal.common.axis.estimateXAxisLabelFootprintPx as estimateXAxisLabelFootprintPxCore
import io.github.dautovicharis.charts.internal.common.axis.estimateYAxisLabelWidthPx as estimateYAxisLabelWidthPxCore
import io.github.dautovicharis.charts.internal.common.axis.resolveAxisLabel as resolveAxisLabelCore
import io.github.dautovicharis.charts.internal.common.axis.sampledLabelIndices as sampledLabelIndicesCore
import io.github.dautovicharis.charts.internal.common.axis.scrollableLabelIndices as scrollableLabelIndicesCore
import io.github.dautovicharis.charts.internal.common.axis.visibleIndexRange as visibleIndexRangeCore
import io.github.dautovicharis.charts.internal.common.density.aggregateLabelsByCenterValue as aggregateLabelsByCenterValueCore
import io.github.dautovicharis.charts.internal.common.density.aggregatePointsByAverage as aggregatePointsByAverageCore
import io.github.dautovicharis.charts.internal.common.density.bucketSizeForTarget as bucketSizeForTargetCore
import io.github.dautovicharis.charts.internal.common.density.buildBucketRanges as buildBucketRangesCore
import io.github.dautovicharis.charts.internal.common.density.shouldUseScrollableDensity as shouldUseScrollableDensityCore
import io.github.dautovicharis.charts.internal.common.interaction.selectedIndexForBarFit as selectedIndexForBarFitCore
import io.github.dautovicharis.charts.internal.common.interaction.selectedIndexForContentX as selectedIndexForContentXCore

const val BAR_DENSE_THRESHOLD = 50

fun resolveAxisLabel(
    labels: List<String>,
    index: Int,
): String {
    return resolveAxisLabelCore(labels = labels, index = index)
}

fun estimateXAxisLabelFootprintPx(
    labels: List<String>,
    dataSize: Int,
    fontSizePx: Float,
    tiltDegrees: Float,
): AxisLabelFootprintPx {
    return estimateXAxisLabelFootprintPxCore(
        labels = labels,
        dataSize = dataSize,
        fontSizePx = fontSizePx,
        tiltDegrees = tiltDegrees,
    )
}

fun estimateYAxisLabelWidthPx(
    ticks: List<YAxisTick>,
    fontSizePx: Float,
): Float {
    return estimateYAxisLabelWidthPxCore(
        labels = ticks.map { it.label },
        fontSizePx = fontSizePx,
    )
}

fun getSelectedIndex(
    position: Offset,
    dataSize: Int,
    canvasSize: IntSize,
    spacingPx: Float,
): Int {
    return selectedIndexForBarFitCore(
        positionX = position.x,
        dataSize = dataSize,
        canvasWidthPx = canvasSize.width.toFloat(),
        spacingPx = spacingPx,
        invalidIndex = 0,
    )
}

fun getSelectedIndexForContentX(
    contentX: Float,
    dataSize: Int,
    unitWidthPx: Float,
): Int {
    return selectedIndexForContentXCore(
        contentX = contentX,
        dataSize = dataSize,
        unitWidthPx = unitWidthPx,
        invalidIndex = 0,
    )
}

fun shouldUseScrollableDensity(pointsCount: Int): Boolean {
    return shouldUseScrollableDensityCore(
        pointsCount = pointsCount,
        threshold = BAR_DENSE_THRESHOLD,
    )
}

fun aggregateForCompactDensity(
    data: ChartData,
    targetPoints: Int = BAR_DENSE_THRESHOLD,
): ChartData {
    if (targetPoints <= 1) return data
    val sourcePointsCount = data.points.size
    if (sourcePointsCount <= targetPoints) return data

    val bucketSize = bucketSizeForTargetCore(totalPoints = sourcePointsCount, targetPoints = targetPoints)
    val bucketRanges = buildBucketRangesCore(totalPoints = sourcePointsCount, bucketSize = bucketSize)
    val aggregatedPoints = aggregatePointsByAverageCore(data.points, bucketRanges)
    val aggregatedLabels = aggregateLabelsByCenterValueCore(data.labels, bucketRanges)
    return aggregatedPoints.toChartData(labels = aggregatedLabels)
}

fun maxBarsThatFit(
    viewportWidthPx: Float,
    spacingPx: Float,
    minBarWidthPx: Float,
): Int {
    val safeViewportWidthPx = max(1f, viewportWidthPx)
    val safeSpacingPx = spacingPx.coerceAtLeast(0f)
    val safeMinBarWidthPx = minBarWidthPx.coerceAtLeast(1f)
    val unitWidthPx = safeMinBarWidthPx + safeSpacingPx
    return ((safeViewportWidthPx + safeSpacingPx) / unitWidthPx).toInt().coerceAtLeast(1)
}

fun unitWidth(
    barWidthPx: Float,
    spacingPx: Float,
): Float {
    return max(1f, barWidthPx + spacingPx)
}

fun contentWidth(
    dataSize: Int,
    unitWidthPx: Float,
    spacingPx: Float,
): Float {
    if (dataSize <= 0) return 0f
    return max(1f, dataSize * unitWidthPx - spacingPx)
}

fun baselineYForRange(
    minValue: Double,
    maxValue: Double,
    heightPx: Float,
): Float {
    return baselineYForRangeCore(
        minValue = minValue,
        maxValue = maxValue,
        heightPx = heightPx,
    )
}

fun visibleIndexRange(
    dataSize: Int,
    viewportWidthPx: Float,
    scrollOffsetPx: Float,
    unitWidthPx: Float,
): IntRange {
    return visibleIndexRangeCore(
        dataSize = dataSize,
        viewportWidthPx = viewportWidthPx,
        scrollOffsetPx = scrollOffsetPx,
        unitWidthPx = unitWidthPx,
    )
}

fun sampledLabelIndices(
    dataSize: Int,
    maxCount: Int,
    visibleRange: IntRange? = null,
): List<Int> {
    return sampledLabelIndicesCore(
        dataSize = dataSize,
        maxCount = maxCount,
        visibleRange = visibleRange,
    )
}

fun scrollableLabelIndices(
    dataSize: Int,
    maxCount: Int,
    visibleRange: IntRange,
): List<Int> {
    return scrollableLabelIndicesCore(
        dataSize = dataSize,
        maxCount = maxCount,
        visibleRange = visibleRange,
    )
}

fun centeredLabelIndexRange(
    dataSize: Int,
    unitWidthPx: Float,
    viewportWidthPx: Float,
    scrollOffsetPx: Float,
    firstCenterPx: Float = 0f,
    labelWidthPx: Float = 0f,
    edgePaddingPx: Float = 0f,
): IntRange {
    return centeredLabelIndexRangeCore(
        dataSize = dataSize,
        unitWidthPx = unitWidthPx,
        viewportWidthPx = viewportWidthPx,
        scrollOffsetPx = scrollOffsetPx,
        firstCenterPx = firstCenterPx,
        labelWidthPx = labelWidthPx,
        edgePaddingPx = edgePaddingPx,
    )
}
