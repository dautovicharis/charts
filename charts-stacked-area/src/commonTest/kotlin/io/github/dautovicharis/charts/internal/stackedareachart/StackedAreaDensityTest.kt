package io.github.dautovicharis.charts.internal.stackedareachart

import io.github.dautovicharis.charts.model.toMultiChartDataSet
import kotlin.test.Test
import kotlin.test.assertEquals

class StackedAreaDensityTest {
    @Test
    fun aggregateForCompactDensity_reducesPoints_andPreservesSourceMapping() {
        val points = 10
        val data =
            listOf(
                "Series A" to List(points) { index -> (index + 1).toFloat() },
                "Series B" to List(points) { index -> (index + 10).toFloat() },
            ).toMultiChartDataSet(
                title = "Dense Stacked Area",
                categories = List(points) { index -> "P${index + 1}" },
            ).data

        val render = aggregateForCompactDensity(data = data, targetPoints = 5)

        assertEquals(
            expected = 5,
            actual =
                render.data.items
                    .first()
                    .item.points.size,
        )
        assertEquals(expected = listOf("P1", "P3", "P5", "P7", "P9"), actual = render.data.categories)
        assertEquals(expected = listOf(0, 2, 4, 6, 8), actual = render.sourceIndexByRenderIndex)
        assertEquals(expected = 0, actual = render.resolveSourceIndex(0))
        assertEquals(expected = 6, actual = render.resolveSourceIndex(3))
        assertEquals(expected = 1, actual = render.resolveRenderIndex(2))
        assertEquals(expected = 3, actual = render.resolveRenderIndex(7))
    }

    @Test
    fun identityRenderData_returnsDirectIndexMapping() {
        val points = 4
        val data =
            listOf(
                "Series A" to List(points) { index -> (index + 1).toFloat() },
                "Series B" to List(points) { index -> (index + 10).toFloat() },
            ).toMultiChartDataSet(
                title = "Stacked Area",
                categories = List(points) { index -> "P${index + 1}" },
            ).data

        val render = identityRenderData(data)

        assertEquals(expected = 4, actual = render.sourcePointsCount)
        assertEquals(expected = listOf(0, 1, 2, 3), actual = render.sourceIndexByRenderIndex)
        assertEquals(expected = 3, actual = render.resolveSourceIndex(3))
        assertEquals(expected = 2, actual = render.resolveRenderIndex(2))
    }
}
