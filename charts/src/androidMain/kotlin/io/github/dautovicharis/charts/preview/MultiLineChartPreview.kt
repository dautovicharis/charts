package io.github.dautovicharis.charts.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.preview.mock.Mock
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.LineChartDefaults

@Composable
private fun MultiLineChartPreview() {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error,
        )

    val style =
        LineChartDefaults.style(
            bezier = true,
            lineColors = colors,
            dragPointSize = 5f,
            pointVisible = true,
            chartViewStyle = ChartViewDefaults.style(width = 300.dp),
        )

    LineChart(
        dataSet = Mock.lineChart(),
        style = style,
    )
}

@Preview
@Composable
private fun MultiLineChartDefault() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = false) {
        MultiLineChartPreview()
    }
}

@Preview
@Composable
private fun MultiLineChartDark() {
    ChartsDefaultTheme(darkTheme = true, dynamicColor = false) {
        MultiLineChartPreview()
    }
}

@Preview(apiLevel = 33)
@Composable
private fun MultiLineChartDynamic() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = true) {
        MultiLineChartPreview()
    }
}

@Preview
@Composable
private fun MultiLineChartError() {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        )

    val style =
        LineChartDefaults.style(
            bezier = true,
            lineColors = colors,
            dragPointSize = 5f,
            pointVisible = true,
            chartViewStyle = ChartViewDefaults.style(width = 300.dp),
        )

    ChartsDefaultTheme {
        LineChart(
            dataSet = Mock.lineChartInvalid(),
            style = style,
        )
    }
}
