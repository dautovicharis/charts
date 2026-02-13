package io.github.dautovicharis.charts.internal

import io.github.dautovicharis.charts.internal.ValidationErrors.MIN_REQUIRED_BAR
import io.github.dautovicharis.charts.internal.common.model.ChartData

fun validateBarData(data: ChartData): List<String> {
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
