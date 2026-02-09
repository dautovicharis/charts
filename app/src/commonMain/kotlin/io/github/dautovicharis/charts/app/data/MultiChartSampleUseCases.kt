package io.github.dautovicharis.charts.app.data

import io.github.dautovicharis.charts.model.ChartDataSet

interface MultiLineSampleUseCase {
    fun initialMultiLineSample(
        title: String,
        prefix: String,
    ): MultiLineSampleData

    fun multiLineSample(
        range: IntRange,
        title: String,
        prefix: String,
    ): MultiLineSampleData
}

interface StackedBarSampleUseCase {
    fun initialStackedBarSample(
        title: String,
        prefix: String,
    ): StackedBarSampleData

    fun stackedBarSample(
        range: IntRange,
        title: String,
        prefix: String,
    ): StackedBarSampleData
}

interface StackedAreaSampleUseCase {
    fun initialStackedAreaSample(
        title: String,
        prefix: String,
    ): StackedAreaSampleData

    fun stackedAreaSample(
        range: IntRange,
        title: String,
        prefix: String,
    ): StackedAreaSampleData
}

interface RadarSampleUseCase {
    fun initialRadarSample(title: String): RadarSampleData

    fun radarBasicDataSet(
        range: IntRange,
        title: String,
    ): ChartDataSet

    fun radarCustomSample(
        range: IntRange,
        title: String,
    ): RadarCustomSampleData
}
