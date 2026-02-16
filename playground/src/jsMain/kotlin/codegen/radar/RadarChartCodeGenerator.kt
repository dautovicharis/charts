package codegen.radar

import codegen.common.BaseChartCodeGenerator
import codegen.common.MultiSeriesItem
import codegen.common.buildChartImports
import codegen.common.buildMultiChartDataSetCode
import codegen.common.resolveStyleArguments
import model.ChartCodeGenerator
import model.GeneratedSnippet
import model.RadarCodegenConfig
import kotlin.math.max

class RadarChartCodeGenerator :
    BaseChartCodeGenerator<RadarCodegenConfig>(),
    ChartCodeGenerator<RadarCodegenConfig> {
    override fun generate(config: RadarCodegenConfig): GeneratedSnippet {
        val normalized = normalizeSeries(config)
        val styleArguments = resolveStyleArguments(config.styleProperties, config.codegenMode)
        val includeStyle = styleArguments.isNotEmpty()
        val imports =
            buildChartImports(
                baseImports = BASE_IMPORTS,
                styleImport = if (includeStyle) STYLE_IMPORT else null,
                styleArguments = styleArguments,
            )

        val bodyLines = mutableListOf<String>()
        bodyLines +=
            buildMultiChartDataSetCode(
                items = normalized.series,
                title = config.title,
                categories = normalized.categories,
            )
        bodyLines += ""

        if (includeStyle) {
            bodyLines += buildStyleCode(STYLE_BUILDER, styleArguments)
            bodyLines += ""
        }

        bodyLines += buildChartCallCode(COMPONENT_NAME, includeStyle)
        val code = buildFunctionCode(imports, config.functionName, bodyLines)
        return GeneratedSnippet(code = code)
    }

    private fun normalizeSeries(config: RadarCodegenConfig): NormalizedMultiSeries {
        val initialCategories =
            config.categories.mapIndexed { index, label ->
                label.trim().ifBlank { "Axis ${index + 1}" }
            }
        val targetSize = max(initialCategories.size, config.series.maxOfOrNull { it.values.size } ?: 0)
        val categories =
            List(targetSize) { index ->
                initialCategories.getOrNull(index) ?: "Axis ${index + 1}"
            }

        val series =
            config.series.mapIndexed { index, item ->
                val label = item.label.trim().ifBlank { "Series ${index + 1}" }
                val values =
                    List(targetSize) { valueIndex ->
                        item.values.getOrElse(valueIndex) { 0f }.coerceAtLeast(0f)
                    }
                MultiSeriesItem(label = label, values = values)
            }

        return NormalizedMultiSeries(categories = categories, series = series)
    }

    private data class NormalizedMultiSeries(
        val categories: List<String>,
        val series: List<MultiSeriesItem>,
    )

    private companion object {
        val BASE_IMPORTS =
            listOf(
                "import androidx.compose.runtime.Composable",
                "import io.github.dautovicharis.charts.RadarChart",
                "import io.github.dautovicharis.charts.model.toMultiChartDataSet",
            )
        const val STYLE_IMPORT = "import io.github.dautovicharis.charts.style.RadarChartDefaults"
        const val COMPONENT_NAME = "RadarChart"
        const val STYLE_BUILDER = "RadarChartDefaults.style"
    }
}
