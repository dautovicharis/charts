package io.github.dautovicharis.charts.app.demo.radar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.app.demo.ChartViewDemoStyle
import io.github.dautovicharis.charts.app.ui.composable.ChartDemo
import io.github.dautovicharis.charts.app.ui.composable.ChartStyleType
import io.github.dautovicharis.charts.app.ui.theme.ColorPalette
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.RadarChartStyle
import org.koin.compose.viewmodel.koinViewModel

object RadarDemoStyle {

    @Composable
    fun default(): RadarChartStyle {
        return RadarChartDefaults.style(
            chartViewStyle = ChartViewDemoStyle.custom()
        )
    }

    @Composable
    fun custom(lineColors: List<Color>): RadarChartStyle {
        return RadarChartDefaults.style(
            lineColors = lineColors,
            lineWidth = 3.5f,
            pointColor = ColorPalette.DataColor.magenta,
            pointSize = 7f,
            gridSteps = 6,
            gridLineWidth = 1.4f,
            axisLineWidth = 1.2f,
            fillAlpha = 0.2f,
            categoryLegendVisible = false,
            chartViewStyle = ChartViewDemoStyle.custom()
        )
    }
}

@Composable
fun RadarChartBasicDemo(viewModel: RadarChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()

    ChartDemo(
        type = ChartStyleType.RadarChartDefault,
        onRefresh = viewModel::regenerateBasicDataSet
    ) {
        RadarChart(
            dataSet = dataSet.basicDataSet,
            style = RadarDemoStyle.default()
        )
    }
}

@Composable
fun RadarChartCustomDemo(viewModel: RadarChartViewModel = koinViewModel()) {
    val dataSet by viewModel.dataSet.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.regenerateCustomDataSet()
    }

    ChartDemo(
        type = ChartStyleType.RadarChartCustom,
        colors = dataSet.lineColors,
        onRefresh = viewModel::regenerateCustomDataSet
    ) {
        RadarChart(
            dataSet = dataSet.customDataSet,
            style = RadarDemoStyle.custom(dataSet.lineColors)
        )
    }
}
