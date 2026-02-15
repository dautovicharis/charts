package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.radar.RadarChartCodeGenerator
import codegen.radar.radarStylePropertiesSnapshot
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.RadarChartDefaults
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
import model.RADAR_CHART_TITLE
import model.RadarCodegenConfig
import model.RadarStyleState
import model.SettingDescriptor
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.random.Random

internal object RadarChartDefinition : PlaygroundChartDefinition {
    private val generator = RadarChartCodeGenerator()

    override val type: ChartType = ChartType.RADAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = RADAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.radar
            .initialRadarSample()
            .customDataSet
            .toRadarSeries()

    override fun defaultStyleState(): PlaygroundStyleState = RadarStyleState()

    override fun createEditorState(model: PlaygroundDataModel): DataEditorState {
        val data = model as? PlaygroundDataModel.RadarSeries ?: defaultDataModel() as PlaygroundDataModel.RadarSeries
        val columns =
            buildList {
                add(DataEditorColumn(id = LABEL_COLUMN_ID, label = "Axis", numeric = false, weight = 1.4f))
                data.entries.forEachIndexed { index, entry ->
                    add(
                        DataEditorColumn(
                            id = "entry_$index",
                            label = entry.name,
                            numeric = true,
                            weight = 1f,
                            defaultValue = "0",
                        ),
                    )
                }
            }

        val rowCount = data.axes.size
        val rows =
            (0 until rowCount).map { rowIndex ->
                val cells = mutableMapOf<String, String>()
                cells[LABEL_COLUMN_ID] = data.axes[rowIndex]
                data.entries.forEachIndexed { entryIndex, entry ->
                    cells["entry_$entryIndex"] = formatEditorFloat(entry.values.getOrElse(rowIndex) { 0f })
                }
                DataEditorRow(id = rowIndex + 1, cells = cells)
            }

        return DataEditorState(
            columns = columns,
            rows = rows,
            minRows = 3,
        )
    }

    override fun validate(editorState: DataEditorState): PlaygroundValidationResult {
        if (editorState.rows.size < 3) {
            return PlaygroundValidationResult(
                sanitizedEditor = null,
                dataModel = null,
                message = "Radar chart needs at least 3 rows.",
            )
        }
        val parsed =
            parseEditorTable(
                editorState = editorState,
                labelPrefix = "Axis",
                clampToPositive = true,
            ) ?: return invalidNumericResult

        val entries =
            parsed.numericColumns.map { column ->
                PlaygroundDataModel.RadarSeries.RadarEntry(
                    name = column.label,
                    values = parsed.valuesByColumn.getValue(column.id),
                )
            }

        return PlaygroundValidationResult(
            sanitizedEditor = editorState.copy(rows = parsed.sanitizedRows),
            dataModel = PlaygroundDataModel.RadarSeries(entries = entries, axes = parsed.labels),
            message = "Applied ${parsed.labels.size} rows.",
        )
    }

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Axis")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { 35f + it * 65f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Radar"),
            SettingDescriptor.Slider(
                id = "lineWidth",
                label = "Line Width",
                defaultValue = 2f,
                min = 0f,
                max = 8f,
                steps = 16,
                read = { style -> (style as RadarStyleState).lineWidth },
                write = { style, value -> (style as RadarStyleState).copy(lineWidth = value) },
            ),
            SettingDescriptor.Slider(
                id = "pointSize",
                label = "Point Size",
                defaultValue = 8f,
                min = 0f,
                max = 12f,
                steps = 12,
                read = { style -> (style as RadarStyleState).pointSize },
                write = { style, value -> (style as RadarStyleState).copy(pointSize = value) },
            ),
            SettingDescriptor.Slider(
                id = "fillAlpha",
                label = "Fill Transparency",
                defaultValue = 0.3f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as RadarStyleState).fillAlpha },
                write = { style, value -> (style as RadarStyleState).copy(fillAlpha = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Visibility"),
            SettingDescriptor.Toggle(
                id = "fillVisible",
                label = "Show Fill",
                defaultValue = true,
                read = { style -> (style as RadarStyleState).fillVisible },
                write = { style, value -> (style as RadarStyleState).copy(fillVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "pointVisible",
                label = "Show Points",
                defaultValue = true,
                read = { style -> (style as RadarStyleState).pointVisible },
                write = { style, value -> (style as RadarStyleState).copy(pointVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "gridVisible",
                label = "Show Grid",
                defaultValue = true,
                read = { style -> (style as RadarStyleState).gridVisible },
                write = { style, value -> (style as RadarStyleState).copy(gridVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "categoryLegendVisible",
                label = "Show Category Legend",
                defaultValue = true,
                read = { style -> (style as RadarStyleState).categoryLegendVisible },
                write = { style, value -> (style as RadarStyleState).copy(categoryLegendVisible = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "lineColors",
                title = "Line Colors",
                itemCount = {
                    val data = it.appliedData as PlaygroundDataModel.RadarSeries
                    data.entries.size
                },
                read = { style -> (style as RadarStyleState).lineColors },
                write = { style, value -> (style as RadarStyleState).copy(lineColors = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.RadarSeries
        val styleState = session.styleState as RadarStyleState
        val dataSet =
            data.entries
                .map { entry -> entry.name to entry.values }
                .toMultiChartDataSet(
                    title = session.title,
                    categories = data.axes,
                )

        val defaultStyle = RadarChartDefaults.style()
        val normalizedLineColors =
            styleState.lineColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.entries.size)
            }
        val style =
            RadarChartDefaults.style(
                lineColors = normalizedLineColors ?: defaultStyle.lineColors,
                lineWidth = styleState.lineWidth ?: defaultStyle.lineWidth,
                pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
                pointSize = styleState.pointSize ?: defaultStyle.pointSize,
                fillVisible = styleState.fillVisible ?: defaultStyle.fillVisible,
                fillAlpha = styleState.fillAlpha ?: defaultStyle.fillAlpha,
                gridVisible = styleState.gridVisible ?: defaultStyle.gridVisible,
                categoryLegendVisible = styleState.categoryLegendVisible ?: defaultStyle.categoryLegendVisible,
            )
        RadarChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.RadarSeries
        val style = session.styleState as RadarStyleState
        return generator.generate(
            RadarCodegenConfig(
                series =
                    data.entries.map { entry ->
                        MultiSeriesCodegenInput(label = entry.name, values = entry.values)
                    },
                categories = data.axes,
                title = session.title,
                style = style,
                styleProperties = radarStylePropertiesSnapshot(style, data.entries.size),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
