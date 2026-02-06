package io.github.dautovicharis.charts.unit.helpers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.dautovicharis.charts.internal.linechart.findNearestPoint
import io.github.dautovicharis.charts.internal.linechart.scaleValues
import kotlin.test.Test
import kotlin.test.assertTrue

class LineChartHelpersTest {
    @Test
    fun findNearestPoint_validInput_correctOffsetReturned() {
        // Arrange
        val testCases =
            hashMapOf(
                Triple(500f, listOf(100f, 200f, 300f, 400f, 500f), Size(1000f, 2500f))
                    to Offset(500f, 2200f),
                Triple(900f, listOf(180f, 360f, 540f, 720f, 900f), Size(1000f, 2500f))
                    to Offset(900f, 1672f),
                Triple(400f, listOf(-40f, 20f, 50f, -100f, 300f), Size(1000f, 2500f))
                    to Offset(400f, 2462f),
            )

        testCases.forEach { entry: Map.Entry<Triple<Float, List<Float>, Size>, Offset> ->
            // Act
            val nearestPoint =
                findNearestPoint(
                    touchX = entry.key.first,
                    scaledValues = entry.key.second,
                    size = entry.key.third,
                    bezier = false,
                )

            // Assert
            assertTrue { nearestPoint == entry.value }
        }
    }

    @Test
    fun scaleValues_validInput_correctlyScaledValuesReturned() {
        // Arrange
        val testCases =
            hashMapOf(
                Pair(listOf(10.0, 20.0, 30.0, 40.0, 50.0), Size(40f, 40f))
                    to listOf(0.0f, 10.0f, 20.0f, 30.0f, 40.0f),
                Pair(listOf(-5.0, 0.0, 5.0, 10.0, 15.0), Size(30f, 30f))
                    to listOf(0.0f, 7.5f, 15.0f, 22.5f, 30.0f),
                Pair(listOf(100.0, 200.0, 300.0, 400.0, 500.0), Size(100f, 100f))
                    to listOf(0.0f, 25.0f, 50.0f, 75.0f, 100.0f),
            )

        testCases.forEach { entry: Map.Entry<Pair<List<Double>, Size>, List<Float>> ->
            // Act
            val scaledValues = scaleValues(entry.key.first, entry.key.second)

            // Assert
            assertTrue { scaledValues == entry.value }
        }
    }
}
