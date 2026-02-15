package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.multiline.MultiLineChartCodeGenerator
import codegen.multiline.multiLineStylePropertiesSnapshot
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.LineChartDefaults
import model.ChartType
import model.DataEditorColumn
import model.DataEditorRow
import model.DataEditorState
import model.GeneratedSnippet
import model.MULTI_LINE_CHART_TITLE
import model.MultiLineCodegenConfig
import model.MultiLineStyleState
import model.MultiSeriesCodegenInput
import model.PlaygroundChartDefinition
import model.PlaygroundChartSession
import model.PlaygroundDataModel
import model.PlaygroundStyleState
import model.PlaygroundValidationResult
import model.SettingDescriptor
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.math.max
import kotlin.random.Random

internal object MultiLineChartDefinition : PlaygroundChartDefinition {
    private val generator = MultiLineChartCodeGenerator()

    override val type: ChartType = ChartType.MULTI_LINE
    override val displayName: String = type.displayName
    override val defaultTitle: String = MULTI_LINE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.multiLine
            .initialMultiLineSample()
            .dataSet
            .toMultiSeries()

    override fun defaultStyleState(): PlaygroundStyleState = MultiLineStyleState()

    override fun createEditorState(model: PlaygroundDataModel): DataEditorState {
        val data = model as? PlaygroundDataModel.MultiSeries ?: defaultDataModel() as PlaygroundDataModel.MultiSeries
        val columns =
            buildList {
                add(DataEditorColumn(id = LABEL_COLUMN_ID, label = "Category", numeric = false, weight = 1.4f))
                data.series.forEachIndexed { index, series ->
                    add(
                        DataEditorColumn(
                            id = "series_$index",
                            label = series.name,
                            numeric = true,
                            weight = 1f,
                            defaultValue = "0",
                        ),
                    )
                }
            }

        val categories = data.xLabels.orEmpty()
        val rowCount = max(categories.size, data.series.maxOfOrNull { series -> series.values.size } ?: 0)
        val rows =
            (0 until rowCount).map { rowIndex ->
                val cells = mutableMapOf<String, String>()
                cells[LABEL_COLUMN_ID] = categories.getOrNull(rowIndex) ?: "Point ${rowIndex + 1}"
                data.series.forEachIndexed { seriesIndex, series ->
                    cells["series_$seriesIndex"] = formatEditorFloat(series.values.getOrElse(rowIndex) { 0f })
                }
                DataEditorRow(id = rowIndex + 1, cells = cells)
            }

        return DataEditorState(
            columns = columns,
            rows = rows,
            minRows = 2,
        )
    }

    override fun validate(editorState: DataEditorState): PlaygroundValidationResult {
        if (editorState.rows.size < 2) {
            return PlaygroundValidationResult(
                sanitizedEditor = null,
                dataModel = null,
                message = "Multi line chart needs at least 2 rows.",
            )
        }
        val parsed =
            parseEditorTable(
                editorState = editorState,
                labelPrefix = "Point",
                clampToPositive = false,
            ) ?: return invalidNumericResult

        val series =
            parsed.numericColumns.map { column ->
                PlaygroundDataModel.MultiSeries.Series(
                    name = column.label,
                    values = parsed.valuesByColumn.getValue(column.id),
                )
            }

        return PlaygroundValidationResult(
            sanitizedEditor = editorState.copy(rows = parsed.sanitizedRows),
            dataModel = PlaygroundDataModel.MultiSeries(series = series, xLabels = parsed.labels),
            message = "Applied ${parsed.labels.size} rows.",
        )
    }

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Point")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { 220f + it * 520f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Multi Line"),
            SettingDescriptor.Slider(
                id = "lineAlpha",
                label = "Line Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as MultiLineStyleState).lineAlpha },
                write = { style, value -> (style as MultiLineStyleState).copy(lineAlpha = value) },
            ),
            SettingDescriptor.Toggle(
                id = "bezier",
                label = "Use Bezier Curves",
                defaultValue = true,
                read = { style -> (style as MultiLineStyleState).bezier },
                write = { style, value -> (style as MultiLineStyleState).copy(bezier = value) },
            ),
            SettingDescriptor.Toggle(
                id = "pointVisible",
                label = "Show Points",
                defaultValue = false,
                read = { style -> (style as MultiLineStyleState).pointVisible },
                write = { style, value -> (style as MultiLineStyleState).copy(pointVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "dragPointVisible",
                label = "Show Drag Point",
                defaultValue = true,
                read = { style -> (style as MultiLineStyleState).dragPointVisible },
                write = { style, value -> (style as MultiLineStyleState).copy(dragPointVisible = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "lineColors",
                title = "Line Colors",
                itemCount = {
                    val data = it.appliedData as PlaygroundDataModel.MultiSeries
                    data.series.size
                },
                read = { style -> (style as MultiLineStyleState).lineColors },
                write = { style, value -> (style as MultiLineStyleState).copy(lineColors = value) },
            ),
            SettingDescriptor.Color(
                id = "pointColor",
                label = "Point Color",
                read = { style -> (style as MultiLineStyleState).pointColor },
                write = { style, value -> (style as MultiLineStyleState).copy(pointColor = value) },
            ),
            SettingDescriptor.Color(
                id = "dragPointColor",
                label = "Drag Point Color",
                read = { style -> (style as MultiLineStyleState).dragPointColor },
                write = { style, value -> (style as MultiLineStyleState).copy(dragPointColor = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.MultiSeries
        val styleState = session.styleState as MultiLineStyleState
        val categories = data.xLabels.orEmpty()
        val dataSet =
            data.series
                .map { series -> series.name to series.values }
                .toMultiChartDataSet(
                    title = session.title,
                    categories = categories,
                    prefix = "$",
                )

        val defaultStyle = LineChartDefaults.style()
        val normalizedLineColors =
            styleState.lineColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.series.size)
            }
        val style =
            LineChartDefaults.style(
                lineColors = normalizedLineColors ?: defaultStyle.lineColors,
                lineAlpha = styleState.lineAlpha ?: defaultStyle.lineAlpha,
                bezier = styleState.bezier ?: defaultStyle.bezier,
                pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
                dragPointVisible = styleState.dragPointVisible ?: defaultStyle.dragPointVisible,
                pointColor = styleState.pointColor ?: defaultStyle.pointColor,
                dragPointColor = styleState.dragPointColor ?: defaultStyle.dragPointColor,
            )
        LineChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.MultiSeries
        val style = session.styleState as MultiLineStyleState
        return generator.generate(
            MultiLineCodegenConfig(
                series =
                    data.series.map { series ->
                        MultiSeriesCodegenInput(label = series.name, values = series.values)
                    },
                categories = data.xLabels.orEmpty(),
                title = session.title,
                style = style,
                styleProperties = multiLineStylePropertiesSnapshot(style, data.series.size),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
