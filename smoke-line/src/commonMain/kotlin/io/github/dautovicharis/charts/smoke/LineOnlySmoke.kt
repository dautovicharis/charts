package io.github.dautovicharis.charts.smoke

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet

val lineOnlySmokeDataSet =
    listOf(10f, 20f, 15f).toChartDataSet(
        labels = listOf("A", "B", "C"),
        title = "Smoke",
    )

@Composable
fun LineOnlySmokeChart() {
    LineChart(
        dataSet = lineOnlySmokeDataSet,
        interactionEnabled = false,
        animateOnStart = false,
    )
}
