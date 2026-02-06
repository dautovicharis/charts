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
private fun LineChartViewPreview() {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
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
        dataSet = Mock.lineChartSimple(),
        style = style,
    )
}

@Preview
@Composable
private fun LineChartDefault() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = false) {
        LineChartViewPreview()
    }
}

@Preview
@Composable
private fun LineChartDark() {
    ChartsDefaultTheme(darkTheme = true, dynamicColor = false) {
        LineChartViewPreview()
    }
}

@Preview
@Composable
private fun LineChartDynamic() {
    ChartsDefaultTheme(dynamicColor = true) {
        LineChartViewPreview()
    }
}

@Preview
@Composable
private fun LineChartError() {
    ChartsDefaultTheme {
        LineChart(
            dataSet = Mock.lineChartSimple(1),
            style = LineChartDefaults.style(),
        )
    }
}
