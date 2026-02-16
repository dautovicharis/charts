package model

import androidx.compose.ui.graphics.Color

const val PIE_CHART_TITLE = "Revenue Breakdown"
const val LINE_CHART_TITLE = "Monthly Trend"
const val MULTI_LINE_CHART_TITLE = "Revenue By Channel"
const val BAR_CHART_TITLE = "Weekly Performance"
const val STACKED_BAR_CHART_TITLE = "Quarterly Revenue Mix"
const val AREA_CHART_TITLE = "Plan Distribution"
const val RADAR_CHART_TITLE = "Platform Capability"

data class PieSliceInput(
    val label: String,
    val valueText: String,
)

typealias LinePointInput = PieSliceInput
typealias BarPointInput = PieSliceInput

sealed interface PlaygroundStyleState

data class PieStyleState(
    val donutPercentage: Float? = null,
    val borderWidth: Float? = null,
    val pieAlpha: Float? = null,
    val legendVisible: Boolean? = null,
    val pieColors: List<Color>? = null,
) : PlaygroundStyleState

data class LineStyleState(
    val lineColor: Color? = null,
    val lineAlpha: Float? = null,
    val bezier: Boolean? = null,
    val pointColor: Color? = null,
    val pointVisible: Boolean? = null,
    val pointSize: Float? = null,
    val dragPointColor: Color? = null,
    val dragPointVisible: Boolean? = null,
    val dragPointSize: Float? = null,
    val dragActivePointSize: Float? = null,
    val axisVisible: Boolean? = null,
    val axisLineWidth: Float? = null,
    val xAxisLabelsVisible: Boolean? = null,
    val yAxisLabelsVisible: Boolean? = null,
    val zoomControlsVisible: Boolean? = null,
) : PlaygroundStyleState

data class MultiLineStyleState(
    val lineColors: List<Color>? = null,
    val lineAlpha: Float? = null,
    val bezier: Boolean? = null,
    val pointVisible: Boolean? = null,
    val dragPointVisible: Boolean? = null,
    val pointColor: Color? = null,
    val dragPointColor: Color? = null,
) : PlaygroundStyleState

data class BarStyleState(
    val barColor: Color? = null,
    val barAlpha: Float? = null,
    val gridVisible: Boolean? = null,
    val axisVisible: Boolean? = null,
    val selectionLineVisible: Boolean? = null,
    val selectionLineWidth: Float? = null,
    val zoomControlsVisible: Boolean? = null,
) : PlaygroundStyleState

data class StackedBarStyleState(
    val barColors: List<Color>? = null,
    val barAlpha: Float? = null,
    val selectionLineVisible: Boolean? = null,
    val selectionLineWidth: Float? = null,
    val zoomControlsVisible: Boolean? = null,
) : PlaygroundStyleState

data class AreaStyleState(
    val areaColors: List<Color>? = null,
    val lineColors: List<Color>? = null,
    val fillAlpha: Float? = null,
    val lineVisible: Boolean? = null,
    val lineWidth: Float? = null,
    val bezier: Boolean? = null,
    val zoomControlsVisible: Boolean? = null,
) : PlaygroundStyleState

data class RadarStyleState(
    val lineColors: List<Color>? = null,
    val lineWidth: Float? = null,
    val pointVisible: Boolean? = null,
    val pointSize: Float? = null,
    val fillVisible: Boolean? = null,
    val fillAlpha: Float? = null,
    val gridVisible: Boolean? = null,
    val categoryLegendVisible: Boolean? = null,
) : PlaygroundStyleState

data class MultiSeriesCodegenInput(
    val label: String,
    val values: List<Float>,
)

data class PieCodegenConfig(
    val rows: List<PieSliceInput>,
    val title: String = PIE_CHART_TITLE,
    val style: PieStyleState = PieStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundPieChartExample",
)

data class LineCodegenConfig(
    val points: List<LinePointInput>,
    val title: String = LINE_CHART_TITLE,
    val style: LineStyleState = LineStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundLineChartExample",
)

data class BarCodegenConfig(
    val points: List<BarPointInput>,
    val title: String = BAR_CHART_TITLE,
    val style: BarStyleState = BarStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundBarChartExample",
)

data class MultiLineCodegenConfig(
    val series: List<MultiSeriesCodegenInput>,
    val categories: List<String>,
    val title: String = MULTI_LINE_CHART_TITLE,
    val style: MultiLineStyleState = MultiLineStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundMultiLineChartExample",
)

data class StackedBarCodegenConfig(
    val series: List<MultiSeriesCodegenInput>,
    val categories: List<String>,
    val title: String = STACKED_BAR_CHART_TITLE,
    val style: StackedBarStyleState = StackedBarStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundStackedBarChartExample",
)

data class AreaCodegenConfig(
    val series: List<MultiSeriesCodegenInput>,
    val categories: List<String>,
    val title: String = AREA_CHART_TITLE,
    val style: AreaStyleState = AreaStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundAreaChartExample",
)

data class RadarCodegenConfig(
    val series: List<MultiSeriesCodegenInput>,
    val categories: List<String>,
    val title: String = RADAR_CHART_TITLE,
    val style: RadarStyleState = RadarStyleState(),
    val styleProperties: StylePropertiesSnapshot? = null,
    val codegenMode: CodegenMode = CodegenMode.MINIMAL,
    val functionName: String = "PlaygroundRadarChartExample",
)
