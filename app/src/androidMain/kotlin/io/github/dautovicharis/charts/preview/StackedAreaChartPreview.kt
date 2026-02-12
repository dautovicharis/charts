package io.github.dautovicharis.charts.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.preview.mock.Mock
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults

@Composable
private fun StackedAreaChartViewPreview() {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        )
    val style =
        StackedAreaChartDefaults.style(
            areaColors = colors,
            lineColors = colors,
            fillAlpha = 0.32f,
            bezier = false,
            chartViewStyle = ChartViewDefaults.style(width = 300.dp),
        )

    StackedAreaChart(
        dataSet = Mock.stackedAreaChart(),
        style = style,
    )
}

@Preview
@Composable
private fun StackedAreaChartDefault() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = false) {
        StackedAreaChartViewPreview()
    }
}

@Preview
@Composable
private fun StackedAreaChartDark() {
    ChartsDefaultTheme(darkTheme = true, dynamicColor = false) {
        StackedAreaChartViewPreview()
    }
}

@Preview(apiLevel = 33)
@Composable
private fun StackedAreaChartDynamic() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = true) {
        StackedAreaChartViewPreview()
    }
}

@Preview
@Composable
private fun StackedAreaChartError() {
    ChartsDefaultTheme {
        StackedAreaChart(
            dataSet = Mock.stackedAreaChartInvalid(),
            style = StackedAreaChartDefaults.style(),
        )
    }
}
