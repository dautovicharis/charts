package codegen.radar

import model.CodegenMode
import model.MultiSeriesCodegenInput
import model.RadarCodegenConfig
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RadarChartCodeGeneratorTest {
    private val generator = RadarChartCodeGenerator()

    @Test
    fun default_series_generate_snippet_without_style() {
        val snippet =
            generator.generate(
                RadarCodegenConfig(
                    series =
                        listOf(
                            MultiSeriesCodegenInput("Android", listOf(80f, 75f, 70f)),
                            MultiSeriesCodegenInput("iOS", listOf(78f, 74f, 72f)),
                        ),
                    categories = listOf("Perf", "UX", "Security"),
                ),
            )

        assertTrue(snippet.code.contains("import io.github.dautovicharis.charts.RadarChart"))
        assertTrue(snippet.code.contains("fun PlaygroundRadarChartExample()"))
        assertTrue(snippet.code.contains("RadarChart(dataSet = dataSet)"))
        assertFalse(snippet.code.contains("RadarChartDefaults.style("))
    }

    @Test
    fun minimal_mode_omits_defaults_and_keeps_changes() {
        val snippet =
            generator.generate(
                RadarCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Android", listOf(80f, 75f, 70f))),
                    categories = listOf("Perf", "UX", "Security"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("lineWidth" to 2f, "gridVisible" to false),
                            defaults = listOf("lineWidth" to 2f, "gridVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertFalse(snippet.code.contains("lineWidth = 2f,"))
        assertTrue(snippet.code.contains("gridVisible = false,"))
    }

    @Test
    fun full_mode_emits_values_that_match_defaults() {
        val snippet =
            generator.generate(
                RadarCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Android", listOf(80f, 75f, 70f))),
                    categories = listOf("Perf", "UX", "Security"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("pointVisible" to true, "fillAlpha" to 0.3f),
                            defaults = listOf("pointVisible" to true, "fillAlpha" to 0.3f),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("pointVisible = true,"))
        assertTrue(snippet.code.contains("fillAlpha = 0.3f,"))
    }
}
