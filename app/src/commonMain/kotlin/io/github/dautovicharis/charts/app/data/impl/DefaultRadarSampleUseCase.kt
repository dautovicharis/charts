package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.data.RadarCustomSampleData
import io.github.dautovicharis.charts.app.data.RadarSampleData
import io.github.dautovicharis.charts.app.data.RadarSampleUseCase
import io.github.dautovicharis.charts.model.ChartDataSet
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.model.toMultiChartDataSet

class DefaultRadarSampleUseCase : RadarSampleUseCase {
    private val radarCategories =
        listOf(
            "Speed",
            "Strength",
            "Agility",
            "Stamina",
            "Skill",
            "Luck",
        )
    private val radarInitialItems =
        listOf(
            "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
            "Tiger" to listOf(65f, 88f, 60f, 82f, 55f, 68f),
            "Octane" to listOf(92f, 58f, 76f, 62f, 86f, 60f),
        )
    private val radarBasicValues = listOf(74, 60, 82, 55, 69, 88)

    override fun initialRadarSample(title: String): RadarSampleData {
        val basicDataSet =
            radarBasicValues.toChartDataSet(
                title = title,
                labels = radarCategories,
            )
        val customDataSet =
            radarInitialItems.toMultiChartDataSet(
                title = title,
                categories = radarCategories,
            )
        return RadarSampleData(
            basicDataSet = basicDataSet,
            customDataSet = customDataSet,
            seriesKeys = radarInitialItems.map { it.first },
        )
    }

    override fun radarBasicDataSet(
        range: IntRange,
        title: String,
    ): ChartDataSet {
        val values = radarCategories.map { range.random() }
        return values.toChartDataSet(
            title = title,
            labels = radarCategories,
        )
    }

    override fun radarCustomSample(
        range: IntRange,
        title: String,
    ): RadarCustomSampleData {
        val newItems =
            radarInitialItems.map { (name, values) ->
                name to values.map { range.random().toFloat() }
            }
        val dataSet =
            newItems.toMultiChartDataSet(
                title = title,
                categories = radarCategories,
            )
        return RadarCustomSampleData(
            dataSet = dataSet,
            seriesKeys = newItems.map { it.first },
        )
    }
}
