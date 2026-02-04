package io.github.dautovicharis.charts.app.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.demo.bar.BarChartStyleItems
import io.github.dautovicharis.charts.app.demo.line.LineChartStyleItems
import io.github.dautovicharis.charts.app.demo.multiline.MultiLineStyleItems
import io.github.dautovicharis.charts.app.demo.pie.PieChartStyleItems
import io.github.dautovicharis.charts.app.demo.radar.RadarChartStyleItems
import io.github.dautovicharis.charts.app.demo.stackedbar.StackedBarChartStyleItems

sealed class ChartStyleType {
    data object PieChartDefault : ChartStyleType()
    data object PieChartCustom : ChartStyleType()

    data object LineChartDefault : ChartStyleType()
    data object LineChartCustom : ChartStyleType()

    data object MultiLineChartDefault : ChartStyleType()
    data object MultiLineChartCustom : ChartStyleType()

    data object BarChartDefault : ChartStyleType()
    data object BarChartCustom : ChartStyleType()

    data object StackedBarChartDefault : ChartStyleType()
    data object StackedBarChartCustom : ChartStyleType()

    data object RadarChartDefault : ChartStyleType()
    data object RadarChartCustom : ChartStyleType()
}

@Composable
fun ChartDemo(type: ChartStyleType, onRefresh: () -> Unit,
              colors: List<Color> = emptyList(),
              chartItem: @Composable () -> Unit) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        StyleAndChartComponent(
            tableItems = DemoStyleItems(type, colors),
            onRefresh = onRefresh,
            chartItem = chartItem
        )
    }
}

@Composable
private fun DemoStyleItems(type: ChartStyleType, colors: List<Color> = emptyList()): StyleItems {
    return when (type) {
        is ChartStyleType.PieChartDefault -> PieChartStyleItems.default()
        is ChartStyleType.PieChartCustom -> PieChartStyleItems.custom(colors)
        is ChartStyleType.LineChartDefault -> LineChartStyleItems.default()
        is ChartStyleType.LineChartCustom -> LineChartStyleItems.custom()
        is ChartStyleType.MultiLineChartDefault -> MultiLineStyleItems.default()
        is ChartStyleType.MultiLineChartCustom -> MultiLineStyleItems.custom(colors)
        is ChartStyleType.BarChartDefault -> BarChartStyleItems.default()
        is ChartStyleType.BarChartCustom -> BarChartStyleItems.custom()
        is ChartStyleType.StackedBarChartDefault -> StackedBarChartStyleItems.default()
        is ChartStyleType.StackedBarChartCustom -> StackedBarChartStyleItems.custom(colors)
        is ChartStyleType.RadarChartDefault -> RadarChartStyleItems.default()
        is ChartStyleType.RadarChartCustom -> RadarChartStyleItems.custom(colors)
    }
}
