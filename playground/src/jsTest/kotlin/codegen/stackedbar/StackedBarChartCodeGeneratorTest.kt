package codegen.stackedbar

import model.CodegenMode
import model.MultiSeriesCodegenInput
import model.StackedBarCodegenConfig
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StackedBarChartCodeGeneratorTest {
    private val generator = StackedBarChartCodeGenerator()

    @Test
    fun generator_escapes_dollar_signs_in_prefix_and_labels() {
        val snippet =
            generator.generate(
                StackedBarCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("$" + "{segment}", listOf(10f, 20f))),
                    categories = listOf("Q$2"),
                    title = "Revenue $",
                ),
            )
        val escapedTemplate = "\\$" + "{segment}"

        assertTrue(snippet.code.contains("prefix = \"\\$\","), snippet.code)
        assertTrue(snippet.code.contains("\"$escapedTemplate\" to listOf(10f, 20f)"), snippet.code)
        assertTrue(snippet.code.contains("\"Q\\$2\""), snippet.code)
        assertTrue(snippet.code.contains("title = \"Revenue \\$\","), snippet.code)
    }

    @Test
    fun minimal_mode_omits_defaults_and_keeps_changes() {
        val snippet =
            generator.generate(
                StackedBarCodegenConfig(
                    series =
                        listOf(
                            MultiSeriesCodegenInput("A", listOf(10f, 20f)),
                            MultiSeriesCodegenInput("B", listOf(5f, 15f)),
                        ),
                    categories = listOf("Q1", "Q2"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("barAlpha" to 0.8f, "selectionLineVisible" to false),
                            defaults = listOf("barAlpha" to 0.8f, "selectionLineVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertFalse(snippet.code.contains("barAlpha = 0.8f,"))
        assertTrue(snippet.code.contains("selectionLineVisible = false,"))
    }

    @Test
    fun full_mode_emits_values_that_match_defaults() {
        val snippet =
            generator.generate(
                StackedBarCodegenConfig(
                    series =
                        listOf(
                            MultiSeriesCodegenInput("A", listOf(10f, 20f)),
                            MultiSeriesCodegenInput("B", listOf(5f, 15f)),
                        ),
                    categories = listOf("Q1", "Q2"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("barAlpha" to 0.8f, "selectionLineWidth" to 2f),
                            defaults = listOf("barAlpha" to 0.8f, "selectionLineWidth" to 2f),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("barAlpha = 0.8f,"))
        assertTrue(snippet.code.contains("selectionLineWidth = 2f,"))
    }
}
