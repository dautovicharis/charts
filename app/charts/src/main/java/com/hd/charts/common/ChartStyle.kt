package com.hd.charts.common

import com.hd.charts.barchart.BarChartViewStyle
import com.hd.charts.barstackedchart.StackedBarChartViewStyle
import com.hd.charts.linechart.LineChartViewStyle
import com.hd.charts.piechart.PieChartViewStyle

object ChartStyle {
    val pieChart: PieChartViewStyle.PieChartStyleBuilder
        get() = PieChartViewStyle.PieChartStyleBuilder()

    val barChart: BarChartViewStyle.BarChartStyleBuilder
        get() = BarChartViewStyle.BarChartStyleBuilder()

    val stackedBar: StackedBarChartViewStyle.StackedBarChartStyleBuilder
        get() = StackedBarChartViewStyle.StackedBarChartStyleBuilder()

    val lineChart: LineChartViewStyle.LineChartStyleBuilder
        get() = LineChartViewStyle.LineChartStyleBuilder()
}