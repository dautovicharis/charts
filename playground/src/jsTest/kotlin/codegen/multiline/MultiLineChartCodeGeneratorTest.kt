package codegen.multiline

import model.CodegenMode
import model.MultiLineCodegenConfig
import model.MultiSeriesCodegenInput
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MultiLineChartCodeGeneratorTest {
    private val generator = MultiLineChartCodeGenerator()

    @Test
    fun generator_escapes_dollar_signs_in_prefix_and_labels() {
        val snippet =
            generator.generate(
                MultiLineCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Revenue $", listOf(120f, 140f))),
                    categories = listOf("Q$1", "$" + "{value}"),
                    title = "Growth $",
                ),
            )
        val escapedTemplate = "\\$" + "{value}"

        assertTrue(snippet.code.contains("prefix = \"\\$\","))
        assertTrue(snippet.code.contains("\"Revenue \\$\" to listOf(120f, 140f)"))
        assertTrue(snippet.code.contains("categories = listOf(\"Q\\$1\", \"$escapedTemplate\")"))
        assertTrue(snippet.code.contains("title = \"Growth \\$\","))
    }

    @Test
    fun minimal_mode_omits_defaults_and_keeps_changes() {
        val snippet =
            generator.generate(
                MultiLineCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Web", listOf(120f, 140f, 150f))),
                    categories = listOf("W1", "W2", "W3"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("bezier" to true, "pointVisible" to false),
                            defaults = listOf("bezier" to true, "pointVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertFalse(snippet.code.contains("bezier = true,"))
        assertTrue(snippet.code.contains("pointVisible = false,"))
    }

    @Test
    fun full_mode_emits_values_that_match_defaults() {
        val snippet =
            generator.generate(
                MultiLineCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Web", listOf(120f, 140f, 150f))),
                    categories = listOf("W1", "W2", "W3"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("bezier" to true, "lineAlpha" to 1f),
                            defaults = listOf("bezier" to true, "lineAlpha" to 1f),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("bezier = true,"))
        assertTrue(snippet.code.contains("lineAlpha = 1f,"))
    }

}
