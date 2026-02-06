package io.github.dautovicharis.charts.unit.helpers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.github.dautovicharis.charts.internal.barchart.getSelectedIndex
import kotlin.test.Test
import kotlin.test.assertTrue

class BarChartHelpersTest {
    private data class GetSelectedIndexTestCase(
        val position: Offset,
        val values: List<Double>,
        val size: IntSize,
        val spacingPx: Float,
        val expectedIndex: Int,
    )

    @Test
    fun getSelectedIndex_shouldReturnCorrectIndex() {
        // Arrange
        val testCases =
            listOf(
                GetSelectedIndexTestCase(
                    position = Offset(500.0F, 500.0F),
                    values = listOf(1.0, 2.0, 3.0, 4.0),
                    size = IntSize(1000, 1000),
                    spacingPx = 0f,
                    expectedIndex = 2,
                ),
                GetSelectedIndexTestCase(
                    position = Offset(900.0F, 900.0F),
                    values = listOf(100.0, 200.0, 300.0, 400.0, 500.0),
                    size = IntSize(1500, 1500),
                    spacingPx = 0f,
                    expectedIndex = 3,
                ),
                GetSelectedIndexTestCase(
                    position = Offset(400.0F, 600.0F),
                    values = listOf(50.0, 100.0, 150.0, 200.0, 250.0),
                    size = IntSize(800, 1200),
                    spacingPx = 0f,
                    expectedIndex = 2,
                ),
                GetSelectedIndexTestCase(
                    position = Offset(520.0F, 500.0F),
                    values = listOf(1.0, 2.0, 3.0, 4.0),
                    size = IntSize(1000, 1000),
                    spacingPx = 20f,
                    expectedIndex = 2,
                ),
            )

        testCases.forEach { testCase ->
            // Act
            val result =
                getSelectedIndex(
                    testCase.position,
                    testCase.values.size,
                    testCase.size,
                    testCase.spacingPx,
                )

            // Assert
            assertTrue { result == testCase.expectedIndex }
        }
    }
}
