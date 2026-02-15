package model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import codegen.area.AreaChartCodeGenerator
import codegen.area.areaStylePropertiesSnapshot
import codegen.bar.BarChartCodeGenerator
import codegen.bar.barStylePropertiesSnapshot
import codegen.line.LineChartCodeGenerator
import codegen.line.lineStylePropertiesSnapshot
import codegen.multiline.MultiLineChartCodeGenerator
import codegen.multiline.multiLineStylePropertiesSnapshot
import codegen.pie.PieChartCodeGenerator
import codegen.pie.pieStylePropertiesSnapshot
import codegen.radar.RadarChartCodeGenerator
import codegen.radar.radarStylePropertiesSnapshot
import codegen.stackedbar.StackedBarChartCodeGenerator
import codegen.stackedbar.stackedBarStylePropertiesSnapshot
import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import kotlin.math.max
import kotlin.random.Random

private const val LABEL_COLUMN_ID = "label"

val playgroundChartRegistry: PlaygroundChartRegistry =
    PlaygroundChartRegistry(
        charts =
            listOf(
                LineChartDefinition,
                BarChartDefinition,
                PieChartDefinition,
                RadarChartDefinition,
                AreaChartDefinition,
                MultiLineChartDefinition,
                StackedBarChartDefinition,
            ),
        primaryChartTypes =
            listOf(
                ChartType.LINE,
                ChartType.BAR,
                ChartType.PIE,
                ChartType.RADAR,
                ChartType.AREA,
            ),
        overflowChartTypes =
            listOf(
                ChartType.MULTI_LINE,
                ChartType.STACKED_BAR,
            ),
    )

private object PieChartDefinition : PlaygroundChartDefinition {
    private val generator = PieChartCodeGenerator()

    override val type: ChartType = ChartType.PIE
    override val displayName: String = type.displayName
    override val defaultTitle: String = PIE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.SimpleSeries(
            values = listOf(24f, 18f, 36f, 22f),
            labels = listOf("Product A", "Product B", "Product C", "Product D"),
        )

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

private object LineChartDefinition : PlaygroundChartDefinition {
    private val generator = LineChartCodeGenerator()

    override val type: ChartType = ChartType.LINE
    override val displayName: String = type.displayName
    override val defaultTitle: String = LINE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.SimpleSeries(
            values = listOf(12f, 18f, 15f, 22f, 28f, 24f),
            labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
        )

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

private object BarChartDefinition : PlaygroundChartDefinition {
    private val generator = BarChartCodeGenerator()

    override val type: ChartType = ChartType.BAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = BAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.SimpleSeries(
            values = listOf(45f, -12f, 38f, 27f, -19f, 42f, 31f),
            labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        )

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

private object MultiLineChartDefinition : PlaygroundChartDefinition {
    private val generator = MultiLineChartCodeGenerator()

    override val type: ChartType = ChartType.MULTI_LINE
    override val displayName: String = type.displayName
    override val defaultTitle: String = MULTI_LINE_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.MultiSeries(
            series =
                listOf(
                    PlaygroundDataModel.MultiSeries.Series("Web Store", listOf(420f, 510f, 480f, 530f, 560f, 590f)),
                    PlaygroundDataModel.MultiSeries.Series("Mobile App", listOf(390f, 460f, 430f, 480f, 505f, 525f)),
                    PlaygroundDataModel.MultiSeries.Series("Partner Sales", listOf(320f, 350f, 340f, 360f, 380f, 410f)),
                ),
            xLabels = listOf("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6"),
        )

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

private object StackedBarChartDefinition : PlaygroundChartDefinition {
    private val generator = StackedBarChartCodeGenerator()

    override val type: ChartType = ChartType.STACKED_BAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = STACKED_BAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.StackedSeries(
            segmentNames = listOf("North America", "Europe", "Asia Pacific"),
            bars =
                listOf(
                    PlaygroundDataModel.StackedSeries.StackedBar("Q1", listOf(152f, 98f, 70f)),
                    PlaygroundDataModel.StackedSeries.StackedBar("Q2", listOf(165f, 105f, 75f)),
                    PlaygroundDataModel.StackedSeries.StackedBar("Q3", listOf(172f, 112f, 81f)),
                    PlaygroundDataModel.StackedSeries.StackedBar("Q4", listOf(187f, 125f, 92f)),
                ),
            labels = listOf("Q1", "Q2", "Q3", "Q4"),
        )

