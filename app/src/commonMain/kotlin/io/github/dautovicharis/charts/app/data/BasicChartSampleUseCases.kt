package io.github.dautovicharis.charts.app.data

import io.github.dautovicharis.charts.model.ChartDataSet

interface PieSampleUseCase {
    fun initialPieSample(
        title: String,
        postfix: String,
    ): PieSampleData

    fun pieSample(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
        postfix: String,
    ): PieSampleData

    fun pieCustomSample(
        range: IntRange,
        title: String,
        postfix: String,
    ): PieSampleData
}

interface LineSampleUseCase {
    fun initialLineDataSet(title: String): ChartDataSet

    fun lineDataSet(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
    ): ChartDataSet
}

interface BarSampleUseCase {
    fun initialBarDataSet(title: String): ChartDataSet

    fun barDataSet(
        range: IntRange,
        numOfPoints: IntRange,
        title: String,
    ): ChartDataSet
}
