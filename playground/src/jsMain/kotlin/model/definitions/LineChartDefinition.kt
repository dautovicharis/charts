package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.line.LineChartCodeGenerator
import codegen.line.lineStylePropertiesSnapshot
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.LineChartDefaults
import model.ChartType
import model.DataEditorColumn
import model.DataEditorState
import model.DropdownOption
import model.GeneratedSnippet
import model.LINE_CHART_TITLE
import model.LineCodegenConfig
import model.LineStyleState
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

internal object LineChartDefinition : PlaygroundChartDefinition {
    private val generator = LineChartCodeGenerator()

    override val type: ChartType = ChartType.LINE
    override val displayName: String = type.displayName
    override val defaultTitle: String = LINE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundSampleUseCases.line.initialLineDataSet().toSimpleSeries()

    override fun defaultStyleState(): PlaygroundStyleState = LineStyleState()

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
            chartName = "Line chart",
            minRows = 2,
            labelPrefix = "Point",
            clampToPositive = false,
        )

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Point")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { 8f + it * 36f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> =
        listOf(
            SettingDescriptor.Section("Series"),
            SettingDescriptor.Slider(
                id = "lineAlpha",
                label = "Line Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as LineStyleState).lineAlpha },
                write = { style, value -> (style as LineStyleState).copy(lineAlpha = value) },
            ),
            SettingDescriptor.Dropdown(
                id = "curveMode",
                label = "Curve Mode",
                options =
                    listOf(
                        DropdownOption(label = "Bezier", value = "bezier"),
                        DropdownOption(label = "Linear", value = "linear"),
                    ),
                defaultValue = "bezier",
                read = { style -> if ((style as LineStyleState).bezier == false) "linear" else "bezier" },
                write = { style, value -> (style as LineStyleState).copy(bezier = value != "linear") },
            ),
            SettingDescriptor.Toggle(
                id = "pointVisible",
                label = "Show Points",
                defaultValue = false,
                read = { style -> (style as LineStyleState).pointVisible },
                write = { style, value -> (style as LineStyleState).copy(pointVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "pointSize",
                label = "Point Size",
                defaultValue = 9f,
                min = 2f,
                max = 20f,
                steps = 18,
                read = { style -> (style as LineStyleState).pointSize },
                write = { style, value -> (style as LineStyleState).copy(pointSize = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Colors"),
            SettingDescriptor.Color(
                id = "lineColor",
                label = "Line Color",
                read = { style -> (style as LineStyleState).lineColor },
                write = { style, value -> (style as LineStyleState).copy(lineColor = value) },
            ),
            SettingDescriptor.Color(
                id = "pointColor",
                label = "Point Color",
                read = { style -> (style as LineStyleState).pointColor },
                write = { style, value -> (style as LineStyleState).copy(pointColor = value) },
            ),
            SettingDescriptor.Color(
                id = "dragPointColor",
                label = "Drag Point Color",
                read = { style -> (style as LineStyleState).dragPointColor },
                write = { style, value -> (style as LineStyleState).copy(dragPointColor = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Interaction"),
            SettingDescriptor.Toggle(
                id = "dragPointVisible",
                label = "Show Drag Point",
                defaultValue = true,
                read = { style -> (style as LineStyleState).dragPointVisible },
                write = { style, value -> (style as LineStyleState).copy(dragPointVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "dragPointSize",
                label = "Drag Point Size",
                defaultValue = 7f,
                min = 2f,
                max = 20f,
                steps = 18,
                read = { style -> (style as LineStyleState).dragPointSize },
                write = { style, value -> (style as LineStyleState).copy(dragPointSize = value) },
            ),
            SettingDescriptor.Slider(
                id = "dragActivePointSize",
                label = "Active Drag Point Size",
                defaultValue = 12f,
                min = 2f,
                max = 24f,
                steps = 22,
                read = { style -> (style as LineStyleState).dragActivePointSize },
                write = { style, value -> (style as LineStyleState).copy(dragActivePointSize = value) },
            ),
            SettingDescriptor.Toggle(
                id = "zoomControlsVisible",
                label = "Show Zoom Controls",
                defaultValue = true,
                read = { style -> (style as LineStyleState).zoomControlsVisible },
                write = { style, value -> (style as LineStyleState).copy(zoomControlsVisible = value) },
            ),
            SettingDescriptor.Divider,
            SettingDescriptor.Section("Axes"),
            SettingDescriptor.Toggle(
                id = "axisVisible",
                label = "Show Axes",
                defaultValue = true,
                read = { style -> (style as LineStyleState).axisVisible },
                write = { style, value -> (style as LineStyleState).copy(axisVisible = value) },
            ),
            SettingDescriptor.Slider(
                id = "axisLineWidth",
                label = "Axis Line Width",
                defaultValue = 1f,
                min = 0f,
                max = 4f,
                steps = 16,
                read = { style -> (style as LineStyleState).axisLineWidth },
                write = { style, value -> (style as LineStyleState).copy(axisLineWidth = value) },
            ),
            SettingDescriptor.Toggle(
                id = "xAxisLabelsVisible",
                label = "Show X Labels",
                defaultValue = true,
                read = { style -> (style as LineStyleState).xAxisLabelsVisible },
                write = { style, value -> (style as LineStyleState).copy(xAxisLabelsVisible = value) },
            ),
            SettingDescriptor.Toggle(
                id = "yAxisLabelsVisible",
                label = "Show Y Labels",
                defaultValue = true,
                read = { style -> (style as LineStyleState).yAxisLabelsVisible },
                write = { style, value -> (style as LineStyleState).copy(yAxisLabelsVisible = value) },
            ),
        )

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val styleState = session.styleState as LineStyleState
        val dataSet =
            data.values.toChartDataSet(
                title = session.title,
                labels = data.labels,
            )
        val defaultStyle = LineChartDefaults.style()
        val style =
            LineChartDefaults.style(
                lineColor = styleState.lineColor ?: defaultStyle.lineColor,
                lineAlpha = styleState.lineAlpha ?: defaultStyle.lineAlpha,
                bezier = styleState.bezier ?: defaultStyle.bezier,
                pointColor = styleState.pointColor ?: defaultStyle.pointColor,
                pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
                pointSize = styleState.pointSize ?: defaultStyle.pointSize,
                dragPointColor = styleState.dragPointColor ?: defaultStyle.dragPointColor,
                dragPointVisible = styleState.dragPointVisible ?: defaultStyle.dragPointVisible,
                dragPointSize = styleState.dragPointSize ?: defaultStyle.dragPointSize,
                dragActivePointSize = styleState.dragActivePointSize ?: defaultStyle.dragActivePointSize,
                axisVisible = styleState.axisVisible ?: defaultStyle.axisVisible,
                axisLineWidth = styleState.axisLineWidth ?: defaultStyle.axisLineWidth,
                xAxisLabelsVisible = styleState.xAxisLabelsVisible ?: defaultStyle.xAxisLabelsVisible,
                yAxisLabelsVisible = styleState.yAxisLabelsVisible ?: defaultStyle.yAxisLabelsVisible,
                zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
            )
        LineChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val style = session.styleState as LineStyleState
        val points =
            data.values.mapIndexed { index, value ->
                PieSliceInput(
                    label = data.labels?.getOrNull(index) ?: "Point ${index + 1}",
                    valueText = formatEditorFloat(value),
                )
            }

        return generator.generate(
            LineCodegenConfig(
                points = points,
                title = session.title,
                style = style,
                styleProperties = lineStylePropertiesSnapshot(style),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
