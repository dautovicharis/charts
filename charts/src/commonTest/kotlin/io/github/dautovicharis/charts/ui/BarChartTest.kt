package io.github.dautovicharis.charts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.TestTags
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_BAR
import io.github.dautovicharis.charts.internal.ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN
import io.github.dautovicharis.charts.internal.format
import io.github.dautovicharis.charts.mock.MockTest.TITLE
import io.github.dautovicharis.charts.mock.MockTest.dataSet
import kotlin.test.Test

class BarChartTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun barChart_withValidData_displaysChart() = runComposeUiTest {
        // Arrange
        val expectedTitle = dataSet.data.label

        // Act
        setContent {
            BarChart(dataSet)
        }

        // Assert
        onNodeWithTag(TestTags.BAR_CHART).isDisplayed()
        onNodeWithTag(TestTags.CHART_TITLE)
            .assertTextEquals(expectedTitle).isDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun barChart_withInvalidData_displaysError () = runComposeUiTest {
        val dataSet = ChartDataSet(
            items = ChartDataType.FloatData(listOf(1f)),
            title = TITLE
        )
        val expectedError = RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_BAR)

        setContent {
            BarChart(dataSet)
        }

        onNodeWithTag(TestTags.CHART_ERROR).isDisplayed()
        onNodeWithText("${expectedError}\n").isDisplayed()
    }
}
