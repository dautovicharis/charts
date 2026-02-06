package io.github.dautovicharis.charts.internal

import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_BAR
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_LINE
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_PIE
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_RADAR
import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_STACKED_BAR
import io.github.dautovicharis.charts.internal.common.model.ChartData
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.style.LineChartStyle
import io.github.dautovicharis.charts.style.PieChartStyle
import io.github.dautovicharis.charts.style.RadarChartStyle
import io.github.dautovicharis.charts.style.StackedBarChartStyle

internal object ValidationErrors {
    const val RULE_ITEM_POINTS_SIZE: String = "Item at index %d has %d points, expected %d."
    const val RULE_ITEM_POINT_NOT_NUMBER: String =
        "Item at index %d has non-numeric value at index %d."
    const val RULE_CATEGORIES_SIZE_MISMATCH: String =
        "Categories size %d does not match expected %d."
    const val RULE_COLORS_SIZE_MISMATCH: String = "Colors size %d does not match expected %d."
    const val RULE_DATA_POINTS_LESS_THAN_MIN: String =
        "Data points size should be greater than or equal to %d."
    const val RULE_DATA_POINT_NOT_NUMBER: String =
        "Data point at index %d is not a valid number."

    const val MIN_REQUIRED_PIE: Int = 2
    const val MIN_REQUIRED_LINE: Int = 2
    const val MIN_REQUIRED_STACKED_BAR: Int = 1
    const val MIN_REQUIRED_BAR: Int = 2
    const val MIN_REQUIRED_RADAR: Int = 3
}

internal fun String.format(vararg args: Any?): String {
    if (args.isEmpty()) return this
    return args.fold(this) { formattedString, arg ->
        formattedString.replaceFirst("%d", arg.toString())
    }
}

internal fun validateLineData(
    data: MultiChartData,
    style: LineChartStyle,
): List<String> {
    val firstPointsSize = data.items.first().item.points.size

    val colorsSize = style.lineColors.size
    val expectedColorsSize = data.items.size

    return validateChartData(
        data = data,
        pointsSize = firstPointsSize,
        minRequiredPointsSize = MIN_REQUIRED_LINE,
        colorsSize = colorsSize,
        expectedColorsSize = expectedColorsSize,
    )
}

internal fun validateRadarData(
    data: MultiChartData,
    style: RadarChartStyle,
): List<String> {
    val firstPointsSize = data.items.first().item.points.size

    val colorsSize = style.lineColors.size
    val expectedColorsSize = data.items.size

    return validateChartData(
        data = data,
        pointsSize = firstPointsSize,
        minRequiredPointsSize = MIN_REQUIRED_RADAR,
        colorsSize = colorsSize,
        expectedColorsSize = expectedColorsSize,
    )
}

internal fun validateBarData(
    data: MultiChartData,
    style: StackedBarChartStyle,
): List<String> {
    val firstPointsSize = data.items.first().item.points.size
    val colorsSize = style.barColors.size

    return validateChartData(
        data = data,
        pointsSize = firstPointsSize,
        minRequiredPointsSize = MIN_REQUIRED_STACKED_BAR,
        colorsSize = colorsSize,
        expectedColorsSize = firstPointsSize,
    )
}

internal fun validateBarData(data: ChartData): List<String> {
    val validationErrors = mutableListOf<String>()
    val pointsSize = data.points.size

    if (pointsSize < MIN_REQUIRED_BAR) {
        val validationError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_BAR)
        validationErrors.add(validationError)
        return validationErrors
    }

    data.points.forEachIndexed { index, value ->
        if (value.isNaN()) {
            val validationError = ValidationErrors.RULE_DATA_POINT_NOT_NUMBER.format(index)
            validationErrors.add(validationError)
        }
    }
    return validationErrors
}

internal fun validatePieData(
    dataSet: ChartDataSet,
    style: PieChartStyle,
): List<String> {
    val validationErrors = mutableListOf<String>()
    val pointsSize = dataSet.data.item.points.size
    val colorsSize = style.pieColors.size

    // Rule 1: pointsSize should be greater than minRequiredPointsSize
    if (pointsSize < MIN_REQUIRED_PIE) {
        val validationError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(MIN_REQUIRED_PIE)
        validationErrors.add(validationError)
        return validationErrors
    }

    dataSet.data.item.points.forEachIndexed { index, value ->
        if (value.isNaN()) {
            val validationError = ValidationErrors.RULE_DATA_POINT_NOT_NUMBER.format(index)
            validationErrors.add(validationError)
        }
    }

    // Rule 2: If colors are not empty, it should match pointsSize
    if (colorsSize > 0 && colorsSize != pointsSize) {
        val validationError =
            ValidationErrors.RULE_COLORS_SIZE_MISMATCH.format(colorsSize, pointsSize)
        validationErrors.add(validationError)
    }
    return validationErrors
}

private fun validateChartData(
    data: MultiChartData,
    pointsSize: Int,
    minRequiredPointsSize: Int,
    colorsSize: Int,
    expectedColorsSize: Int,
): List<String> {
    val validationErrors = mutableListOf<String>()

    // Rule 1: pointsSize should be greater than minRequiredPointsSize
    if (pointsSize < minRequiredPointsSize) {
        val validationError =
            ValidationErrors.RULE_DATA_POINTS_LESS_THAN_MIN.format(minRequiredPointsSize)
        validationErrors.add(validationError)
        return validationErrors
    }

    // Rule 2: Each item should have the same number of points
    data.items.forEachIndexed { index, dataItem ->
        if (dataItem.item.points.size != pointsSize) {
            val validationError =
                ValidationErrors.RULE_ITEM_POINTS_SIZE.format(
                    index,
                    dataItem.item.points.size,
                    pointsSize,
                )
            validationErrors.add(validationError)
        }
    }

    // Rule 3: If categories are not empty, it should match pointsSize
    if (data.hasCategories() && data.categories.size != pointsSize) {
        val validationError =
            ValidationErrors.RULE_CATEGORIES_SIZE_MISMATCH.format(
                data.categories.size,
                pointsSize,
            )
        validationErrors.add(validationError)
    }

    // Rule 4: If colors are not empty, it should match expectedColorsSize
    if (colorsSize > 0 && colorsSize != expectedColorsSize) {
        val validationError =
            ValidationErrors.RULE_COLORS_SIZE_MISMATCH.format(colorsSize, expectedColorsSize)
        validationErrors.add(validationError)
    }

    data.items.forEachIndexed { itemIndex, dataItem ->
        dataItem.item.points.forEachIndexed { pointIndex, value ->
            if (value.isNaN()) {
                val validationError =
                    ValidationErrors.RULE_ITEM_POINT_NOT_NUMBER.format(itemIndex, pointIndex)
                validationErrors.add(validationError)
            }
        }
    }
    return validationErrors
}
