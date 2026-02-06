package io.github.dautovicharis.charts.unit.validation

import io.github.dautovicharis.charts.internal.ValidationErrors
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_PIE
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.internal.validatePieData
import io.github.dautovicharis.charts.mock.MockTest.TITLE
import io.github.dautovicharis.charts.mock.MockTest.colors
import io.github.dautovicharis.charts.mock.MockTest.dataSet
import io.github.dautovicharis.charts.mock.MockTest.mockPieChartStyle
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataValidationPieTest {
    @Test
    fun validatePieData_validDataSet_noValidationErrors() {
        // Arrange
        val pieChartStyle = mockPieChartStyle()

        // Act
        val validationErrors = validatePieData(dataSet = dataSet, style = pieChartStyle)

        // Assert
        assertTrue(validationErrors.isEmpty())
    }

    @Test
    fun validatePieData_invalidDataItems_validationErrorsPresent() {
        // Arrange
        val chartDataSet =
            ChartDataSet(
                items = ChartDataType.FloatData(listOf(10f)),
                title = TITLE,
            )

        val pieChartStyle = mockPieChartStyle()

        // Act
        val validationErrors =
            validatePieData(
                dataSet = chartDataSet,
                style = pieChartStyle,
            )

        // Assert
        val expectedError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_PIE)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }

    @Test
    fun validatePieData_invalidColors_validationErrorsPresent() {
        // Arrange
        val colors = colors.drop(2)

        val pieChartStyle = mockPieChartStyle(colors)
        val expectedColorSize = dataSet.data.item.points.size

        // Act
        val validationErrors =
            validatePieData(dataSet = dataSet, style = pieChartStyle)

        // Assert
        val expectedError =
            ValidationErrors.RULE_COLORS_SIZE_MISMATCH.format(colors.size, expectedColorSize)
        assertTrue(validationErrors.isNotEmpty())
        assertEquals(validationErrors.first(), expectedError)
    }
}
