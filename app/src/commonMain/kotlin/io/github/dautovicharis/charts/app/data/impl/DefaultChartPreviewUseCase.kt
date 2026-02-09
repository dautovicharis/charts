package io.github.dautovicharis.charts.app.data.impl

import io.github.dautovicharis.charts.app.ChartGalleryPreviewState
import io.github.dautovicharis.charts.app.data.ChartPreviewUseCase
import kotlin.random.Random

class DefaultChartPreviewUseCase : ChartPreviewUseCase {
    private val previewPieValues = listOf(35f, 20f, 15f, 30f)
    private val previewLineValues = listOf(12f, 18f, 9f, 24f, 16f)
    private val previewMultiLineSeries =
        listOf(
            "Cherry St." to listOf(10f, 14f, 8f, 18f, 12f),
            "Strawberry Mall" to listOf(6f, 12f, 10f, 14f, 9f),
            "Lime Av." to listOf(9f, 7f, 12f, 9f, 15f),
        )
    private val previewStackedAreaSeries =
        listOf(
            "Series A" to listOf(14f, 18f, 16f, 20f, 22f),
            "Series B" to listOf(9f, 11f, 10f, 12f, 13f),
            "Series C" to listOf(6f, 8f, 7f, 9f, 10f),
        )
    private val previewBarValues = listOf(18f, 32f, 26f, 48f, 36f, 28f, 54f)
    private val previewStackedSeries =
        listOf(
            "Q1" to listOf(20f, 14f, 10f),
            "Q2" to listOf(14f, 18f, 12f),
            "Q3" to listOf(16f, 12f, 18f),
        )
    private val previewRadarSeries =
        listOf(
            "Falcon" to listOf(78f, 62f, 90f, 55f, 70f, 80f),
        )

    override fun previewSeed(): ChartGalleryPreviewState {
        return ChartGalleryPreviewState(
            pieValues = previewPieValues,
            lineValues = previewLineValues,
            multiLineSeries = previewMultiLineSeries,
            stackedAreaSeries = previewStackedAreaSeries,
            barValues = previewBarValues,
            stackedSeries = previewStackedSeries,
            radarSeries = previewRadarSeries,
        )
    }

    override fun nextPiePreview(values: List<Float>): List<Float> {
        return values.map { value ->
            jitter(value, from = -6, until = 6, min = 8f, max = 55f)
        }
    }

    override fun nextLinePreview(values: List<Float>): List<Float> {
        return values.map { value ->
            jitter(value, from = -6, until = 6, min = 6f, max = 28f)
        }
    }

    override fun nextBarPreview(values: List<Float>): List<Float> {
        return values.map { value ->
            jitter(value, from = -8, until = 9, min = 0f, max = 100f)
        }
    }

    override fun nextMultiLinePreview(): List<Pair<String, List<Float>>> {
        return previewMultiLineSeries.map { (label, values) ->
            label to
                values.map { value ->
                    jitter(value, from = -5, until = 6, min = 6f, max = 22f)
                }
        }
    }

    override fun nextStackedAreaPreview(): List<Pair<String, List<Float>>> {
        return previewStackedAreaSeries.map { (label, values) ->
            label to
                values.map { value ->
                    jitter(value, from = -5, until = 6, min = 4f, max = 28f)
                }
        }
    }

    override fun nextStackedPreview(): List<Pair<String, List<Float>>> {
        return previewStackedSeries.map { (label, values) ->
            label to
                values.map { value ->
                    jitter(value, from = -6, until = 6, min = 6f, max = 28f)
                }
        }
    }

    override fun nextRadarPreview(): List<Pair<String, List<Float>>> {
        return previewRadarSeries.map { (label, values) ->
            label to
                values.map { value ->
                    jitter(value, from = -18, until = 18, min = 30f, max = 100f)
                }
        }
    }

    private fun jitter(
        value: Float,
        from: Int,
        until: Int,
        min: Float,
        max: Float,
    ): Float {
        val delta = Random.nextInt(from, until)
        return (value + delta).coerceIn(min, max)
    }
}
