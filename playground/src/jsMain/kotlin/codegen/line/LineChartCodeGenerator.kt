package codegen.line

import codegen.common.BaseChartCodeGenerator
import codegen.common.buildChartImports
import codegen.common.resolveStyleArguments
import model.ChartCodeGenerator
import model.GeneratedSnippet
import model.LineCodegenConfig
import model.LinePointInput

class LineChartCodeGenerator : BaseChartCodeGenerator<LineCodegenConfig>(), ChartCodeGenerator<LineCodegenConfig> {
    override fun generate(config: LineCodegenConfig): GeneratedSnippet {
        val items = normalizePoints(config.points)
        val styleArguments = resolveStyleArguments(config.styleProperties, config.codegenMode)
        val includeStyle = styleArguments.isNotEmpty()
        val imports =
            buildChartImports(
                baseImports = BASE_IMPORTS,
                styleImport = if (includeStyle) STYLE_IMPORT else null,
                styleArguments = styleArguments,
            )
        val bodyLines = mutableListOf<String>()

        bodyLines += buildDataSetCode(items, config.title)
        bodyLines += ""

        if (includeStyle) {
            bodyLines += buildStyleCode(STYLE_BUILDER, styleArguments)
            bodyLines += ""
        }

        bodyLines += buildChartCallCode(COMPONENT_NAME, includeStyle)

        val code = buildFunctionCode(imports, config.functionName, bodyLines)
        return GeneratedSnippet(code = code)
    }

    private fun normalizePoints(points: List<LinePointInput>): List<NormalizedItem> =
        points.mapIndexed { index, point ->
            val sanitizedLabel = point.label.trim().ifBlank { "Point ${index + 1}" }
            val floatValue = point.valueText.toFloatOrNull() ?: 0f
            NormalizedItem(
                label = sanitizedLabel,
                value = floatValue,
            )
        }

    private companion object {
        val BASE_IMPORTS =
            listOf(
                "import androidx.compose.runtime.Composable",
                "import io.github.dautovicharis.charts.LineChart",
                "import io.github.dautovicharis.charts.model.toChartDataSet",
            )
        const val STYLE_IMPORT = "import io.github.dautovicharis.charts.style.LineChartDefaults"
        const val COMPONENT_NAME = "LineChart"
        const val STYLE_BUILDER = "LineChartDefaults.style"
    }
}
