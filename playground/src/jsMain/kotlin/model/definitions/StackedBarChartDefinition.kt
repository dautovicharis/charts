package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.stackedbar.StackedBarChartCodeGenerator
import codegen.stackedbar.stackedBarStylePropertiesSnapshot
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import model.ChartType
import model.DataEditorColumn
import model.DataEditorRow
import model.DataEditorState
import model.GeneratedSnippet
import model.MultiSeriesCodegenInput
import model.PlaygroundChartDefinition
import model.PlaygroundChartSession
import model.PlaygroundDataModel
import model.PlaygroundStyleState
import model.PlaygroundValidationResult
import model.STACKED_BAR_CHART_TITLE
import model.SettingDescriptor
import model.StackedBarCodegenConfig
import model.StackedBarStyleState
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.random.Random

internal object StackedBarChartDefinition : PlaygroundChartDefinition {
    private val generator = StackedBarChartCodeGenerator()

    override val type: ChartType = ChartType.STACKED_BAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = STACKED_BAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.stackedBar
            .initialStackedBarSample()
            .dataSet
            .toStackedSeries()

    override fun defaultStyleState(): PlaygroundStyleState = StackedBarStyleState()

    override fun createEditorState(model: PlaygroundDataModel): DataEditorState {
        val data =
            model as? PlaygroundDataModel.StackedSeries ?: defaultDataModel() as PlaygroundDataModel.StackedSeries
        val columns =
            buildList {
                add(DataEditorColumn(id = LABEL_COLUMN_ID, label = "Category", numeric = false, weight = 1.4f))
                data.segmentNames.forEachIndexed { index, name ->
                    add(
                        DataEditorColumn(
                            id = "segment_$index",
                            label = name,
                            numeric = true,
                            weight = 1f,
                            defaultValue = "0",
                        ),
                    )
                }
            }

        val rowCount = data.bars.size
        val rows =
            (0 until rowCount).map { rowIndex ->
                val bar = data.bars[rowIndex]
                val cells = mutableMapOf<String, String>()
                cells[LABEL_COLUMN_ID] = bar.label
                data.segmentNames.indices.forEach { segmentIndex ->
                    cells["segment_$segmentIndex"] = formatEditorFloat(bar.values.getOrElse(segmentIndex) { 0f })
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
                message = "Stacked bar chart needs at least 2 rows.",
            )
        }
        val parsed =
            parseEditorTable(
                editorState = editorState,
                labelPrefix = "Bar",
                clampToPositive = true,
            ) ?: return invalidNumericResult(editorState)

        val segmentNames = parsed.numericColumns.map { column -> column.label }
        val bars =
            parsed.labels.mapIndexed { rowIndex, label ->
                val values = parsed.numericColumns.map { column -> parsed.valuesByColumn.getValue(column.id)[rowIndex] }
                PlaygroundDataModel.StackedSeries.StackedBar(
                    label = label,
                    values = values,
                )
            }

        return PlaygroundValidationResult(
            sanitizedEditor = editorState.copy(rows = parsed.sanitizedRows),
            dataModel =
                PlaygroundDataModel.StackedSeries(
                    segmentNames = segmentNames,
                    bars = bars,
                    labels = parsed.labels,
                ),
            message = "Applied ${parsed.labels.size} rows.",
        )
    }

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Bar")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { 30f + it * 180f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Stacked Bar"),
            SettingDescriptor.Slider(
                id = "barAlpha",
                label = "Bar Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as StackedBarStyleState).barAlpha },
                write = { style, value -> (style as StackedBarStyleState).copy(barAlpha = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "barColors",
                title = "Segment Colors",
                itemCount = {
                    val data = it.appliedData as PlaygroundDataModel.StackedSeries
                    data.segmentNames.size
                },
                read = { style -> (style as StackedBarStyleState).barColors },
                write = { style, value -> (style as StackedBarStyleState).copy(barColors = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Interaction"),
            SettingDescriptor.Toggle(
                id = "selectionLineVisible",
                label = "Show Selection Line",
                defaultValue = true,
                read = { style -> (style as StackedBarStyleState).selectionLineVisible },
                write = { style, value -> (style as StackedBarStyleState).copy(selectionLineVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "selectionLineWidth",
                label = "Selection Line Width",
                defaultValue = 1f,
                min = 0f,
                max = 4f,
                steps = 16,
                read = { style -> (style as StackedBarStyleState).selectionLineWidth },
                write = { style, value -> (style as StackedBarStyleState).copy(selectionLineWidth = value) },
            ),
            SettingDescriptor.Toggle(
                id = "zoomControlsVisible",
                label = "Show Zoom Controls",
                defaultValue = true,
                read = { style -> (style as StackedBarStyleState).zoomControlsVisible },
                write = { style, value -> (style as StackedBarStyleState).copy(zoomControlsVisible = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.StackedSeries
        val styleState = session.styleState as StackedBarStyleState
        val series =
            data.segmentNames.mapIndexed { segmentIndex, name ->
                name to
                    data.bars.map { bar ->
                        bar.values.getOrElse(segmentIndex) { 0f }
                    }
            }

        val categories = data.bars.map { bar -> bar.label }
        val dataSet =
            series.toMultiChartDataSet(
                title = session.title,
                categories = categories,
                prefix = "$",
            )

        val defaultStyle = StackedBarChartDefaults.style()
        val normalizedBarColors =
            styleState.barColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.segmentNames.size)
            }
        val style =
            StackedBarChartDefaults.style(
                barColors = normalizedBarColors ?: defaultStyle.barColors,
                barAlpha = styleState.barAlpha ?: defaultStyle.barAlpha,
                selectionLineVisible = styleState.selectionLineVisible ?: defaultStyle.selectionLineVisible,
                selectionLineWidth = styleState.selectionLineWidth ?: defaultStyle.selectionLineWidth,
                zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
            )
        StackedBarChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.StackedSeries
        val style = session.styleState as StackedBarStyleState
        val series =
            data.segmentNames.mapIndexed { segmentIndex, name ->
                MultiSeriesCodegenInput(
                    label = name,
                    values = data.bars.map { bar -> bar.values.getOrElse(segmentIndex) { 0f } },
                )
            }
        val categories = data.bars.map { bar -> bar.label }

        return generator.generate(
            StackedBarCodegenConfig(
                series = series,
                categories = categories,
                title = session.title,
                style = style,
                styleProperties = stackedBarStylePropertiesSnapshot(style, data.segmentNames.size),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
