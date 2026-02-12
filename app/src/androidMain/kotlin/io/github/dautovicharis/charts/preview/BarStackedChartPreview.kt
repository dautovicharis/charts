package io.github.dautovicharis.charts.preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.preview.mock.Mock
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

@Composable
private fun StackedBarChartPreview() {
    StackedBarChart(
        dataSet = Mock.stackedBarChart(),
        style = StackedBarChartDefaults.style(),
    )
}

@Preview
@Composable
private fun StackedBarChartDefault() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = false) {
        StackedBarChartPreview()
    }
}

@Preview
@Composable
private fun StackedBarChartDark() {
    ChartsDefaultTheme(darkTheme = true, dynamicColor = false) {
        StackedBarChartPreview()
    }
}

@Preview(apiLevel = 33)
@Composable
private fun StackedBarChartDynamic() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = true) {
        StackedBarChartPreview()
    }
}

@Preview
@Composable
private fun StackedBarChartError() {
    val barColor = MaterialTheme.colorScheme.primary
    val style =
        StackedBarChartDefaults.style(
            barColors = listOf(barColor),
            space = 8.dp,
        )

    ChartsDefaultTheme {
        StackedBarChart(
            dataSet = Mock.stackedBarChartInvalid(),
            style = style,
        )
    }
}
