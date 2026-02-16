package model

sealed interface PlaygroundDataModel {
    data class SimpleSeries(
        val values: List<Float>,
        val labels: List<String>? = null,
    ) : PlaygroundDataModel

    data class XYSeries(
        val points: List<Pair<Float, Float>>,
        val labels: List<String>? = null,
    ) : PlaygroundDataModel

    data class MultiSeries(
        val series: List<Series>,
        val xLabels: List<String>? = null,
    ) : PlaygroundDataModel {
        data class Series(
            val name: String,
            val values: List<Float>,
        )
    }

    data class StackedSeries(
        val segmentNames: List<String>,
        val bars: List<StackedBar>,
        val labels: List<String>? = null,
    ) : PlaygroundDataModel {
        data class StackedBar(
            val label: String,
            val values: List<Float>,
        )
    }

    data class RadarSeries(
        val entries: List<RadarEntry>,
        val axes: List<String>,
    ) : PlaygroundDataModel {
        data class RadarEntry(
            val name: String,
            val values: List<Float>,
        )
    }
}

data class DataEditorColumn(
    val id: String,
    val label: String,
    val numeric: Boolean,
    val weight: Float = 1f,
    val defaultValue: String = "",
)

data class DataEditorRow(
    val id: Int,
    val cells: Map<String, String>,
)

data class DataEditorState(
    val columns: List<DataEditorColumn>,
    val rows: List<DataEditorRow>,
    val minRows: Int,
)

fun DataEditorState.updateCell(
    rowIndex: Int,
    columnId: String,
    value: String,
): DataEditorState {
    if (rowIndex !in rows.indices) return this
    val nextRows =
        rows.toMutableList().also { mutableRows ->
            val row = mutableRows[rowIndex]
            mutableRows[rowIndex] = row.copy(cells = row.cells + (columnId to value))
        }
    return copy(rows = nextRows)
}

fun DataEditorState.withAddedRow(
    cells: Map<String, String>,
    insertAtTop: Boolean = false,
): DataEditorState {
    val nextId = (rows.maxOfOrNull { row -> row.id } ?: 0) + 1
    val nextRow = DataEditorRow(id = nextId, cells = cells)
    val nextRows =
        if (insertAtTop) {
            listOf(nextRow) + rows
        } else {
            rows + nextRow
        }
    return copy(rows = nextRows)
}

fun DataEditorState.withDeletedRow(index: Int): DataEditorState {
    if (rows.size <= minRows) return this
    if (index !in rows.indices) return this
    return copy(rows = rows.filterIndexed { rowIndex, _ -> rowIndex != index })
}

fun formatEditorFloat(value: Float): String =
    value
        .toString()
        .removeSuffix(".0")
