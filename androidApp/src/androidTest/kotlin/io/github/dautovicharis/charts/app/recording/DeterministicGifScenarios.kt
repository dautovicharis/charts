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
import io.github.dautovicharis.charts.app.fixtures.ChartTestStyleFixtures
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
            demoName = "pie_custom",
            introFrames = 65,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.PIE,
            renderChart = { chartViewStyle ->
                PieChart(
                    dataSet = pieSampleUseCase.initialPieCustomSample().dataSet,
                    style =
                        ChartTestStyleFixtures.pieCustomStyle(
                            chartViewStyle = chartViewStyle,
                            segmentCount = 6,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                listOf(
                    Tap(xFraction = 0.52f, yFraction = 0.34f, framesAfter = frames),
                    Tap(xFraction = 0.70f, yFraction = 0.52f, framesAfter = frames),
                    Tap(xFraction = 0.30f, yFraction = 0.58f, framesAfter = frames),
                )
            },
        ),
        DemoScenario(
            // Backward-compatible alias for existing docs/tooling.
            demoName = "pie_donut",
            introFrames = 65,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.PIE,
            renderChart = { chartViewStyle ->
                PieChart(
                    dataSet = pieSampleUseCase.initialPieCustomSample().dataSet,
                    style =
                        ChartTestStyleFixtures.pieCustomStyle(
                            chartViewStyle = chartViewStyle,
                            segmentCount = 6,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                listOf(
                    Tap(xFraction = 0.52f, yFraction = 0.34f, framesAfter = frames),
                    Tap(xFraction = 0.70f, yFraction = 0.52f, framesAfter = frames),
                    Tap(xFraction = 0.30f, yFraction = 0.58f, framesAfter = frames),
                )
            },
        ),
        DemoScenario(
            demoName = "line_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE,
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
            demoName = "line_custom",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE,
            renderChart = { chartViewStyle ->
                LineChart(
                    dataSet = lineSampleUseCase.initialLineDataSet(),
                    style = ChartTestStyleFixtures.lineCustomStyle(chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.54f)
            },
        ),
        DemoScenario(
            demoName = "multi_line_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE,
            renderChart = { chartViewStyle ->
                LineChart(
                    dataSet = multiLineSampleUseCase.initialMultiLineSample().dataSet,
                    style = LineChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.60f)
            },
        ),
        DemoScenario(
            demoName = "multi_line_custom",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.LINE,
            renderChart = { chartViewStyle ->
                LineChart(
                    dataSet = multiLineSampleUseCase.initialMultiLineSample().dataSet,
                    style =
                        ChartTestStyleFixtures.multiLineCustomStyle(
                            chartViewStyle = chartViewStyle,
                            seriesCount = 3,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.56f)
            },
        ),
        DemoScenario(
            demoName = "bar_default",
            introFrames = 55,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.BAR,
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
            demoName = "bar_custom",
            introFrames = 55,
            interactionFrames = 14,
            interactionNodeTag = ChartSemanticsTags.BAR,
            renderChart = { chartViewStyle ->
                BarChart(
                    dataSet = barSampleUseCase.initialBarDataSet(),
                    style = ChartTestStyleFixtures.barCustomStyle(chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                listOf(
                    Tap(xFraction = 0.12f, yFraction = 0.24f, framesAfter = frames),
                    Tap(xFraction = 0.46f, yFraction = 0.78f, framesAfter = frames),
                    Tap(xFraction = 0.86f, yFraction = 0.40f, framesAfter = frames),
                )
            },
        ),
        DemoScenario(
            demoName = "stacked_bar_default",
            introFrames = 70,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_BAR,
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
            demoName = "stacked_bar_custom",
            introFrames = 70,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_BAR,
            renderChart = { chartViewStyle ->
                StackedBarChart(
                    dataSet = stackedBarSampleUseCase.initialStackedBarSample().dataSet,
                    style =
                        ChartTestStyleFixtures.stackedBarCustomStyle(
                            chartViewStyle = chartViewStyle,
                            segmentCount = 4,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.60f, withPause = false)
            },
        ),
        DemoScenario(
            demoName = "stacked_area_default",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_AREA,
            renderChart = { chartViewStyle ->
                StackedAreaChart(
                    dataSet = stackedAreaSampleUseCase.initialStackedAreaSample().dataSet,
                    style = StackedAreaChartDefaults.style(chartViewStyle = chartViewStyle),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.64f)
            },
        ),
        DemoScenario(
            demoName = "stacked_area_custom",
            introFrames = 90,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.STACKED_AREA,
            renderChart = { chartViewStyle ->
                StackedAreaChart(
                    dataSet = stackedAreaSampleUseCase.initialStackedAreaSample().dataSet,
                    style =
                        ChartTestStyleFixtures.stackedAreaCustomStyle(
                            chartViewStyle = chartViewStyle,
                            seriesCount = 3,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                horizontalDragSequence(frames, yFraction = 0.58f)
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
        DemoScenario(
            demoName = "radar_custom",
            introFrames = 80,
            interactionFrames = 12,
            interactionNodeTag = ChartSemanticsTags.RADAR,
            renderChart = { chartViewStyle ->
                val sample = radarSampleUseCase.initialRadarSample()
                RadarChart(
                    dataSet = sample.customDataSet,
                    style =
                        ChartTestStyleFixtures.radarCustomStyle(
                            chartViewStyle = chartViewStyle,
                            seriesKeys = sample.seriesKeys,
                        ),
                    animateOnStart = true,
                )
            },
            interactionSteps = { frames ->
                radarDragSequence(frames)
            },
        ),
    ).associateBy { it.demoName }
