package io.github.dautovicharis.charts.app.recording

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.dautovicharis.charts.app.ui.theme.AppTheme
import io.github.dautovicharis.charts.app.ui.theme.docsSlate
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.ChartViewStyle
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DeterministicGifFrameCaptureTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private data class CaptureRequest(
        val scenario: DemoScenario,
        val outputDir: File,
        val frameStepMs: Long,
        val introFrames: Int,
        val interactionFrames: Int,
    )

    @Test
    fun captureDemoFrames() {
        val request = resolveCaptureRequest()

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            DeterministicChartScene(
                chartContent = request.scenario.renderChart,
            )
        }
        composeRule.waitForIdle()

        regenerateChart()

        val captureNode = requireNodeWithTag(CHART_CAPTURE_TEST_TAG)
        val interactionNode = requireNodeWithTag(request.scenario.interactionNodeTag)

        var frameIndex =
            captureFrames(
                chartNode = captureNode,
                outputDir = request.outputDir,
                startIndex = 1,
                frameCount = request.introFrames,
                frameStepMs = request.frameStepMs,
            )

        if (request.interactionFrames > 0) {
            frameIndex =
                runInteractionSteps(
                    captureNode = captureNode,
                    interactionNode = interactionNode,
                    outputDir = request.outputDir,
                    startIndex = frameIndex,
                    frameStepMs = request.frameStepMs,
                    steps = request.scenario.interactionSteps(request.interactionFrames),
                )
        }

        writeMetadata(
            outputDir = request.outputDir,
            demoName = request.scenario.demoName,
            frameStepMs = request.frameStepMs,
            introFrames = request.introFrames,
            interactionFrames = request.interactionFrames,
            totalFrames = frameIndex - 1,
        )
    }

    private fun resolveCaptureRequest(): CaptureRequest {
        val args = InstrumentationRegistry.getArguments()
        val requestedDemo = args.getString("demo_name") ?: DEFAULT_DEMO_NAME
        val scenario =
            SCENARIOS[requestedDemo]
                ?: error(
                    "Unsupported demo '$requestedDemo'. Supported demos: ${SCENARIOS.keys.sorted().joinToString()}",
                )
        val outputSubdir = args.getString("output_subdir") ?: "deterministic-gif/${scenario.demoName}"
        val frameStepMs =
            args.getString("frame_step_ms")?.toLongOrNull()?.coerceAtLeast(1L)
                ?: DEFAULT_FRAME_STEP_MS
        val introFrames =
            args.getString("intro_frames")?.toIntOrNull()?.coerceAtLeast(0)
                ?: scenario.introFrames
        val interactionFrames =
            (
                args.getString("interaction_frames")?.toIntOrNull()
                    ?: args.getString("tap_frames")?.toIntOrNull()
                    ?: scenario.interactionFrames
            ).coerceAtLeast(0)

        return CaptureRequest(
            scenario = scenario,
            outputDir = prepareOutputDirectory(outputSubdir),
            frameStepMs = frameStepMs,
            introFrames = introFrames,
            interactionFrames = interactionFrames,
        )
    }

    private fun regenerateChart() {
        val regenerateButton = composeRule.onNodeWithText(REGENERATE_LABEL)
        regenerateButton.fetchSemanticsNode()
        regenerateButton.performClick()
        composeRule.waitForIdle()
    }

    private fun requireNodeWithTag(tag: String): SemanticsNodeInteraction {
        val node = composeRule.onNodeWithTag(tag, useUnmergedTree = true)
        node.fetchSemanticsNode()
        return node
    }

    private fun prepareOutputDirectory(subdir: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val baseDir = context.filesDir
        val outputDir = File(baseDir, subdir)
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        check(outputDir.mkdirs()) {
            "Could not create output directory: $outputDir"
        }
        return outputDir
    }

    private fun runInteractionSteps(
        captureNode: SemanticsNodeInteraction,
        interactionNode: SemanticsNodeInteraction,
        outputDir: File,
        startIndex: Int,
        frameStepMs: Long,
        steps: List<InteractionStep>,
    ): Int {
        var frameIndex = startIndex
        var pointerActive = false
        try {
            for (step in steps) {
                frameIndex =
                    when (step) {
                        is Pause ->
                            captureFrames(
                                chartNode = captureNode,
                                outputDir = outputDir,
                                startIndex = frameIndex,
                                frameCount = step.frames,
                                frameStepMs = frameStepMs,
                            )

                        is Tap -> {
                            val target = fractionToOffset(interactionNode, step.xFraction, step.yFraction)
                            interactionNode.performTouchInput {
                                click(target)
                            }
                            composeRule.waitForIdle()
                            captureFrames(
                                chartNode = captureNode,
                                outputDir = outputDir,
                                startIndex = frameIndex,
                                frameCount = step.framesAfter,
                                frameStepMs = frameStepMs,
                            )
                        }

                        is DragPath -> {
                            if (step.points.size < 2) {
                                frameIndex
                            } else {
                                val start = step.points.first()
                                val startOffset = fractionToOffset(interactionNode, start.x, start.y)
                                interactionNode.performTouchInput {
                                    down(startOffset)
                                }
                                pointerActive = true
                                composeRule.waitForIdle()
                                frameIndex =
                                    captureFrames(
                                        chartNode = captureNode,
                                        outputDir = outputDir,
                                        startIndex = frameIndex,
                                        frameCount = step.holdStartFrames,
                                        frameStepMs = frameStepMs,
                                    )
                                var previousOffset = startOffset
                                for (point in step.points.drop(1)) {
                                    val waypointOffset =
                                        fractionToOffset(
                                            interactionNode = interactionNode,
                                            xFraction = point.x,
                                            yFraction = point.y,
                                        )
                                    val waypointFrames = step.framesPerWaypoint
                                    if (waypointFrames <= 0) {
                                        interactionNode.performTouchInput {
                                            moveTo(waypointOffset)
                                        }
                                        composeRule.waitForIdle()
                                    } else {
                                        repeat(waypointFrames) { frame ->
                                            val progress = (frame + 1).toFloat() / waypointFrames.toFloat()
                                            val interpolatedOffset =
                                                interpolateOffset(
                                                    start = previousOffset,
                                                    end = waypointOffset,
                                                    progress = progress,
                                                )
                                            interactionNode.performTouchInput {
                                                moveTo(interpolatedOffset)
                                            }
                                            composeRule.waitForIdle()
                                            frameIndex =
                                                captureFrames(
                                                    chartNode = captureNode,
                                                    outputDir = outputDir,
                                                    startIndex = frameIndex,
                                                    frameCount = 1,
                                                    frameStepMs = frameStepMs,
                                                )
                                        }
                                    }
                                    previousOffset = waypointOffset
                                }
                                interactionNode.performTouchInput { up() }
                                pointerActive = false
                                composeRule.waitForIdle()
                                captureFrames(
                                    chartNode = captureNode,
                                    outputDir = outputDir,
                                    startIndex = frameIndex,
                                    frameCount = step.releaseFrames,
                                    frameStepMs = frameStepMs,
                                )
                            }
                        }
                    }
            }
        } finally {
            if (pointerActive) {
                runCatching {
                    interactionNode.performTouchInput {
                        cancel()
                    }
                }
                composeRule.waitForIdle()
            }
        }
        return frameIndex
    }

    private fun interpolateOffset(
        start: Offset,
        end: Offset,
        progress: Float,
    ): Offset {
        val clampedProgress = progress.coerceIn(0f, 1f)
        return Offset(
            x = start.x + ((end.x - start.x) * clampedProgress),
            y = start.y + ((end.y - start.y) * clampedProgress),
        )
    }

    private fun fractionToOffset(
        interactionNode: SemanticsNodeInteraction,
        xFraction: Float,
        yFraction: Float,
    ): Offset {
        val bounds = interactionNode.fetchSemanticsNode().boundsInRoot
        return Offset(
            x = bounds.width * xFraction.coerceIn(0f, 1f),
            y = bounds.height * yFraction.coerceIn(0f, 1f),
        )
    }

    private fun captureFrames(
        chartNode: SemanticsNodeInteraction,
        outputDir: File,
        startIndex: Int,
        frameCount: Int,
        frameStepMs: Long,
    ): Int {
        var frameIndex = startIndex
        if (frameCount <= 0) return frameIndex
        repeat(frameCount) {
            composeRule.mainClock.advanceTimeBy(frameStepMs)
            composeRule.waitForIdle()
            saveChartFrame(chartNode, outputDir, frameIndex)
            frameIndex += 1
        }
        return frameIndex
    }

    private fun saveChartFrame(
        chartNode: SemanticsNodeInteraction,
        outputDir: File,
        frameIndex: Int,
    ) {
        val bitmap = chartNode.captureToImage().asAndroidBitmap()
        val file = File(outputDir, String.format(Locale.US, "frame-%04d.png", frameIndex))
        FileOutputStream(file).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                "Failed to save frame: $file"
            }
        }
    }

    private fun writeMetadata(
        outputDir: File,
        demoName: String,
        frameStepMs: Long,
        introFrames: Int,
        interactionFrames: Int,
        totalFrames: Int,
    ) {
        val metadata =
            """
            demo=$demoName
            frame_step_ms=$frameStepMs
            intro_frames=$introFrames
            interaction_frames=$interactionFrames
            total_frames=$totalFrames
            """.trimIndent()
        File(outputDir, "metadata.txt").writeText(metadata)
    }
}

@Composable
private fun DeterministicChartScene(chartContent: @Composable (ChartViewStyle) -> Unit) {
    var refreshKey by remember { mutableIntStateOf(0) }
    AppTheme(theme = docsSlate, darkTheme = false, useDynamicColors = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                key(refreshKey) {
                    Box(modifier = Modifier.testTag(CHART_CAPTURE_TEST_TAG)) {
                        chartContent(deterministicChartViewStyle())
                    }
                }
                TextButton(onClick = { refreshKey += 1 }) {
                    Text(text = REGENERATE_LABEL)
                }
            }
        }
    }
}

@Composable
private fun deterministicChartViewStyle(): ChartViewStyle {
    return ChartViewDefaults.style(
        width = 360.dp,
        outerPadding = 12.dp,
        innerPadding = 12.dp,
        cornerRadius = 20.dp,
        shadow = 10.dp,
    )
}
