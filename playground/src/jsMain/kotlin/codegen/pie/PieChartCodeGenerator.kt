package codegen.pie

import codegen.common.BaseChartCodeGenerator
import codegen.common.buildChartImports
import codegen.common.resolveStyleArguments
import model.ChartCodeGenerator
import model.GeneratedSnippet
import model.PieCodegenConfig
import model.PieSliceInput

class PieChartCodeGenerator : BaseChartCodeGenerator<PieCodegenConfig>(), ChartCodeGenerator<PieCodegenConfig> {
    override fun generate(config: PieCodegenConfig): GeneratedSnippet {
        val items = normalizeRows(config.rows)
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

    private fun normalizeRows(rows: List<PieSliceInput>): List<NormalizedItem> =
        rows.mapIndexed { index, row ->
            val sanitizedLabel = row.label.trim().ifBlank { "Slice ${index + 1}" }
            val floatValue = row.valueText.toFloatOrNull() ?: 0f
            NormalizedItem(
                label = sanitizedLabel,
                value = floatValue,
            )
        }

    private companion object {
        val BASE_IMPORTS =
            listOf(
                "import androidx.compose.runtime.Composable",
                "import io.github.dautovicharis.charts.PieChart",
                "import io.github.dautovicharis.charts.model.toChartDataSet",
            )
        const val STYLE_IMPORT = "import io.github.dautovicharis.charts.style.PieChartDefaults"
        const val COMPONENT_NAME = "PieChart"
        const val STYLE_BUILDER = "PieChartDefaults.style"
    }
}
