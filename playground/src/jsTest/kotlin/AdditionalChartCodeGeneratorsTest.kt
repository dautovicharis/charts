import codegen.area.AreaChartCodeGenerator
import codegen.bar.BarChartCodeGenerator
import codegen.multiline.MultiLineChartCodeGenerator
import codegen.radar.RadarChartCodeGenerator
import codegen.stackedbar.StackedBarChartCodeGenerator
import model.AreaCodegenConfig
import model.BarCodegenConfig
import model.CodegenMode
import model.MultiLineCodegenConfig
import model.MultiSeriesCodegenInput
import model.PieSliceInput
import model.RadarCodegenConfig
import model.StackedBarCodegenConfig
import model.StylePropertiesSnapshot
import kotlin.test.Test
import kotlin.test.assertTrue

class AdditionalChartCodeGeneratorsTest {
    @Test
    fun multiline_generator_uses_direct_series_data() {
        val generator = MultiLineChartCodeGenerator()
        val snippet =
            generator.generate(
                MultiLineCodegenConfig(
                    series =
                        listOf(
                            MultiSeriesCodegenInput("Web", listOf(120f, 140f, 150f)),
                            MultiSeriesCodegenInput("Mobile", listOf(80f, 90f, 95f)),
                        ),
                    categories = listOf("W1", "W2", "W3"),
                ),
            )

        assertTrue(snippet.code.contains("\"Web\" to listOf(120f, 140f, 150f)"))
        assertTrue(snippet.code.contains("\"Mobile\" to listOf(80f, 90f, 95f)"))
        assertTrue(snippet.code.contains("categories = listOf(\"W1\", \"W2\", \"W3\")"))
    }

    @Test
    fun stacked_bar_generator_uses_direct_segment_data() {
        val generator = StackedBarChartCodeGenerator()
        val snippet =
            generator.generate(
                StackedBarCodegenConfig(
                    series =
                        listOf(
                            MultiSeriesCodegenInput("A", listOf(10f, 20f)),
                            MultiSeriesCodegenInput("B", listOf(5f, 15f)),
                        ),
                    categories = listOf("Q1", "Q2"),
                ),
            )

        assertTrue(snippet.code.contains("\"A\" to listOf(10f, 20f)"))
        assertTrue(snippet.code.contains("\"B\" to listOf(5f, 15f)"))
        assertTrue(snippet.code.contains("categories = listOf(\"Q1\", \"Q2\")"))
    }

    @Test
    fun area_generator_supports_full_mode_style_emission() {
        val generator = AreaChartCodeGenerator()
        val snippet =
            generator.generate(
                AreaCodegenConfig(
                    series = listOf(MultiSeriesCodegenInput("Free", listOf(60f, 80f))),
                    categories = listOf("Jan", "Feb"),
                    styleProperties =
                        StylePropertiesSnapshot(
                            current = listOf("fillAlpha" to 0.4f),
                            defaults = listOf("fillAlpha" to 0.4f),
                        ),
                    codegenMode = CodegenMode.FULL,
                ),
            )

        assertTrue(snippet.code.contains("fillAlpha = 0.4f,"))
    }

    @Test
    fun radar_generator_supports_direct_series_data() {
        val generator = RadarChartCodeGenerator()
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

        assertTrue(snippet.code.contains("\"Android\" to listOf(80f, 75f, 70f)"))
        assertTrue(snippet.code.contains("\"iOS\" to listOf(78f, 74f, 72f)"))
        assertTrue(snippet.code.contains("categories = listOf(\"Perf\", \"UX\", \"Security\")"))
    }

    @Test
    fun bar_generator_supports_dynamic_function_name() {
        val generator = BarChartCodeGenerator()
        val snippet =
            generator.generate(
                BarCodegenConfig(
                    points =
                        listOf(
                            PieSliceInput("A", "10"),
                            PieSliceInput("B", "20"),
                        ),
                    functionName = "QuarterlyBarChart",
                ),
            )

        assertTrue(snippet.code.contains("fun QuarterlyBarChart()"))
    }
}
