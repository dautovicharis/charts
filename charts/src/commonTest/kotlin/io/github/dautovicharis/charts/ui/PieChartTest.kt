package io.github.dautovicharis.charts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_PIE
import io.github.dautovicharis.charts.internal.ValidationErrors.RULE_COLORS_SIZE_MISMATCH
import io.github.dautovicharis.charts.internal.ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.internal.piechart.createPieSlices
import io.github.dautovicharis.charts.internal.piechart.getCoordinatesForSlice
import io.github.dautovicharis.charts.mock.MockTest.TITLE
import io.github.dautovicharis.charts.mock.MockTest.colors
import io.github.dautovicharis.charts.mock.MockTest.dataSet
import io.github.dautovicharis.charts.mock.MockTest.mockPieChartStyle
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.style.PieChartDefaults
import kotlin.test.Test

class PieChartTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pieChart_withValidData_displaysChart() =
        runComposeUiTest {
            // Arrange
            val expectedTitle = dataSet.data.label

            // Act
            setContent {
                PieChart(dataSet, PieChartDefaults.style())
            }

            // Assert
            onNodeWithTag(TestTags.PIE_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE).assertTextEquals(expectedTitle)
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pieChart_withValidData_displayAndInteractWithChart() =
        runComposeUiTest {
            // Arrange
            val slices = createPieSlices(dataSet.data.item)
            val expectedTitle = dataSet.data.label

            // Act
            setContent {
                PieChart(dataSet, PieChartDefaults.style())
            }

            // Assert
            onNodeWithTag(TestTags.PIE_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE).assertTextEquals(expectedTitle)
            val size = onNodeWithTag(TestTags.PIE_CHART).fetchSemanticsNode().size

            dataSet.data.item.labels.forEachIndexed { index, value ->
                val sliceMiddlePosition =
                    getCoordinatesForSlice(index = index, size = size, slices = slices)
                onNodeWithTag(TestTags.PIE_CHART).performTouchInput {
                    down(sliceMiddlePosition)
                    up()
                }
                onNodeWithTag(TestTags.CHART_TITLE).assertTextEquals(value).isDisplayed()
            }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pieChart_withInvalidData_displaysError() =
        runComposeUiTest {
            // Arrange
            val dataSet =
                ChartDataSet(
                    items = ChartDataType.FloatData(listOf(1f)),
                    title = TITLE,
                )
            val expectedError = RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_PIE)

            // Act
            setContent {
                PieChart(dataSet, PieChartDefaults.style())
            }

            // Assert
            onNodeWithTag(TestTags.PIE_CHART).assertDoesNotExist()
            onNodeWithTag(TestTags.CHART_ERROR).isDisplayed()
            onNodeWithText("${expectedError}\n").isDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pieChart_withInvalidColors_displaysError() =
        runComposeUiTest {
            // Arrange
            val colors = colors.drop(2)
            val pieChartStyle = mockPieChartStyle(colors)
            val expectedColorsSize = dataSet.data.item.points.size
            val colorsSize = colors.size
            val expectedError = RULE_COLORS_SIZE_MISMATCH.format(colorsSize, expectedColorsSize)

            // Act
            setContent {
                PieChart(
                    dataSet,
                    pieChartStyle,
                )
            }

            // Assert
            onNodeWithTag(TestTags.PIE_CHART).assertDoesNotExist()
            onNodeWithTag(TestTags.CHART_ERROR).isDisplayed()
            onNodeWithText("${expectedError}\n").isDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pieChart_withDonutStyle_displaysCorrectly() =
        runComposeUiTest {
            // Arrange
            val slices = createPieSlices(dataSet.data.item)

            // Act
            setContent {
                PieChart(dataSet, PieChartDefaults.style(donutPercentage = 0.5f))
            }

            // Assert
            onNodeWithTag(TestTags.PIE_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE).assertTextEquals(dataSet.data.label)
            val size = onNodeWithTag(TestTags.PIE_CHART).fetchSemanticsNode().size

            dataSet.data.item.labels.forEachIndexed { index, value ->
                val sliceMiddlePosition =
                    getCoordinatesForSlice(index = index, size = size, slices = slices)
                onNodeWithTag(TestTags.PIE_CHART).performTouchInput {
                    down(sliceMiddlePosition)
                    up()
                }
                onNodeWithTag(TestTags.CHART_TITLE).assertTextEquals(value).isDisplayed()
            }
        }
}
