package io.github.dautovicharis.charts.app.recording

import io.github.dautovicharis.charts.BarChart
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.RadarChart
import io.github.dautovicharis.charts.StackedAreaChart
import io.github.dautovicharis.charts.StackedBarChart
import io.github.dautovicharis.charts.app.data.impl.DefaultBarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultMultiLineSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultPieSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultRadarSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedAreaSampleUseCase
import io.github.dautovicharis.charts.app.data.impl.DefaultStackedBarSampleUseCase
import io.github.dautovicharis.charts.style.BarChartDefaults
import io.github.dautovicharis.charts.style.LineChartDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults
import io.github.dautovicharis.charts.style.RadarChartDefaults
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import io.github.dautovicharis.charts.style.StackedBarChartDefaults

private val pieSampleUseCase = DefaultPieSampleUseCase()
private val lineSampleUseCase = DefaultLineSampleUseCase()
private val multiLineSampleUseCase = DefaultMultiLineSampleUseCase()
private val barSampleUseCase = DefaultBarSampleUseCase()
private val stackedBarSampleUseCase = DefaultStackedBarSampleUseCase()
private val stackedAreaSampleUseCase = DefaultStackedAreaSampleUseCase()
private val radarSampleUseCase = DefaultRadarSampleUseCase()

internal val SCENARIOS: Map<String, DemoScenario> =
    listOf(
        DemoScenario(
            demoName = "pie_default",
            introFrames = 65,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.PIE,
            renderChart = { chartViewStyle ->
                PieChart(
                    dataSet = pieSampleUseCase.initialPieSample().dataSet,
                    style = PieChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                listOf(
                    Tap(xFraction = 0.50f, yFraction = 0.38f, framesAfter = frames),
                    Tap(xFraction = 0.66f, yFraction = 0.48f, framesAfter = frames),
                    Tap(xFraction = 0.36f, yFraction = 0.50f, framesAfter = frames),
                )
            },
        ),
        DemoScenario(
            demoName = "line_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE_PLOT,
            renderChart = { chartViewStyle ->
                LineChart(
                    dataSet = lineSampleUseCase.initialLineDataSet(),
                    style = LineChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.62f)
            },
        ),
        DemoScenario(
            demoName = "multi_line_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE_PLOT,
            renderChart = { chartViewStyle ->
                LineChart(
                    dataSet = multiLineSampleUseCase.initialMultiLineSample().dataSet,
                    style = LineChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                val pauseFrames = frames.coerceAtLeast(1)
                val dragFrames = (frames * 2).coerceAtLeast(1)
                val tapFrames = (frames * 2).coerceAtLeast(1)
                listOf(
                    Pause(frames = pauseFrames),
                    DragPath(
                        points =
                            listOf(
                                FractionPoint(0.14f, 0.60f),
                                FractionPoint(0.38f, 0.60f),
                                FractionPoint(0.62f, 0.60f),
                                FractionPoint(0.84f, 0.60f),
                            ),
                        holdStartFrames = dragFrames,
                        framesPerWaypoint = dragFrames,
                        releaseFrames = dragFrames,
                    ),
                    Tap(xFraction = 0.24f, yFraction = 0.60f, framesAfter = tapFrames),
                    Tap(xFraction = 0.52f, yFraction = 0.58f, framesAfter = tapFrames),
                    Tap(xFraction = 0.80f, yFraction = 0.56f, framesAfter = tapFrames),
                )
            },
        ),
        DemoScenario(
            demoName = "bar_default",
            introFrames = 55,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.BAR_PLOT,
            renderChart = { chartViewStyle ->
                BarChart(
                    dataSet = barSampleUseCase.initialBarDataSet(),
                    style = BarChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                listOf(
                    Tap(xFraction = 0.18f, yFraction = 0.55f, framesAfter = frames),
                    Tap(xFraction = 0.52f, yFraction = 0.30f, framesAfter = frames),
                    Tap(xFraction = 0.84f, yFraction = 0.58f, framesAfter = frames),
                )
            },
        ),
        DemoScenario(
            demoName = "stacked_bar_default",
            introFrames = 70,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_BAR_PLOT,
            renderChart = { chartViewStyle ->
                StackedBarChart(
                    dataSet = stackedBarSampleUseCase.initialStackedBarSample().dataSet,
                    style = StackedBarChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.58f, withPause = false)
            },
        ),
        DemoScenario(
            demoName = "stacked_area_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_AREA_PLOT,
            renderChart = { chartViewStyle ->
                StackedAreaChart(
                    dataSet = stackedAreaSampleUseCase.initialStackedAreaSample().dataSet,
                    style = StackedAreaChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                val pauseFrames = frames.coerceAtLeast(1)
                val dragFrames = (frames * 2).coerceAtLeast(1)
                val tapFrames = (frames * 2).coerceAtLeast(1)
                val finalTapFrames = (frames * 3).coerceAtLeast(1)
                listOf(
                    Pause(frames = pauseFrames),
                    DragPath(
                        points =
                            listOf(
                                FractionPoint(0.16f, 0.58f),
                                FractionPoint(0.40f, 0.58f),
                                FractionPoint(0.62f, 0.58f),
                                FractionPoint(0.84f, 0.58f),
                            ),
                        holdStartFrames = dragFrames,
                        framesPerWaypoint = dragFrames,
                        releaseFrames = dragFrames,
                    ),
                    Tap(xFraction = 0.22f, yFraction = 0.60f, framesAfter = tapFrames),
                    Tap(xFraction = 0.52f, yFraction = 0.56f, framesAfter = tapFrames),
                    Tap(xFraction = 0.80f, yFraction = 0.52f, framesAfter = finalTapFrames),
                )
            },
        ),
        DemoScenario(
            demoName = "radar_default",
            introFrames = 80,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.RADAR,
            renderChart = { chartViewStyle ->
                RadarChart(
                    dataSet = radarSampleUseCase.initialRadarDefaultDataSet(),
                    style = RadarChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                radarDragSequence(frames)
            },
        ),
    ).associateBy { it.demoName }
