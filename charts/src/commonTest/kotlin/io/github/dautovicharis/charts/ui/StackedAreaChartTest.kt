package io.github.dautovicharis.charts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_STACKED_AREA
import io.github.dautovicharis.charts.internal.ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.mock.MockTest.multiDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import kotlin.test.Test

class StackedAreaChartTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stackedAreaChart_withValidData_displaysChart() =
        runComposeUiTest {
            // Arrange
            val expectedTitle = multiDataSet.data.title

            // Act
            setContent {
                StackedAreaChart(multiDataSet)
            }

            // Assert
            onNodeWithTag(TestTags.STACKED_AREA_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE)
                .assertTextEquals(expectedTitle)
                .isDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stackedAreaChart_withInvalidData_displaysError() =
        runComposeUiTest {
            // Arrange
            val dataSet =
                listOf("Series" to listOf(10f)).toMultiChartDataSet(
                    title = "Stacked Area",
                    categories = listOf("Q1"),
                )
            val expectedError =
                RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_STACKED_AREA)

            // Act
            setContent {
                StackedAreaChart(dataSet)
            }

            // Assert
            onNodeWithTag(TestTags.CHART_ERROR).isDisplayed()
            onNodeWithText("${expectedError}\n").isDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun stackedAreaChart_withSelectedPointIndex_displaysSelectedPointDetails() =
        runComposeUiTest {
            // Arrange
            val selectedPointIndex = 1
            val expectedTitle = multiDataSet.data.getLabel(selectedPointIndex)

            // Act
            setContent {
                StackedAreaChart(
                    dataSet = multiDataSet,
                    interactionEnabled = false,
                    animateOnStart = false,
                    selectedPointIndex = selectedPointIndex,
                )
            }

            // Assert
            onNodeWithTag(TestTags.STACKED_AREA_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE)
                .assertTextEquals(expectedTitle)
                .isDisplayed()
        }
}
