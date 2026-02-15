package codegen.bar

import model.BarCodegenConfig
import model.CodegenMode
import model.PieSliceInput
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BarChartCodeGeneratorTest {
    private val generator = BarChartCodeGenerator()

    @Test
    fun default_points_generate_snippet_without_style() {
        val snippet =
            generator.generate(
                BarCodegenConfig(
                    points =
                        listOf(
                            PieSliceInput(label = "Mon", valueText = "12"),
                            PieSliceInput(label = "Tue", valueText = "18"),
                        ),
                ),
            )

        assertTrue(snippet.code.contains("import io.github.dautovicharis.charts.BarChart"))
        assertTrue(snippet.code.contains("fun PlaygroundBarChartExample()"))
        assertTrue(snippet.code.contains("BarChart(dataSet = dataSet)"))
        assertFalse(snippet.code.contains("BarChartDefaults.style("))
    }

    @Test
    fun minimal_mode_omits_defaults_and_keeps_changes() {
        val snippet =
            generator.generate(
                BarCodegenConfig(
                    points =
                        listOf(
                            PieSliceInput(label = "A", valueText = "1"),
                            PieSliceInput(label = "B", valueText = "2"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("barAlpha" to 0.8f, "gridVisible" to false),
                            defaults = listOf("barAlpha" to 0.8f, "gridVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertFalse(snippet.code.contains("barAlpha = 0.8f,"))
        assertTrue(snippet.code.contains("gridVisible = false,"))
    }

    @Test
    fun full_mode_emits_values_that_match_defaults() {
        val snippet =
            generator.generate(
                BarCodegenConfig(
                    points =
                        listOf(
                            PieSliceInput(label = "A", valueText = "1"),
                            PieSliceInput(label = "B", valueText = "2"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("axisVisible" to true, "selectionLineWidth" to 1.5f),
                            defaults = listOf("axisVisible" to true, "selectionLineWidth" to 1.5f),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("axisVisible = true,"))
        assertTrue(snippet.code.contains("selectionLineWidth = 1.5f,"))
    }

}
