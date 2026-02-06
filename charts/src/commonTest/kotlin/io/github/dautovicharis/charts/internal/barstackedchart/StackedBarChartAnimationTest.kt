package io.github.dautovicharis.charts.internal.barstackedchart

import kotlin.test.Test
import kotlin.test.assertEquals

class StackedBarChartAnimationTest {
    @Test
    fun stackedSegmentHeight_usesBarTotalShare_withProgress() {
        // Arrange
        val totalMax = 10f
        val barTotal = 5f
        val value = 2f
        val chartHeight = 100f
        val progress = barTotal / totalMax
        val segmentShare = value / barTotal

        // Act
        val height =
            stackedSegmentHeight(
                segmentShare = segmentShare,
                chartHeight = chartHeight,
                barScale = 1f,
                progress = progress,
            )

        // Assert
        val expected = chartHeight * 1f * progress * segmentShare
        assertEquals(expected, height, 0.0001f)
    }

    @Test
    fun stackedSegmentHeight_whenProgressIsZero_returnsZeroHeight() {
        // Arrange
        val segmentShare = 0.6f
        val chartHeight = 100f
        val progress = 0f

        // Act
        val height =
            stackedSegmentHeight(
                segmentShare = segmentShare,
                chartHeight = chartHeight,
                barScale = 1f,
                progress = progress,
            )

        // Assert
        assertEquals(0f, height, 0.0001f)
    }

    @Test
    fun stackedSegmentHeight_appliesBarScale() {
        // Arrange
        val segmentShare = 0.25f
        val chartHeight = 80f
        val barScale = 2f
        val progress = 0.5f

        // Act
        val height =
            stackedSegmentHeight(
                segmentShare = segmentShare,
                chartHeight = chartHeight,
                barScale = barScale,
                progress = progress,
            )

        // Assert
        val expected = chartHeight * barScale * progress * segmentShare
        assertEquals(expected, height, 0.0001f)
    }

    @Test
    fun stackedSegmentHeight_withZeroBarTotal_usesZeroSegmentShare() {
        // Arrange
        val barTotal = 0f
        val value = 2f
        val segmentShare = if (barTotal == 0f) 0f else value / barTotal
        val chartHeight = 100f
        val barScale = 1f
        val progress = 0.7f

        // Act
        val height =
            stackedSegmentHeight(
                segmentShare = segmentShare,
                chartHeight = chartHeight,
                barScale = barScale,
                progress = progress,
            )

        // Assert
        val expected = chartHeight * barScale * progress * segmentShare
        assertEquals(0f, height, 0.0001f)
        assertEquals(expected, height, 0.0001f)
    }
}
