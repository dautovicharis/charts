package io.github.dautovicharis.charts.internal.barstackedchart

import io.github.dautovicharis.charts.model.toMultiChartDataSet
import kotlin.test.Test
import kotlin.test.assertEquals

class StackedBarDensityTest {
    @Test
    fun aggregateForCompactDensity_reducesBars_andPreservesSourceMapping() {
        val data =
            List(10) { index ->
                "Bar ${index + 1}" to listOf((index + 1).toFloat(), (index + 2).toFloat())
            }.toMultiChartDataSet(
                title = "Dense Stacked Bar",
                categories = listOf("S1", "S2"),
            ).data

        val render = aggregateForCompactDensity(data = data, targetBars = 5)

        assertEquals(expected = 5, actual = render.data.items.size)
        assertEquals(expected = 10, actual = render.sourceSize)
        assertEquals(expected = listOf(0, 2, 4, 6, 8), actual = render.sourceIndexByRenderIndex)
        assertEquals(expected = 0, actual = render.resolveSourceIndex(0))
        assertEquals(expected = 4, actual = render.resolveSourceIndex(2))
        assertEquals(expected = 0, actual = render.resolveRenderIndex(1))
        assertEquals(expected = 2, actual = render.resolveRenderIndex(4))
    }

    @Test
    fun identityRenderData_returnsDirectIndexMapping() {
        val data =
            List(4) { index ->
                "Bar ${index + 1}" to listOf((index + 1).toFloat(), (index + 2).toFloat())
            }.toMultiChartDataSet(
                title = "Stacked Bar",
                categories = listOf("S1", "S2"),
            ).data

        val render = identityRenderData(data)

        assertEquals(expected = 4, actual = render.data.items.size)
        assertEquals(expected = listOf(0, 1, 2, 3), actual = render.sourceIndexByRenderIndex)
        assertEquals(expected = 3, actual = render.resolveSourceIndex(3))
        assertEquals(expected = 2, actual = render.resolveRenderIndex(2))
    }
}
