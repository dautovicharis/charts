package io.github.dautovicharis.charts.unit.validation

import io.github.dautovicharis.charts.internal.ValidationErrors
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_STACKED_AREA
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.internal.validateStackedAreaData
import io.github.dautovicharis.charts.mock.MockTest.colors
import io.github.dautovicharis.charts.mock.MockTest.invalidDataSetCategories
import io.github.dautovicharis.charts.mock.MockTest.invalidMultiDataSet
import io.github.dautovicharis.charts.mock.MockTest.mockStackedAreaChartStyle
import io.github.dautovicharis.charts.mock.MockTest.multiDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataValidationStackedAreaTest {
    @Test
    fun validateStackedAreaData_validDataSet_noValidationErrors() {
        // Arrange
        val dataSet = multiDataSet
        val style = mockStackedAreaChartStyle()

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        assertTrue(validationErrors.isEmpty())
    }

    @Test
    fun validateStackedAreaData_negativeValue_validationErrorPresent() {
        // Arrange
        val dataSet =
            listOf(
                "Series A" to listOf(10f, -2f, 6f),
                "Series B" to listOf(4f, 3f, 5f),
            ).toMultiChartDataSet(
                title = "Stacked Area",
                categories = listOf("Q1", "Q2", "Q3"),
            )
        val style = mockStackedAreaChartStyle(areaColors = emptyList(), lineColors = emptyList())

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        val expectedError =
            ValidationErrors.RULE_ITEM_POINT_NEGATIVE.format(0, 1)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(expectedError, validationErrors.first())
    }

    @Test
    fun validateStackedAreaData_invalidAreaColors_validationErrorsPresent() {
        // Arrange
        val dataSet = multiDataSet
        val style = mockStackedAreaChartStyle(areaColors = colors.drop(2))
        val expectedColorSize = dataSet.data.items.size

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        val expectedError =
            ValidationErrors.RULE_COLORS_SIZE_MISMATCH.format(
                colors.drop(2).size,
                expectedColorSize,
            )
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(expectedError, validationErrors.first())
    }

    @Test
    fun validateStackedAreaData_invalidCategories_validationErrorsPresent() {
        // Arrange
        val dataSet = invalidDataSetCategories()
        val style = mockStackedAreaChartStyle()
        val expectedCategoriesSize = dataSet.data.items.first().item.points.size

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        val expectedError =
            ValidationErrors.RULE_CATEGORIES_SIZE_MISMATCH.format(
                dataSet.data.categories.size,
                expectedCategoriesSize,
            )
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(expectedError, validationErrors.first())
    }

    @Test
    fun validateStackedAreaData_invalidDataItems_validationErrorsPresent() {
        // Arrange
        val index = 1
        val dataSet = invalidMultiDataSet(index)
        val style = mockStackedAreaChartStyle()
        val expectedPoints = dataSet.data.items.first().item.points.size
        val pointsSize = dataSet.data.items[index].item.points.size

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        val expectedError =
            ValidationErrors.RULE_ITEM_POINTS_SIZE.format(index, pointsSize, expectedPoints)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(expectedError, validationErrors.first())
    }

    @Test
    fun validateStackedAreaData_withTooFewPoints_validationErrorsPresent() {
        // Arrange
        val index = 0
        val dataSet = invalidMultiDataSet(index, empty = true)
        val style = mockStackedAreaChartStyle()

        // Act
        val validationErrors = validateStackedAreaData(dataSet.data, style)

        // Assert
        val expectedError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_STACKED_AREA)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(expectedError, validationErrors.first())
    }
}
