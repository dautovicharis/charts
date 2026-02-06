package io.github.dautovicharis.charts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_LINE
import io.github.dautovicharis.charts.internal.ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.mock.MockTest.TITLE
import io.github.dautovicharis.charts.mock.MockTest.dataSet
import io.github.dautovicharis.charts.model.ChartDataSet
import kotlin.test.Test

class LineChartTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lineChart_withValidData_displaysChart() =
        runComposeUiTest {
            // Arrange
            val expectedTitle = dataSet.data.label

            // Act
            setContent {
                LineChart(dataSet)
            }

            // Assert
            onNodeWithTag(TestTags.LINE_CHART).isDisplayed()
            onNodeWithTag(TestTags.CHART_TITLE)
                .assertTextEquals(expectedTitle)
                .isDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lineChart_withInvalidData_displaysError() =
        runComposeUiTest {
            val dataSet =
                ChartDataSet(
                    items = ChartDataType.FloatData(listOf(1f)),
                    title = TITLE,
                )
            val expectedError = RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_LINE)

            setContent {
                LineChart(dataSet)
            }

            onNodeWithTag(TestTags.CHART_ERROR).isDisplayed()
            onNodeWithText("${expectedError}\n").isDisplayed()
        }
}
