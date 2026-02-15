package codegen

import codegen.area.AreaChartCodeGenerator
import codegen.bar.BarChartCodeGenerator
import codegen.line.LineChartCodeGenerator
import codegen.multiline.MultiLineChartCodeGenerator
import codegen.pie.PieChartCodeGenerator
import codegen.radar.RadarChartCodeGenerator
import codegen.stackedbar.StackedBarChartCodeGenerator
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.io.path.createTempDirectory
import model.AreaCodegenConfig
import model.BarCodegenConfig
import model.LineCodegenConfig
import model.LinePointInput
import model.MultiLineCodegenConfig
import model.MultiSeriesCodegenInput
import model.PieCodegenConfig
import model.PieSliceInput
import model.RadarCodegenConfig
import model.StackedBarCodegenConfig
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedSnippetCompilationTest {
    @Test
    fun generated_snippets_compile_for_all_chart_generators() {
        val snippets =
            listOf(
                LineChartCodeGenerator()
                    .generate(
                        LineCodegenConfig(
                            points =
                                listOf(
                                    LinePointInput(label = "Jan", valueText = "12"),
                                    LinePointInput(label = "Feb", valueText = "18"),
                                ),
                        ),
                    ).code,
                PieChartCodeGenerator()
                    .generate(
                        PieCodegenConfig(
                            rows =
                                listOf(
                                    PieSliceInput(label = "A", valueText = "24"),
                                    PieSliceInput(label = "B", valueText = "18"),
                                ),
                        ),
                    ).code,
                BarChartCodeGenerator()
                    .generate(
                        BarCodegenConfig(
                            points =
                                listOf(
                                    PieSliceInput(label = "Mon", valueText = "12"),
                                    PieSliceInput(label = "Tue", valueText = "18"),
                                ),
                        ),
                    ).code,
                MultiLineChartCodeGenerator()
                    .generate(
                        MultiLineCodegenConfig(
                            series =
                                listOf(
                                    MultiSeriesCodegenInput("Web", listOf(120f, 140f, 150f)),
                                    MultiSeriesCodegenInput("Mobile", listOf(80f, 90f, 95f)),
                                ),
                            categories = listOf("W1", "W2", "W3"),
                        ),
                    ).code,
                StackedBarChartCodeGenerator()
                    .generate(
                        StackedBarCodegenConfig(
                            series =
                                listOf(
                                    MultiSeriesCodegenInput("A", listOf(10f, 20f)),
                                    MultiSeriesCodegenInput("B", listOf(5f, 15f)),
                                ),
                            categories = listOf("Q1", "Q2"),
                        ),
                    ).code,
                AreaChartCodeGenerator()
                    .generate(
                        AreaCodegenConfig(
                            series =
                                listOf(
                                    MultiSeriesCodegenInput("Plan", listOf(60f, 80f)),
                                    MultiSeriesCodegenInput("Actual", listOf(55f, 70f)),
                                ),
                            categories = listOf("Jan", "Feb"),
                        ),
                    ).code,
                RadarChartCodeGenerator()
                    .generate(
                        RadarCodegenConfig(
                            series =
                                listOf(
                                    MultiSeriesCodegenInput("Android", listOf(80f, 75f, 70f)),
                                    MultiSeriesCodegenInput("iOS", listOf(78f, 74f, 72f)),
                                ),
                            categories = listOf("Perf", "UX", "Security"),
                        ),
                    ).code,
            )

        snippets.forEachIndexed { index, snippet ->
            assertSnippetCompiles(snippet, index)
        }
    }

    private fun assertSnippetCompiles(
        snippet: String,
        index: Int,
    ) {
        val tempDir = createTempDirectory("generated-snippet-$index").toFile()
        val sourceFile = File(tempDir, "GeneratedSnippet$index.kt")
        sourceFile.writeText(snippet)
        val outputDir = File(tempDir, "classes").apply { mkdirs() }

        val compilerOutput = ByteArrayOutputStream()
        val exitCode =
            K2JVMCompiler().exec(
                PrintStream(compilerOutput),
                "-jvm-target",
                "17",
                "-classpath",
                System.getProperty("java.class.path"),
                "-d",
                outputDir.absolutePath,
                sourceFile.absolutePath,
            )

        assertEquals(
            ExitCode.OK,
            exitCode,
            "Generated snippet failed to compile:\n$snippet\n\nCompiler output:\n${compilerOutput}",
        )
    }
}
