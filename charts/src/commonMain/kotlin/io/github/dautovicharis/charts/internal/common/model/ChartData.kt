package io.github.dautovicharis.charts.internal.common.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.jvm.JvmName

internal class ChartData(data: List<Pair<String, Double>>) {
    val data: ImmutableList<Pair<String, Double>> = data.toImmutableList()
    val labels: ImmutableList<String> = this.data.map { it.first }.toImmutableList()
    val points: ImmutableList<Double> = this.data.map { it.second }.toImmutableList()
}

private fun parseStringToDouble(value: String): Double {
    return value.toDoubleOrNull() ?: Double.NaN
}

@JvmName("toDoubleChartData")
internal fun List<Double>.toChartData(
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null
): ChartData = ChartData(
    this.mapIndexed { index, it ->
        (if (!labels.isNullOrEmpty()) labels[index] else "${prefix}${it}${postfix}") to it
    }
)

@JvmName("toFloatChartData")
internal fun List<Float>.toChartData(
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null
): ChartData = ChartData(
    this.mapIndexed { index, it ->
        (if (!labels.isNullOrEmpty()) labels[index] else "${prefix}${it}${postfix}") to it.toDouble()
    }
)

@JvmName("toStringChartData")
internal fun List<String>.toChartData(
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null
): ChartData = ChartData(this.mapIndexed { index, value ->
    val label = if (!labels.isNullOrEmpty()) labels[index] else "${prefix}${value}${postfix}"
    label to parseStringToDouble(value)
})

@JvmName("toIntChartData")
internal fun List<Int>.toChartData(
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null
): ChartData = ChartData(
    this.mapIndexed { index, it ->
        (if (!labels.isNullOrEmpty()) labels[index] else "${prefix}${it}${postfix}") to it.toDouble()
    }
)
