package model.definitions

import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.app.data.BarSampleUseCase
import io.github.dautovicharis.charts.app.data.LineSampleUseCase
import io.github.dautovicharis.charts.app.data.MultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.PieSampleUseCase
import io.github.dautovicharis.charts.app.data.RadarSampleUseCase
import io.github.dautovicharis.charts.app.data.StackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.StackedBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultMultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultPieSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultRadarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedBarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet
import model.DataEditorColumn
import model.DataEditorRow
import model.DataEditorState
import model.PlaygroundDataModel
import model.PlaygroundValidationResult
import model.formatEditorFloat
import kotlin.math.max

internal const val LABEL_COLUMN_ID = "label"

internal object PlaygroundSampleUseCases {
    val pie: PieSampleUseCase = DefaultPieSampleUseCase()
    val line: LineSampleUseCase = DefaultLineSampleUseCase()
    val bar: BarSampleUseCase = DefaultBarSampleUseCase()
    val multiLine: MultiLineSampleUseCase = DefaultMultiLineSampleUseCase()
    val stackedBar: StackedBarSampleUseCase = DefaultStackedBarSampleUseCase()
    val stackedArea: StackedAreaSampleUseCase = DefaultStackedAreaSampleUseCase()
    val radar: RadarSampleUseCase = DefaultRadarSampleUseCase()
}

internal fun ChartDataSet.toSimpleSeries(labelsOverride: List<String>? = null): PlaygroundDataModel.SimpleSeries {
    val labels = labelsOverride ?: data.item.labels.toList()
    return PlaygroundDataModel.SimpleSeries(
        values = data.item.points.map(Double::toFloat),
        labels = labels.takeIf { it.isNotEmpty() },
    )
}

internal fun MultiChartDataSet.toMultiSeries(): PlaygroundDataModel.MultiSeries =
    PlaygroundDataModel.MultiSeries(
        series =
            data.items.map { item ->
                PlaygroundDataModel.MultiSeries.Series(
                    name = item.label,
                    values = item.item.points.map(Double::toFloat),
                )
            },
        xLabels = data.categories.toList().takeIf { it.isNotEmpty() },
    )

internal fun MultiChartDataSet.toStackedSeries(): PlaygroundDataModel.StackedSeries {
    val segmentNames = data.items.map { item -> item.label }
    val categoryLabels = data.categories.toList()
    val valuesPerSegment =
        data.items.map { item ->
            item.item.points.map(Double::toFloat)
        }
    val maxPoints =
        max(
            categoryLabels.size,
            valuesPerSegment.maxOfOrNull { points -> points.size } ?: 0,
        )
    val labels =
        if (maxPoints == 0) {
            emptyList()
        } else {
            List(maxPoints) { index ->
                categoryLabels.getOrNull(index) ?: "Bar ${index + 1}"
            }
        }
    val bars =
        labels.indices.map { pointIndex ->
            PlaygroundDataModel.StackedSeries.StackedBar(
                label = labels[pointIndex],
                values = valuesPerSegment.map { points -> points.getOrElse(pointIndex) { 0f } },
            )
        }
    return PlaygroundDataModel.StackedSeries(
        segmentNames = segmentNames,
        bars = bars,
        labels = labels,
    )
}

internal fun MultiChartDataSet.toRadarSeries(): PlaygroundDataModel.RadarSeries {
    val entries =
        data.items.map { item ->
            PlaygroundDataModel.RadarSeries.RadarEntry(
                name = item.label,
                values = item.item.points.map(Double::toFloat),
            )
        }
    val maxPoints = entries.maxOfOrNull { entry -> entry.values.size } ?: 0
    val axes =
        if (data.categories.isNotEmpty()) {
            data.categories.toList()
        } else {
            List(maxPoints) { index -> "Axis ${index + 1}" }
        }
    return PlaygroundDataModel.RadarSeries(entries = entries, axes = axes)
}

internal fun createSimpleSeriesEditor(
    model: PlaygroundDataModel.SimpleSeries,
    minRows: Int,
    labelHeader: String,
): DataEditorState {
    val labels = model.labels.orEmpty()
    val rowCount = max(labels.size, model.values.size)
    val columns =
        listOf(
            DataEditorColumn(id = LABEL_COLUMN_ID, label = labelHeader, numeric = false, weight = 1.5f),
            DataEditorColumn(id = "value", label = "Value", numeric = true, weight = 1f, defaultValue = "0"),
        )
    val rows =
        (0 until rowCount).map { index ->
            DataEditorRow(
                id = index + 1,
                cells =
                    mapOf(
                        LABEL_COLUMN_ID to (labels.getOrNull(index) ?: "Item ${index + 1}"),
                        "value" to formatEditorFloat(model.values.getOrElse(index) { 0f }),
                    ),
            )
        }
    return DataEditorState(columns = columns, rows = rows, minRows = minRows)
}

