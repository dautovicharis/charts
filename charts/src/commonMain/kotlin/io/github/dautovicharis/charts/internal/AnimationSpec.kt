package io.github.dautovicharis.charts.internal

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec

internal object AnimationSpec {
    private fun duration(
        index: Int,
        duration: Int = ANIMATION_DURATION,
        offset: Int = ANIMATION_OFFSET,
    ): Int {
        return duration + offset * index
    }

    fun lineChart() =
        TweenSpec<Float>(
            durationMillis = ANIMATION_DURATION_LINE_CHART,
            delay = 0,
            easing = LinearEasing,
        )

    fun radarChart() =
        TweenSpec<Float>(
            durationMillis = ANIMATION_DURATION_LINE + 200,
            delay = 0,
            easing = FastOutSlowInEasing,
        )

    fun barChart(index: Int) =
        TweenSpec<Float>(
            durationMillis = duration(index = index),
            delay = 0,
            easing = LinearEasing,
        )

    fun stackedBar(index: Int) =
        TweenSpec<Float>(
            durationMillis =
                duration(
                    index = index,
                    duration = ANIMATION_DURATION_BAR,
                ),
            delay = 0,
            easing = LinearEasing,
        )

    fun pieChart(index: Int) =
        TweenSpec<Float>(
            durationMillis = duration(index = index),
            delay = index * ANIMATION_OFFSET,
            easing = LinearEasing,
        )

    fun pieChartValue() =
        TweenSpec<Float>(
            durationMillis = ANIMATION_DURATION_LINE,
            delay = 0,
            easing = FastOutSlowInEasing,
        )

    fun pieChartDonut() =
        TweenSpec<Float>(
            durationMillis = 900,
            delay = 0,
        )
}
