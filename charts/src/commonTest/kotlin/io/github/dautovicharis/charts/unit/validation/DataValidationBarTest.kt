package io.github.dautovicharis.charts.unit.validation

import io.github.dautovicharis.charts.internal.ValidationErrors
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_STACKED_BAR
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.internal.validateBarData
import io.github.dautovicharis.charts.mock.MockTest.colors
import io.github.dautovicharis.charts.mock.MockTest.invalidDataSetCategories
import io.github.dautovicharis.charts.mock.MockTest.invalidMultiDataSet
import io.github.dautovicharis.charts.mock.MockTest.mockBarChartStyle
import io.github.dautovicharis.charts.mock.MockTest.mockStackedBarChartStyle
import io.github.dautovicharis.charts.mock.MockTest.multiDataSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataValidationBarTest {
    @Test
    fun validateBarData_invalidCategories_validationErrorsPresent() {
        // Arrange
        val dataSet = invalidDataSetCategories()

        val barChartStyle = mockBarChartStyle(colors)
        val expectedCategoriesSize =
            dataSet.data.items
                .first()
                .item.points.size

        // Act
        val validationErrors = validateBarData(dataSet.data, barChartStyle)

        // Assert
        val expectedError =
            ValidationErrors.RULE_CATEGORIES_SIZE_MISMATCH.format(
                dataSet.data.categories.size,
                expectedCategoriesSize,
            )
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }

    @Test
    fun validateBarData_invalidColors_validationErrorsPresent() {
        // Arrange
        val colors = colors.drop(2)
        val dataSet = multiDataSet

        val barChartStyle = mockBarChartStyle(colors)
        val expectedColorSize = dataSet.data.items.size

        // Act
        val validationErrors = validateBarData(dataSet.data, barChartStyle)

        // Assert
        val expectedError =
            ValidationErrors.RULE_COLORS_SIZE_MISMATCH.format(colors.size, expectedColorSize)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }

    @Test
    fun validateBarData_invalidDataItems_validationErrorsPresent() {
        // Arrange
        val index = 1
        val dataSet = invalidMultiDataSet(index)

        val barChartStyle = mockBarChartStyle()
        val expectedPoints =
            dataSet.data.items
                .first()
                .item.points.size
        val pointsSize =
            dataSet.data.items[index]
                .item.points.size

        // Act
        val validationErrors = validateBarData(dataSet.data, barChartStyle)

        // Assert
        val expectedError =
            ValidationErrors.RULE_ITEM_POINTS_SIZE.format(index, pointsSize, expectedPoints)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }

    @Test
    fun validateBarData_emptyItemPoints_validationErrorsPresent() {
        // Arrange
        val index = 0
        val dataSet = invalidMultiDataSet(index, true)
        val barChartStyle = mockStackedBarChartStyle()

        // Act
        val validationErrors = validateBarData(dataSet.data, barChartStyle)

        // Assert
        val expectedError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_STACKED_BAR)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }
}
