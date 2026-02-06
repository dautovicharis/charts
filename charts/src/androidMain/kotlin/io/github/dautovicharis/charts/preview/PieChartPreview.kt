package io.github.dautovicharis.charts.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.internal.common.theme.ChartsDefaultTheme
import io.github.dautovicharis.charts.preview.mock.Mock
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.PieChartStyle

@Composable
private fun PieChartPreview() {
    val pieColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.surface

    val style: PieChartStyle =
        PieChartDefaults.style(
            pieColor = pieColor,
            borderColor = borderColor,
            donutPercentage = 0f,
            chartViewStyle =
                ChartViewDefaults.style(
                    backgroundColor = backgroundColor,
                    cornerRadius = 20.dp,
                    shadow = 15.dp,
                    innerPadding = 15.dp,
                ),
        )

    PieChart(
        dataSet = Mock.pieChart(),
        style = style,
    )
}

@Preview
@Composable
private fun PieChartDefault() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = false) {
        PieChartPreview()
    }
}

@Preview
@Composable
private fun PieChartDark() {
    ChartsDefaultTheme(darkTheme = true, dynamicColor = false) {
        PieChartPreview()
    }
}

@Preview(apiLevel = 33)
@Composable
private fun PieChartDynamic() {
    ChartsDefaultTheme(darkTheme = false, dynamicColor = true) {
        PieChartPreview()
    }
}

@Preview
@Composable
private fun PieChartError() {
    ChartsDefaultTheme {
        PieChart(
            dataSet = Mock.pieChart(1),
            style = PieChartDefaults.style(),
        )
    }
}