internal fun validateSimpleSeries(
    editorState: DataEditorState,
    chartName: String,
    minRows: Int,
    labelPrefix: String,
    clampToPositive: Boolean,
): PlaygroundValidationResult {
    if (editorState.rows.size < minRows) {
        return PlaygroundValidationResult(
            sanitizedEditor = null,
            dataModel = null,
            message = "$chartName needs at least $minRows rows.",
        )
    }
    val parsed =
        parseEditorTable(
            editorState = editorState,
            labelPrefix = labelPrefix,
            clampToPositive = clampToPositive,
        ) ?: return invalidNumericResult(editorState)

    val valueColumn = parsed.numericColumns.firstOrNull() ?: return invalidNumericResult(editorState)
    val values = parsed.valuesByColumn.getValue(valueColumn.id)

    return PlaygroundValidationResult(
        sanitizedEditor = editorState.copy(rows = parsed.sanitizedRows),
        dataModel = PlaygroundDataModel.SimpleSeries(values = values, labels = parsed.labels),
        message = "Applied ${parsed.labels.size} rows.",
    )
}

internal data class ParsedEditorTable(
    val labels: List<String>,
    val numericColumns: List<DataEditorColumn>,
    val valuesByColumn: Map<String, List<Float>>,
    val sanitizedRows: List<DataEditorRow>,
)

internal fun parseEditorTable(
    editorState: DataEditorState,
    labelPrefix: String,
    clampToPositive: Boolean,
): ParsedEditorTable? {
    val labelColumn =
        editorState.columns.firstOrNull { column -> !column.numeric }
            ?: return null
    val numericColumns = editorState.columns.filter { column -> column.numeric }
    if (numericColumns.isEmpty()) {
        return null
    }

    val labels = mutableListOf<String>()
    val valuesByColumn = numericColumns.associate { column -> column.id to mutableListOf<Float>() }

    editorState.rows.forEachIndexed { index, row ->
        val label =
            row.cells[labelColumn.id]
                .orEmpty()
                .trim()
                .ifBlank { "$labelPrefix ${index + 1}" }
        labels += label

        numericColumns.forEach { column ->
            val parsedValue =
                row.cells[column.id]
                    .orEmpty()
                    .trim()
                    .toFloatOrNull() ?: return null
            valuesByColumn.getValue(column.id) += if (clampToPositive) parsedValue.coerceAtLeast(0f) else parsedValue
        }
    }

    val sanitizedRows =
        labels.indices.map { rowIndex ->
            val cells = mutableMapOf<String, String>()
            cells[labelColumn.id] = labels[rowIndex]
            numericColumns.forEach { column ->
                val value = valuesByColumn.getValue(column.id)[rowIndex]
                cells[column.id] = formatEditorFloat(value)
            }
            DataEditorRow(id = editorState.rows[rowIndex].id, cells = cells)
        }

    return ParsedEditorTable(
        labels = labels,
        numericColumns = numericColumns,
        valuesByColumn = valuesByColumn,
        sanitizedRows = sanitizedRows,
    )
}

internal fun invalidNumericResult(editorState: DataEditorState): PlaygroundValidationResult =
    PlaygroundValidationResult(
        sanitizedEditor = null,
        dataModel = null,
        message = "Please enter valid numeric values in all rows.",
        invalidRowIds = invalidNumericRowIds(editorState),
    )

internal fun invalidNumericRowIds(editorState: DataEditorState): Set<Int> {
    val numericColumns = editorState.columns.filter { column -> column.numeric }
    if (numericColumns.isEmpty()) return emptySet()
    return buildSet {
        editorState.rows.forEachIndexed { index, row ->
            val hasInvalidNumericCell =
                numericColumns.any { column ->
                    row.cells[column.id]
                        .orEmpty()
                        .trim()
                        .toFloatOrNull() == null
                }
            if (hasInvalidNumericCell) add(index + 1)
        }
    }
}

internal fun defaultRowCells(
    columns: List<DataEditorColumn>,
    rowIndex: Int,
    labelPrefix: String,
): Map<String, String> {
    val cells = mutableMapOf<String, String>()
    columns.forEach { column ->
        cells[column.id] =
            if (column.numeric) {
                column.defaultValue.ifBlank { "0" }
            } else {
                "$labelPrefix ${rowIndex + 1}"
            }
    }
    return cells
}

internal fun randomizeEditorValues(
    editorState: DataEditorState,
    valueProvider: () -> Float,
): DataEditorState {
    val rows =
        editorState.rows.map { row ->
            val cells = row.cells.toMutableMap()
            editorState.columns.filter { column -> column.numeric }.forEach { column ->
                cells[column.id] = formatEditorFloat(valueProvider())
            }
            row.copy(cells = cells)
        }
    return editorState.copy(rows = rows)
}

internal fun normalizeColorCount(
    colors: List<Color>,
    targetCount: Int,
): List<Color> {
    if (targetCount <= 0 || colors.isEmpty()) return emptyList()
    return List(targetCount) { index -> colors[index % colors.size] }
}
