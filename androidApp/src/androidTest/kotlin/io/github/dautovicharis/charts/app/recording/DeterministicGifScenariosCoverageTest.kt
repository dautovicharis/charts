package io.github.dautovicharis.charts.app.recording

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeterministicGifScenariosCoverageTest {
    @Test
    fun includesDefaultVariantsForAllDemoCharts() {
        val expectedScenarios =
            setOf(
                "pie_default",
                "line_default",
                "multi_line_default",
                "bar_default",
                "stacked_bar_default",
                "stacked_area_default",
                "radar_default",
            )

        val actualScenarios = SCENARIOS.keys
        val missing = expectedScenarios - actualScenarios
        val unexpected = actualScenarios - expectedScenarios
        assertTrue(
            "Deterministic GIF scenarios mismatch. Missing: ${missing.sorted()}, unexpected: ${unexpected.sorted()}, actual: ${actualScenarios.sorted()}",
            missing.isEmpty() && unexpected.isEmpty(),
        )
    }
}
