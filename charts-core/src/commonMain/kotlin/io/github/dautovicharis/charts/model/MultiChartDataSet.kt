package io.github.dautovicharis.charts.model

import io.github.dautovicharis.charts.internal.common.model.ChartDataItem
import io.github.dautovicharis.charts.internal.common.model.ChartDataType
import io.github.dautovicharis.charts.internal.common.model.MultiChartData
import io.github.dautovicharis.charts.internal.common.model.toChartData
import kotlin.jvm.JvmName

/**
 * A class that represents a data set for a chart.
 *
 * @property data The data item that represents the data set.
 * @constructor Creates a new ChartDataSet with the provided items, categories, title, prefix, and postfix.
 *
 * @param items The list of data items.
 * @param categories The list of categories for the data items.
 * @param title The title of the data set.
 * @param prefix The prefix to be added to each data item. Defaults to an empty string.
 * @param postfix The postfix to be added to each data item. Defaults to an empty string.
 */
class MultiChartDataSet constructor(
    items: List<Pair<String, ChartDataType>>,
    categories: List<String> = emptyList(),
    title: String,
    prefix: String = "",
    postfix: String = "",
) {
    val data: MultiChartData

    init {
        val dataItems =
            items.map {
                ChartDataItem(
                    label = it.first,
                    item =
                        when (val item = it.second) {
                            is ChartDataType.FloatData ->
                                item.values.toChartData(
                                    prefix = prefix,
                                    postfix = postfix,
                                )

                            is ChartDataType.DoubleData ->
                                item.values.toChartData(
                                    prefix = prefix,
                                    postfix = postfix,
                                )

                            is ChartDataType.IntData ->
                                item.values.toChartData(
                                    prefix = prefix,
                                    postfix = postfix,
                                )

                            is ChartDataType.StringData ->
                                item.values.toChartData(
                                    prefix = prefix,
                                    postfix = postfix,
                                )
                        },
                )
            }

        data =
            MultiChartData(
                items = dataItems,
                title = title,
                categories = categories,
            )
    }
}

@JvmName("toFloatMultiChartDataSet")
fun List<Pair<String, List<Float>>>.toMultiChartDataSet(
    title: String,
    categories: List<String> = emptyList(),
    prefix: String = "",
    postfix: String = "",
): MultiChartDataSet {
    val items =
        this.map { (label, values) ->
            label to ChartDataType.FloatData(values)
        }
    return MultiChartDataSet(items, categories, title, prefix, postfix)
}

@JvmName("toDoubleMultiChartDataSet")
fun List<Pair<String, List<Double>>>.toMultiChartDataSet(
    title: String,
    categories: List<String> = emptyList(),
    prefix: String = "",
    postfix: String = "",
): MultiChartDataSet {
    val items =
        this.map { (label, values) ->
            label to ChartDataType.DoubleData(values)
        }
    return MultiChartDataSet(items, categories, title, prefix, postfix)
}

@JvmName("toIntMultiChartDataSet")
fun List<Pair<String, List<Int>>>.toMultiChartDataSet(
    title: String,
    categories: List<String> = emptyList(),
    prefix: String = "",
    postfix: String = "",
): MultiChartDataSet {
    val items =
        this.map { (label, values) ->
            label to ChartDataType.IntData(values)
        }
    return MultiChartDataSet(items, categories, title, prefix, postfix)
}

@JvmName("toStringMultiChartDataSet")
fun List<Pair<String, List<String>>>.toMultiChartDataSet(
    title: String,
    categories: List<String> = emptyList(),
    prefix: String = "",
    postfix: String = "",
): MultiChartDataSet {
    val items =
        this.map { (label, values) ->
            label to ChartDataType.StringData(values)
        }
    return MultiChartDataSet(items, categories, title, prefix, postfix)
}
