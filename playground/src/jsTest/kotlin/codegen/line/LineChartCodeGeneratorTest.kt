package codegen.line

import model.CodegenMode
import model.LineCodegenConfig
import model.LinePointInput
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LineChartCodeGeneratorTest {
    private val generator = LineChartCodeGenerator()

    @Test
    fun default_points_generate_snippet_without_style() {
        val snippet =
            generator.generate(
                LineCodegenConfig(
                    points =
                        listOf(
                            LinePointInput(label = "Jan", valueText = "12"),
                            LinePointInput(label = "Feb", valueText = "18"),
                        ),
                ),
            )

        assertTrue(snippet.code.contains("import io.github.dautovicharis.charts.LineChart"))
        assertTrue(snippet.code.contains("fun PlaygroundLineChartExample()"))
        assertTrue(snippet.code.contains("LineChart(dataSet = dataSet)"))
        assertFalse(snippet.code.contains("LineChartDefaults.style("))
    }

    @Test
    fun full_mode_emits_values_that_match_defaults() {
        val snippet =
            generator.generate(
                LineCodegenConfig(
                    points =
                        listOf(
                            LinePointInput(label = "A", valueText = "1"),
                            LinePointInput(label = "B", valueText = "2"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("bezier" to true, "axisVisible" to true),
                            defaults = listOf("bezier" to true, "axisVisible" to true),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("bezier = true,"))
        assertTrue(snippet.code.contains("axisVisible = true,"))
    }

    @Test
    fun minimal_mode_omits_defaults_and_keeps_changes() {
        val snippet =
            generator.generate(
                LineCodegenConfig(
                    points =
                        listOf(
                            LinePointInput(label = "A", valueText = "1"),
                            LinePointInput(label = "B", valueText = "2"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("bezier" to true, "axisVisible" to false),
                            defaults = listOf("bezier" to true, "axisVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertFalse(snippet.code.contains("bezier = true,"))
        assertTrue(snippet.code.contains("axisVisible = false,"))
    }

    @Test
    fun generator_uses_custom_function_name_when_provided() {
        val snippet =
            generator.generate(
                LineCodegenConfig(
                    points =
                        listOf(
                            LinePointInput(label = "A", valueText = "1"),
                            LinePointInput(label = "B", valueText = "2"),
                        ),
                    functionName = "MonthlyTrendLineChart",
                ),
            )

        assertTrue(snippet.code.contains("fun MonthlyTrendLineChart()"))
    }
}
