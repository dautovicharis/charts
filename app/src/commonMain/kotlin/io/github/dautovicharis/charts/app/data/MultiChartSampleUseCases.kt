package io.github.dautovicharis.charts.app.data

import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.MultiChartDataSet

interface MultiLineSampleUseCase {
    fun initialMultiLineSample(title: String, prefix: String): MultiLineSampleData
    fun multiLineSample(range: IntRange, title: String, prefix: String): MultiLineSampleData
}

interface StackedBarSampleUseCase {
    fun initialStackedBarSample(title: String, prefix: String): StackedBarSampleData
    fun stackedBarSample(range: IntRange, title: String, prefix: String): StackedBarSampleData
}

interface RadarSampleUseCase {
    fun initialRadarSample(title: String): RadarSampleData
    fun radarBasicDataSet(range: IntRange, title: String): ChartDataSet
    fun radarCustomSample(range: IntRange, title: String): RadarCustomSampleData
}
