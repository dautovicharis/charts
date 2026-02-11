package io.github.dautovicharis.charts.app.recording

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeterministicGifScenariosCoverageTest {
    @Test
    fun includesDefaultAndCustomVariantsForAllDemoCharts() {
        val expectedScenarios =
            setOf(
                "pie_default",
                "pie_custom",
                "line_default",
                "line_custom",
                "multi_line_default",
                "multi_line_custom",
                "bar_default",
                "bar_custom",
                "stacked_bar_default",
                "stacked_bar_custom",
                "stacked_area_default",
                "stacked_area_custom",
                "radar_default",
                "radar_custom",
            )

        val actualScenarios = SCENARIOS.keys
        val missing = expectedScenarios - actualScenarios
        assertTrue(
            "Missing deterministic GIF scenarios: ${missing.sorted()} (actual: ${actualScenarios.sorted()})",
            missing.isEmpty(),
        )
    }
}
