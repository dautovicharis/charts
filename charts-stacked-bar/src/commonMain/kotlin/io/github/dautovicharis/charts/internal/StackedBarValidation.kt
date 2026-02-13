package io.github.dautovicharis.charts.internal

import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_STACKED_BAR
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.style.StackedBarChartStyle

fun validateBarData(
    data: MultiChartData,
    style: StackedBarChartStyle,
): List<String> {
    val firstPointsSize = data.items.first().item.points.size
    val colorsSize = style.barColors.size

    return validateMultiSeriesChartData(
        data = data,
        pointsSize = firstPointsSize,
        minRequiredPointsSize = MIN_REQUIRED_STACKED_BAR,
        colorsSize = colorsSize,
        expectedColorsSize = firstPointsSize,
    )
}
