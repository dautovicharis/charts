package ui

import chartsproject.app.generated.resources.Res
import chartsproject.app.generated.resources.ic_bar_chart
import chartsproject.app.generated.resources.ic_line_chart
import chartsproject.app.generated.resources.ic_multi_line_chart
import chartsproject.app.generated.resources.ic_pie_chart
import chartsproject.app.generated.resources.ic_radar_chart
import chartsproject.app.generated.resources.ic_stacked_bar_chart
import model.ChartType
import org.jetbrains.compose.resources.DrawableResource

internal fun chartTypeIconResource(type: ChartType): DrawableResource =
    when (type) {
        ChartType.PIE -> Res.drawable.ic_pie_chart
        ChartType.LINE -> Res.drawable.ic_line_chart
        ChartType.MULTI_LINE -> Res.drawable.ic_multi_line_chart
        ChartType.BAR -> Res.drawable.ic_bar_chart
        ChartType.STACKED_BAR -> Res.drawable.ic_stacked_bar_chart
        ChartType.AREA -> Res.drawable.ic_stacked_bar_chart
        ChartType.RADAR -> Res.drawable.ic_radar_chart
    }
