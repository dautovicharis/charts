package codegen.common

import model.GeneratedSnippet

/**
 * Base class for chart code generators that share common structure.
 * Handles indentation, imports, and standard code generation patterns.
 */
abstract class BaseChartCodeGenerator<TConfig> {
    abstract fun generate(config: TConfig): GeneratedSnippet

    // Indentation levels
    protected val indentStatement = 1 // Top-level statements in function body
    protected val indentBuilder = 2 // Inside builder/method calls
    protected val indentParam = 3 // Parameters and arguments
    protected val indentNested = 4 // Nested parameters (e.g., items in nested listOf)

    // Common method names
    protected val methodToChartDataSet = "toChartDataSet"
    protected val methodListOf = "listOf"

    // Common variable names
    protected val varDataSet = "dataSet"
    protected val varStyle = "style"

    // Common parameter names
    protected val paramTitle = "title"
    protected val paramLabels = "labels"

    /**
     * Builds the complete function code with imports, annotation, and body.
     */
    protected fun buildFunctionCode(
        imports: List<String>,
        functionName: String,
        bodyLines: List<String>,
    ): String =
        buildString {
            append(imports.joinToString("\n"))
            append("\n\n")
            append("@Composable\n")
            append("fun $functionName() {\n")
            append(bodyLines.joinToString("\n"))
            append("\n}")
        }

    /**
     * Generates data set builder code (values + toChartDataSet with title and labels).
     */
    protected fun buildDataSetCode(
        items: List<NormalizedItem>,
        title: String,
    ): List<String> {
        val lines = mutableListOf<String>()
        val valuesCode =
            items.joinToString(", ") { item ->
                formatKotlinFloatLiteral(item.value)
            }
        val labelsCode =
            items.joinToString(", ") { item ->
                "\"${escapeKotlinString(item.label)}\""
            }

        lines += kotlinLine(indentStatement, "val $varDataSet =")
        lines += kotlinLine(indentBuilder, "$methodListOf($valuesCode).$methodToChartDataSet(")
        lines += kotlinLine(indentParam, "$paramTitle = \"${escapeKotlinString(title)}\",")
        lines += kotlinLine(indentParam, "$paramLabels = $methodListOf($labelsCode),")
        lines += kotlinLine(indentBuilder, ")")

        return lines
    }

    /**
     * Generates style builder code if style is not default.
     */
    protected fun buildStyleCode(
        styleBuilder: String,
        styleArguments: List<StyleArgument>,
    ): List<String> {
        val lines = mutableListOf<String>()

        lines += kotlinLine(indentStatement, "val $varStyle =")
        lines += kotlinLine(indentBuilder, "$styleBuilder(")
        styleArguments.forEach { argument ->
            lines += kotlinLine(indentParam, argument.code)
        }
        lines += kotlinLine(indentBuilder, ")")

        return lines
    }

    /**
     * Generates chart component call code.
     */
    protected fun buildChartCallCode(
        componentName: String,
        includeStyle: Boolean,
    ): List<String> {
        val lines = mutableListOf<String>()

        if (includeStyle) {
            lines += kotlinLine(indentStatement, "$componentName(")
            lines += kotlinLine(indentBuilder, "$varDataSet = $varDataSet,")
            lines += kotlinLine(indentBuilder, "$varStyle = $varStyle,")
            lines += kotlinLine(indentStatement, ")")
        } else {
            lines += kotlinLine(indentStatement, "$componentName($varDataSet = $varDataSet)")
        }

        return lines
    }

    /**
     * Common normalized item structure used by all chart types.
     */
    protected data class NormalizedItem(
        val label: String,
        val value: Float,
    )
}

// Type alias for better readability in generators
typealias StyleArgument = RenderedStyleArgument
