package io.github.dautovicharis.charts.model

import io.github.dautovicharis.charts.internal.common.model.ChartDataItem
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.common.model.toChartData
import kotlin.jvm.JvmName

/**
 * A class that represents a data set for a chart.
 *
 * @param items The list of data items.
 * @param title The title of the data set.
 * @param prefix The prefix to be added to each data item. Defaults to an empty string.
 * @param postfix The postfix to be added to each data item. Defaults to an empty string.
 * @param labels The labels to be used for each data item.
 */
class ChartDataSet constructor(
    items: ChartDataType,
    title: String,
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null,
) {
    val data: ChartDataItem =
        ChartDataItem(
            label = title,
            item =
                when (items) {
                    is ChartDataType.FloatData ->
                        items.values.toChartData(
                            prefix = prefix,
                            postfix = postfix,
                            labels = labels,
                        )

                    is ChartDataType.DoubleData ->
                        items.values.toChartData(
                            prefix = prefix,
                            postfix = postfix,
                            labels = labels,
                        )

                    is ChartDataType.IntData ->
                        items.values.toChartData(
                            prefix = prefix,
                            postfix = postfix,
                            labels = labels,
                        )

                    is ChartDataType.StringData ->
                        items.values.toChartData(
                            prefix = prefix,
                            postfix = postfix,
                            labels = labels,
                        )
                },
        )

    /**
     * @constructor Creates a new ChartDataSet with the provided items, title, and labels.
     *
     * @param items The list of data items.
     * @param title The title of the data set.
     * @param labels The labels to be used for each data item..
     */
    constructor(items: ChartDataType, title: String, labels: List<String>) :
        this(items = items, title = title, prefix = "", postfix = "", labels = labels)
}

@JvmName("toFloatChartDataSet")
fun List<Float>.toChartDataSet(
    title: String,
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null,
): ChartDataSet = ChartDataSet(ChartDataType.FloatData(this), title, prefix, postfix, labels)

@JvmName("toDoubleChartDataSet")
fun List<Double>.toChartDataSet(
    title: String,
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null,
): ChartDataSet = ChartDataSet(ChartDataType.DoubleData(this), title, prefix, postfix, labels)

@JvmName("toIntChartDataSet")
fun List<Int>.toChartDataSet(
    title: String,
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null,
): ChartDataSet = ChartDataSet(ChartDataType.IntData(this), title, prefix, postfix, labels)

@JvmName("toStringChartDataSet")
fun List<String>.toChartDataSet(
    title: String,
    prefix: String = "",
    postfix: String = "",
    labels: List<String>? = null,
): ChartDataSet = ChartDataSet(ChartDataType.StringData(this), title, prefix, postfix, labels)
