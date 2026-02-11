package io.github.dautovicharis.charts.app.recording

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.ChartViewStyle

internal const val DEFAULT_DEMO_NAME = "pie_custom"
internal const val DEFAULT_FRAME_STEP_MS = 20L
internal const val REGENERATE_LABEL = "Regenerate chart"
internal const val CHART_CAPTURE_TEST_TAG = "DeterministicChartCapture"

internal object ChartSemanticsTags {
    const val PIE = "PieChart"
    const val BAR = "BarChart"
    const val LINE = "LineChart"
    const val STACKED_BAR = "StackedBarChart"
    const val STACKED_AREA = "StackedAreaChart"
    const val RADAR = "RadarChart"
}

internal data class DemoScenario(
    val demoName: String,
    val introFrames: Int,
    val interactionFrames: Int,
    val interactionNodeTag: String,
    val renderChart: @Composable (ChartViewStyle) -> Unit,
    val interactionSteps: (Int) -> List<InteractionStep>,
)

internal data class FractionPoint(
    val x: Float,
    val y: Float,
)

internal sealed interface InteractionStep

internal data class Pause(
    val frames: Int,
) : InteractionStep

internal data class Tap(
    val xFraction: Float,
    val yFraction: Float,
    val framesAfter: Int,
) : InteractionStep

internal data class DragPath(
    val points: List<FractionPoint>,
    val holdStartFrames: Int,
    val framesPerWaypoint: Int,
    val releaseFrames: Int,
) : InteractionStep

private const val DRAG_FRAMES_MULTIPLIER = 3

internal fun horizontalDragSequence(
    frames: Int,
    yFraction: Float,
    withPause: Boolean = true,
): List<InteractionStep> {
    val dragFrames = (frames * DRAG_FRAMES_MULTIPLIER).coerceAtLeast(1)
    val drag =
        DragPath(
            points =
                listOf(
                    FractionPoint(0.10f, yFraction),
                    FractionPoint(0.32f, yFraction - 0.08f),
                    FractionPoint(0.56f, yFraction + 0.04f),
                    FractionPoint(0.82f, yFraction - 0.12f),
                ),
            holdStartFrames = dragFrames,
            framesPerWaypoint = dragFrames,
            releaseFrames = dragFrames,
        )
    return if (withPause) {
        listOf(Pause(frames = (frames / 2).coerceAtLeast(1)), drag)
    } else {
        listOf(drag)
    }
}

internal fun radarDragSequence(frames: Int): List<InteractionStep> {
    val dragFrames = (frames * DRAG_FRAMES_MULTIPLIER).coerceAtLeast(1)
    return listOf(
        Pause(frames = (frames / 2).coerceAtLeast(1)),
        DragPath(
            points =
                listOf(
                    FractionPoint(0.50f, 0.14f),
                    FractionPoint(0.78f, 0.30f),
                    FractionPoint(0.86f, 0.58f),
                    FractionPoint(0.62f, 0.82f),
                    FractionPoint(0.30f, 0.74f),
                    FractionPoint(0.16f, 0.46f),
                    FractionPoint(0.38f, 0.22f),
                ),
            holdStartFrames = dragFrames,
            framesPerWaypoint = dragFrames,
            releaseFrames = dragFrames,
        ),
    )
}
