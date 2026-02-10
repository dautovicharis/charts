package io.github.dautovicharis.charts.internal

internal const val NO_SELECTION = -1
internal const val ANIMATION_TARGET = 1.0f

internal const val DEFAULT_SCALE = 1f
internal const val MAX_SCALE = 1.05f

// Animation duration
internal const val ANIMATION_DURATION = 200
internal const val ANIMATION_DURATION_BAR = 500
internal const val ANIMATION_DURATION_LINE = 1200
internal const val ANIMATION_DURATION_LINE_CHART = 1700

// Animation duration offset
internal const val ANIMATION_OFFSET = 50

// Donut chart
internal const val DONUT_MIN_PERCENTAGE = 0f
internal const val DONUT_MAX_PERCENTAGE = 70f

internal object TestTags {
    const val CHART_ERROR = "ChartError"
    const val CHART_TITLE = "ChartTitle"
    const val PIE_CHART = "PieChart"
    const val BAR_CHART = "BarChart"
    const val BAR_CHART_ZOOM_OUT = "BarChartZoomOut"
    const val BAR_CHART_ZOOM_IN = "BarChartZoomIn"
    const val BAR_CHART_X_AXIS_LABELS = "BarChartXAxisLabels"
    const val BAR_CHART_Y_AXIS_LABELS = "BarChartYAxisLabels"
    const val STACKED_BAR_CHART = "StackedBarChart"
    const val STACKED_AREA_CHART = "StackedAreaChart"
    const val LINE_CHART = "LineChart"
    const val RADAR_CHART = "RadarChart"
}
