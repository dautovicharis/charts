package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.bar.BarChartCodeGenerator
import codegen.bar.barStylePropertiesSnapshot
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import model.BAR_CHART_TITLE
import model.BarCodegenConfig
import model.BarStyleState
import model.ChartType
import model.DataEditorColumn
import model.DataEditorState
import model.GeneratedSnippet
import model.PieSliceInput
import model.PlaygroundChartDefinition
import model.PlaygroundChartSession
import model.PlaygroundDataModel
import model.PlaygroundStyleState
import model.PlaygroundValidationResult
import model.SettingDescriptor
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.random.Random

internal object BarChartDefinition : PlaygroundChartDefinition {
    private val generator = BarChartCodeGenerator()

    override val type: ChartType = ChartType.BAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = BAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.bar.initialBarDataSet().toSimpleSeries()

    override fun defaultStyleState(): PlaygroundStyleState = BarStyleState()

    override fun createEditorState(model: PlaygroundDataModel): DataEditorState {
        val data = model as? PlaygroundDataModel.SimpleSeries ?: defaultDataModel() as PlaygroundDataModel.SimpleSeries
        return createSimpleSeriesEditor(
            model = data,
            minRows = 2,
            labelHeader = "Label",
        )
    }

    override fun validate(editorState: DataEditorState): PlaygroundValidationResult =
        validateSimpleSeries(
            editorState = editorState,
            chartName = "Bar chart",
            minRows = 2,
            labelPrefix = "Bar",
            clampToPositive = false,
        )

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Bar")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { -25f + it * 85f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Bars"),
            SettingDescriptor.Slider(
                id = "barAlpha",
                label = "Bar Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as BarStyleState).barAlpha },
                write = { style, value -> (style as BarStyleState).copy(barAlpha = value) },
            ),
            SettingDescriptor.Color(
                id = "barColor",
                label = "Bar Color",
                read = { style -> (style as BarStyleState).barColor },
                write = { style, value -> (style as BarStyleState).copy(barColor = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Visibility"),
            SettingDescriptor.Toggle(
                id = "gridVisible",
                label = "Show Grid",
                defaultValue = true,
                read = { style -> (style as BarStyleState).gridVisible },
                write = { style, value -> (style as BarStyleState).copy(gridVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "axisVisible",
                label = "Show Axes",
                defaultValue = true,
                read = { style -> (style as BarStyleState).axisVisible },
                write = { style, value -> (style as BarStyleState).copy(axisVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "selectionLineVisible",
                label = "Show Selection Line",
                defaultValue = true,
                read = { style -> (style as BarStyleState).selectionLineVisible },
                write = { style, value -> (style as BarStyleState).copy(selectionLineVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "selectionLineWidth",
                label = "Selection Line Width",
                defaultValue = 1f,
                min = 0f,
                max = 4f,
                steps = 16,
                read = { style -> (style as BarStyleState).selectionLineWidth },
                write = { style, value -> (style as BarStyleState).copy(selectionLineWidth = value) },
            ),
            SettingDescriptor.Toggle(
                id = "zoomControlsVisible",
                label = "Show Zoom Controls",
                defaultValue = true,
                read = { style -> (style as BarStyleState).zoomControlsVisible },
                write = { style, value -> (style as BarStyleState).copy(zoomControlsVisible = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val styleState = session.styleState as BarStyleState
        val dataSet =
            data.values.toChartDataSet(
                title = session.title,
                labels = data.labels,
            )
        val defaultStyle = BarChartDefaults.style()
        val style =
            BarChartDefaults.style(
                barColor = styleState.barColor ?: defaultStyle.barColor,
                barAlpha = styleState.barAlpha ?: defaultStyle.barAlpha,
                gridVisible = styleState.gridVisible ?: defaultStyle.gridVisible,
                axisVisible = styleState.axisVisible ?: defaultStyle.axisVisible,
                selectionLineVisible = styleState.selectionLineVisible ?: defaultStyle.selectionLineVisible,
                selectionLineWidth = styleState.selectionLineWidth ?: defaultStyle.selectionLineWidth,
                zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
            )
        BarChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val style = session.styleState as BarStyleState
        val points =
            data.values.mapIndexed { index, value ->
                PieSliceInput(
                    label = data.labels?.getOrNull(index) ?: "Bar ${index + 1}",
                    valueText = formatEditorFloat(value),
                )
            }

        return generator.generate(
            BarCodegenConfig(
                points = points,
                title = session.title,
                style = style,
                styleProperties = barStylePropertiesSnapshot(style),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
