package io.github.dautovicharis.charts.internal.common.model

/**
 * A sealed class representing different types of chart data items.
 */
internal sealed class ChartDataType {
    internal data class FloatData(val values: List<Float>) : ChartDataType()
    internal data class DoubleData(val values: List<Double>) : ChartDataType()
    internal data class IntData(val values: List<Int>) : ChartDataType()
    internal data class StringData(val values: List<String>) : ChartDataType()
}
