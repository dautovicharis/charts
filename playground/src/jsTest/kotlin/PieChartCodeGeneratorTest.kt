import androidx.compose.ui.graphics.Color
import codegen.pie.PieChartCodeGenerator
import model.CodegenMode
import model.PieCodegenConfig
import model.PieSliceInput
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PieChartCodeGeneratorTest {
    private val generator = PieChartCodeGenerator()

    @Test
    fun default_rows_generate_snippet_without_style_block() {
        val snippet =
            generator.generate(
                PieCodegenConfig(
                    rows =
                        listOf(
                            PieSliceInput(label = "Product A", valueText = "24"),
                            PieSliceInput(label = "Product B", valueText = "18"),
                        ),
                ),
            )

        assertTrue(snippet.code.contains("import androidx.compose.runtime.Composable"))
        assertTrue(snippet.code.contains("import io.github.dautovicharis.charts.PieChart"))
        assertTrue(snippet.code.contains("fun PlaygroundPieChartExample()"))
        assertTrue(snippet.code.contains("PieChart(dataSet = dataSet)"))
        assertFalse(snippet.code.contains("PieChartDefaults.style("))
    }

    @Test
    fun minimal_mode_emits_only_non_default_arguments() {
        val snippet =
            generator.generate(
                PieCodegenConfig(
                    rows =
                        listOf(
                            PieSliceInput(label = "A", valueText = "1"),
                            PieSliceInput(label = "B", valueText = "2"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("borderWidth" to 3f, "legendVisible" to false),
                            defaults = listOf("borderWidth" to 3f, "legendVisible" to true),
                        ),
                    codegenMode = CodegenMode.MINIMAL,
                ),
            )

        assertTrue(snippet.code.contains("legendVisible = false,"))
        assertFalse(snippet.code.contains("borderWidth = 3f,"))
    }

    @Test
    fun full_mode_emits_all_current_arguments_even_if_default() {
        val snippet =
            generator.generate(
                PieCodegenConfig(
                    rows =
                        listOf(
                            PieSliceInput(label = "A", valueText = "1"),
                            PieSliceInput(label = "B", valueText = "2"),
                            PieSliceInput(label = "C", valueText = "3"),
                        ),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current =
                                listOf(
                                    "borderWidth" to 3f,
                                    "pieColors" to listOf(Color(0xFF1D3557), Color(0xFF457B9D), Color(0xFFA8DADC)),
                                ),
                            defaults =
                                listOf(
                                    "borderWidth" to 3f,
                                    "pieColors" to listOf(Color(0xFF1D3557), Color(0xFF457B9D), Color(0xFFA8DADC)),
                                ),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("borderWidth = 3f,"))
        assertTrue(
            snippet.code.contains("pieColors = listOf(Color(0xFF1D3557), Color(0xFF457B9D), Color(0xFFA8DADC)),"),
        )
        assertTrue(snippet.code.contains("import androidx.compose.ui.graphics.Color"))
    }

    @Test
    fun function_name_can_be_overridden() {
        val snippet =
            generator.generate(
                PieCodegenConfig(
                    rows =
                        listOf(
                            PieSliceInput(label = "A", valueText = "1"),
                            PieSliceInput(label = "B", valueText = "2"),
                        ),
                    functionName = "RevenuePieChartSample",
                ),
            )

        assertTrue(snippet.code.contains("fun RevenuePieChartSample()"))
    }
}
