package ui

import chartsproject.charts_demo_shared.generated.resources.ic_bar_chart
import chartsproject.charts_demo_shared.generated.resources.ic_line_chart
import chartsproject.charts_demo_shared.generated.resources.ic_multi_line_chart
import chartsproject.charts_demo_shared.generated.resources.ic_pie_chart
import chartsproject.charts_demo_shared.generated.resources.ic_radar_chart
import chartsproject.charts_demo_shared.generated.resources.ic_stacked_bar_chart
import model.ChartType
import org.jetbrains.compose.resources.DrawableResource
import chartsproject.charts_demo_shared.generated.resources.Res as SharedRes

internal fun chartTypeIconResource(type: ChartType): DrawableResource =
    when (type) {
        ChartType.PIE -> SharedRes.drawable.ic_pie_chart
        ChartType.LINE -> SharedRes.drawable.ic_line_chart
        ChartType.MULTI_LINE -> SharedRes.drawable.ic_multi_line_chart
        ChartType.BAR -> SharedRes.drawable.ic_bar_chart
        ChartType.STACKED_BAR -> SharedRes.drawable.ic_stacked_bar_chart
        ChartType.AREA -> SharedRes.drawable.ic_stacked_bar_chart
        ChartType.RADAR -> SharedRes.drawable.ic_radar_chart
    }
