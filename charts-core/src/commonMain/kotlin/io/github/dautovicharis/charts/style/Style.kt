package io.github.dautovicharis.charts.style

/**
 * An interface that represents a style for a chart.
 *
 * This interface is used to define the common behavior for all chart styles.
 * Each chart style should provide a list of its properties.
 *
 * Note: This interface is only used for demo purposes.
 */
fun interface Style {
    /**
     * Returns a list of the properties of the chart style.
     *
     * Each property is represented as a pair, where the first element is the name of the property
     * and the second element is the value of the property.
     *
     * @return A list of pairs representing the properties of the chart style.
     */
    fun getProperties(): List<Pair<String, Any>>
}
