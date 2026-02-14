package io.github.dautovicharis.charts.internal.common.model

/**
 * A sealed class representing different types of chart data items.
 */
sealed class ChartDataType {
    data class FloatData(
        val values: List<Float>,
    ) : ChartDataType()

    data class DoubleData(
        val values: List<Double>,
    ) : ChartDataType()

    data class IntData(
        val values: List<Int>,
    ) : ChartDataType()

    data class StringData(
        val values: List<String>,
    ) : ChartDataType()
}
