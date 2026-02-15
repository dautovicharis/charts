package codegen.common

data class MultiSeriesItem(
    val label: String,
    val values: List<Float>,
)

fun buildMultiChartDataSetCode(
    items: List<MultiSeriesItem>,
    title: String,
    categories: List<String>,
    prefix: String? = null,
): List<String> {
    val lines = mutableListOf<String>()

    lines += kotlinLine(1, "val items = listOf(")
    items.forEach { item ->
        val valuesCode = item.values.joinToString(", ") { value -> formatKotlinFloatLiteral(value) }
        lines +=
            kotlinLine(
                2,
                "\"${escapeKotlinString(item.label)}\" to listOf($valuesCode),",
            )
    }
    lines += kotlinLine(1, ")")
    lines += ""

    val categoriesCode =
        categories.joinToString(", ") { label ->
            "\"${escapeKotlinString(label)}\""
        }

    lines += kotlinLine(1, "val dataSet = items.toMultiChartDataSet(")
    lines += kotlinLine(2, "title = \"${escapeKotlinString(title)}\",")
    prefix?.let { lines += kotlinLine(2, "prefix = \"${escapeKotlinString(it)}\",") }
    lines += kotlinLine(2, "categories = listOf($categoriesCode),")
    lines += kotlinLine(1, ")")

    return lines
}
