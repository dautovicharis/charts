package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.area.AreaChartCodeGenerator
import codegen.area.areaStylePropertiesSnapshot
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import model.AREA_CHART_TITLE
import model.AreaCodegenConfig
import model.AreaStyleState
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
import model.SettingDescriptor
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.math.max
import kotlin.random.Random

internal object AreaChartDefinition : PlaygroundChartDefinition {
    private val generator = AreaChartCodeGenerator()

    override val type: ChartType = ChartType.AREA
    override val displayName: String = type.displayName
    override val defaultTitle: String = AREA_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.stackedArea
            .initialStackedAreaSample()
            .dataSet
            .toMultiSeries()

    override fun defaultStyleState(): PlaygroundStyleState = AreaStyleState()

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
                message = "Area chart needs at least 2 rows.",
            )
        }
        val parsed =
            parseEditorTable(
                editorState = editorState,
                labelPrefix = "Point",
                clampToPositive = true,
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
            valueProvider = { Random.nextFloat().let { 60f + it * 740f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Area"),
            SettingDescriptor.Slider(
                id = "fillAlpha",
                label = "Fill Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as AreaStyleState).fillAlpha },
                write = { style, value -> (style as AreaStyleState).copy(fillAlpha = value) },
            ),
            SettingDescriptor.Toggle(
                id = "lineVisible",
                label = "Show Lines",
                defaultValue = true,
                read = { style -> (style as AreaStyleState).lineVisible },
                write = { style, value -> (style as AreaStyleState).copy(lineVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "lineWidth",
                label = "Line Width",
                defaultValue = 1f,
                min = 0f,
                max = 8f,
                steps = 16,
                read = { style -> (style as AreaStyleState).lineWidth },
                write = { style, value -> (style as AreaStyleState).copy(lineWidth = value) },
            ),
            SettingDescriptor.Toggle(
                id = "bezier",
                label = "Use Bezier Curves",
                defaultValue = true,
                read = { style -> (style as AreaStyleState).bezier },
                write = { style, value -> (style as AreaStyleState).copy(bezier = value) },
            ),
            SettingDescriptor.Toggle(
                id = "zoomControlsVisible",
                label = "Show Zoom Controls",
                defaultValue = true,
                read = { style -> (style as AreaStyleState).zoomControlsVisible },
                write = { style, value -> (style as AreaStyleState).copy(zoomControlsVisible = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "areaColors",
                title = "Area Colors",
                itemCount = {
                    val data = it.appliedData as PlaygroundDataModel.MultiSeries
                    data.series.size
                },
                read = { style -> (style as AreaStyleState).areaColors },
                write = { style, value -> (style as AreaStyleState).copy(areaColors = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "lineColors",
                title = "Line Colors",
                itemCount = {
                    val data = it.appliedData as PlaygroundDataModel.MultiSeries
                    data.series.size
                },
                read = { style -> (style as AreaStyleState).lineColors },
                write = { style, value -> (style as AreaStyleState).copy(lineColors = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.MultiSeries
        val styleState = session.styleState as AreaStyleState
        val categories = data.xLabels.orEmpty()
        val dataSet =
            data.series
                .map { series -> series.name to series.values }
                .toMultiChartDataSet(
                    title = session.title,
                    categories = categories,
                )

        val defaultStyle = StackedAreaChartDefaults.style()
        val normalizedAreaColors =
            styleState.areaColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.series.size)
            }
        val normalizedLineColors =
            styleState.lineColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.series.size)
            }
        val style =
            StackedAreaChartDefaults.style(
                areaColors = normalizedAreaColors ?: defaultStyle.areaColors,
                lineColors = normalizedLineColors ?: defaultStyle.lineColors,
                fillAlpha = styleState.fillAlpha ?: defaultStyle.fillAlpha,
                lineVisible = styleState.lineVisible ?: defaultStyle.lineVisible,
                lineWidth = styleState.lineWidth ?: defaultStyle.lineWidth,
                bezier = styleState.bezier ?: defaultStyle.bezier,
                zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
            )
        StackedAreaChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.MultiSeries
        val style = session.styleState as AreaStyleState
        return generator.generate(
            AreaCodegenConfig(
                series =
                    data.series.map { series ->
                        MultiSeriesCodegenInput(label = series.name, values = series.values)
                    },
                categories = data.xLabels.orEmpty(),
                title = session.title,
                style = style,
                styleProperties = areaStylePropertiesSnapshot(style, data.series.size),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
