package model.definitions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import codegen.pie.PieChartCodeGenerator
import codegen.pie.pieStylePropertiesSnapshot
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.PieChartDefaults
import model.ChartType
import model.DataEditorColumn
import model.DataEditorState
import model.GeneratedSnippet
import model.PIE_CHART_TITLE
import model.PieCodegenConfig
import model.PieSliceInput
import model.PieStyleState
import model.PlaygroundChartDefinition
import model.PlaygroundChartSession
import model.PlaygroundDataModel
import model.PlaygroundStyleState
import model.PlaygroundValidationResult
import model.SettingDescriptor
import model.deriveFunctionName
import model.formatEditorFloat
import kotlin.random.Random

internal object PieChartDefinition : PlaygroundChartDefinition {
    private val generator = PieChartCodeGenerator()

    override val type: ChartType = ChartType.PIE
    override val displayName: String = type.displayName
    override val defaultTitle: String = PIE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel {
        val sample = PlaygroundSampleUseCases.pie.initialPieSample()
        return sample.dataSet.toSimpleSeries(labelsOverride = sample.segmentKeys)
    }

    override fun defaultStyleState(): PlaygroundStyleState = PieStyleState()

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
            chartName = "Pie chart",
            minRows = 2,
            labelPrefix = "Slice",
            clampToPositive = true,
        )

    override fun newRowCells(
        rowIndex: Int,
        columns: List<DataEditorColumn>,
    ): Map<String, String> = defaultRowCells(columns, rowIndex, labelPrefix = "Slice")

    override fun randomize(editorState: DataEditorState): DataEditorState =
        randomizeEditorValues(
            editorState = editorState,
            valueProvider = { Random.nextFloat().let { 8f + it * 48f } },
        )

    override fun settingsSchema(session: PlaygroundChartSession): List<SettingDescriptor> {
        val itemCount = (session.appliedData as PlaygroundDataModel.SimpleSeries).values.size
        return listOf(
            SettingDescriptor.Section("Pie Chart"),
            SettingDescriptor.Slider(
                id = "donutPercentage",
                label = "Donut Hole Size",
                defaultValue = 0f,
                min = 0f,
                max = 70f,
                steps = 20,
                read = { style -> (style as PieStyleState).donutPercentage },
                write = { style, value -> (style as PieStyleState).copy(donutPercentage = value) },
                format = { value -> "${value.toInt()}%" },
            ),
            SettingDescriptor.Slider(
                id = "borderWidth",
                label = "Border Width",
                defaultValue = 3f,
                min = 0f,
                max = 10f,
                steps = 20,
                read = { style -> (style as PieStyleState).borderWidth },
                write = { style, value -> (style as PieStyleState).copy(borderWidth = value) },
            ),
            SettingDescriptor.Slider(
                id = "pieAlpha",
                label = "Slice Transparency",
                defaultValue = 0.4f,
                min = 0f,
                max = 1f,
                steps = 20,
                read = { style -> (style as PieStyleState).pieAlpha },
                write = { style, value -> (style as PieStyleState).copy(pieAlpha = value) },
            ),
            SettingDescriptor.Toggle(
                id = "legendVisible",
                label = "Show Legend",
                defaultValue = true,
                read = { style -> (style as PieStyleState).legendVisible },
                write = { style, value -> (style as PieStyleState).copy(legendVisible = value) },
            ),
            SettingDescriptor.ColorPalette(
                id = "pieColors",
                title = "Slice Colors",
                itemCount = { itemCount },
                read = { style -> (style as PieStyleState).pieColors },
                write = { style, value -> (style as PieStyleState).copy(pieColors = value) },
            ),
        )
    }

    @Composable
    override fun renderPreview(
        session: PlaygroundChartSession,
        modifier: Modifier,
    ) {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val styleState = session.styleState as PieStyleState
        val dataSet =
            data.values.toChartDataSet(
                title = session.title,
                labels = data.labels,
            )
        val defaultStyle = PieChartDefaults.style()
        val normalizedPieColors =
            styleState.pieColors?.let { colors ->
                normalizeColorCount(colors = colors, targetCount = data.values.size)
            }
        val style =
            PieChartDefaults.style(
                donutPercentage = styleState.donutPercentage ?: defaultStyle.donutPercentage,
                borderWidth = styleState.borderWidth ?: defaultStyle.borderWidth,
                pieAlpha = styleState.pieAlpha ?: defaultStyle.pieAlpha,
                legendVisible = styleState.legendVisible ?: defaultStyle.legendVisible,
                pieColors = normalizedPieColors ?: defaultStyle.pieColors,
                pieColor = defaultStyle.pieColor,
                borderColor = defaultStyle.borderColor,
            )
        PieChart(dataSet = dataSet, style = style)
    }

    @Composable
    override fun generateCode(session: PlaygroundChartSession): GeneratedSnippet {
        val data = session.appliedData as PlaygroundDataModel.SimpleSeries
        val style = session.styleState as PieStyleState
        val rows =
            data.values.mapIndexed { index, value ->
                PieSliceInput(
                    label = data.labels?.getOrNull(index) ?: "Slice ${index + 1}",
                    valueText = formatEditorFloat(value),
                )
            }

        return generator.generate(
            PieCodegenConfig(
                rows = rows,
                title = session.title,
                style = style,
                styleProperties = pieStylePropertiesSnapshot(style, rows.size),
                codegenMode = session.codegenMode,
                functionName = deriveFunctionName(session.title, type),
            ),
        )
    }
}