    override fun defaultStyleState(): PlaygroundStyleState = StackedBarStyleState()

    override fun createEditorState(model: PlaygroundDataModel): DataEditorState {
        val data = model as? PlaygroundDataModel.StackedSeries ?: defaultDataModel() as PlaygroundDataModel.StackedSeries
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
            ) ?: return invalidNumericResult

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
            dataModel = PlaygroundDataModel.StackedSeries(segmentNames = segmentNames, bars = bars, labels = parsed.labels),
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

private object AreaChartDefinition : PlaygroundChartDefinition {
    private val generator = AreaChartCodeGenerator()

    override val type: ChartType = ChartType.AREA
    override val displayName: String = type.displayName
    override val defaultTitle: String = AREA_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.MultiSeries(
            series =
                listOf(
                    PlaygroundDataModel.MultiSeries.Series("Free Plan", listOf(620f, 650f, 690f, 720f, 760f, 800f)),
                    PlaygroundDataModel.MultiSeries.Series("Standard Plan", listOf(280f, 300f, 320f, 345f, 370f, 395f)),
                    PlaygroundDataModel.MultiSeries.Series("Premium Plan", listOf(90f, 98f, 110f, 120f, 130f, 145f)),
                ),
            xLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
        )

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

private object RadarChartDefinition : PlaygroundChartDefinition {
    private val generator = RadarChartCodeGenerator()

    override val type: ChartType = ChartType.RADAR
    override val displayName: String = type.displayName
    override val defaultTitle: String = RADAR_CHART_TITLE

    override fun defaultDataModel(): PlaygroundDataModel =
        PlaygroundDataModel.RadarSeries(
            entries =
                listOf(
                    PlaygroundDataModel.RadarSeries.RadarEntry("Android App", listOf(84f, 79f, 76f, 88f, 82f, 74f)),
                    PlaygroundDataModel.RadarSeries.RadarEntry("iOS App", listOf(80f, 77f, 74f, 84f, 78f, 73f)),
                    PlaygroundDataModel.RadarSeries.RadarEntry("Web App", listOf(75f, 72f, 70f, 82f, 76f, 69f)),
                ),
            axes = listOf("Performance", "Reliability", "Usability", "Security", "Scalability", "Observability"),
        )

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

private fun createSimpleSeriesEditor(
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

private fun validateSimpleSeries(
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
        ) ?: return invalidNumericResult

    val valueColumn = parsed.numericColumns.firstOrNull() ?: return invalidNumericResult
    val values = parsed.valuesByColumn.getValue(valueColumn.id)

    return PlaygroundValidationResult(
        sanitizedEditor = editorState.copy(rows = parsed.sanitizedRows),
        dataModel = PlaygroundDataModel.SimpleSeries(values = values, labels = parsed.labels),
        message = "Applied ${parsed.labels.size} rows.",
    )
}

private data class ParsedEditorTable(
    val labels: List<String>,
    val numericColumns: List<DataEditorColumn>,
    val valuesByColumn: Map<String, List<Float>>,
    val sanitizedRows: List<DataEditorRow>,
)

private fun parseEditorTable(
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
        val label = row.cells[labelColumn.id].orEmpty().trim().ifBlank { "$labelPrefix ${index + 1}" }
        labels += label

        numericColumns.forEach { column ->
            val parsedValue = row.cells[column.id].orEmpty().trim().toFloatOrNull() ?: return null
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

private val invalidNumericResult =
    PlaygroundValidationResult(
        sanitizedEditor = null,
        dataModel = null,
        message = "Please enter valid numeric values in all rows.",
    )

private fun defaultRowCells(
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

private fun randomizeEditorValues(
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

private fun normalizeColorCount(
    colors: List<Color>,
    targetCount: Int,
): List<Color> {
    if (targetCount <= 0 || colors.isEmpty()) return emptyList()
    return List(targetCount) { index -> colors[index % colors.size] }
}
