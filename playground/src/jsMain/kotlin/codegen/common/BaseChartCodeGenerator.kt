package codegen.common

import model.GeneratedSnippet
import model.StylePropertiesSnapshot

/**
 * Base class for chart code generators that share common structure.
 * Handles indentation, imports, and standard code generation patterns.
 */
abstract class BaseChartCodeGenerator<TConfig> {
    
    abstract fun generate(config: TConfig): GeneratedSnippet
    
    // Indentation levels
    protected val INDENT_STATEMENT = 1    // Top-level statements in function body
    protected val INDENT_BUILDER = 2      // Inside builder/method calls
    protected val INDENT_PARAM = 3        // Parameters and arguments
    protected val INDENT_NESTED = 4       // Nested parameters (e.g., items in nested listOf)
    
    // Common method names
    protected val METHOD_TO_CHART_DATA_SET = "toChartDataSet"
    protected val METHOD_LIST_OF = "listOf"
    
    // Common variable names
    protected val VAR_DATA_SET = "dataSet"
    protected val VAR_STYLE = "style"
    
    // Common parameter names
    protected val PARAM_TITLE = "title"
    protected val PARAM_LABELS = "labels"
    
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
        
        lines += kotlinLine(INDENT_STATEMENT, "val $VAR_DATA_SET =")
        lines += kotlinLine(INDENT_BUILDER, "$METHOD_LIST_OF($valuesCode).$METHOD_TO_CHART_DATA_SET(")
        lines += kotlinLine(INDENT_PARAM, "$PARAM_TITLE = \"${escapeKotlinString(title)}\",")
        lines += kotlinLine(INDENT_PARAM, "$PARAM_LABELS = $METHOD_LIST_OF($labelsCode),")
        lines += kotlinLine(INDENT_BUILDER, ")")
        
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
        
        lines += kotlinLine(INDENT_STATEMENT, "val $VAR_STYLE =")
        lines += kotlinLine(INDENT_BUILDER, "$styleBuilder(")
        styleArguments.forEach { argument ->
            lines += kotlinLine(INDENT_PARAM, argument.code)
        }
        lines += kotlinLine(INDENT_BUILDER, ")")
        
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
            lines += kotlinLine(INDENT_STATEMENT, "$componentName(")
            lines += kotlinLine(INDENT_BUILDER, "$VAR_DATA_SET = $VAR_DATA_SET,")
            lines += kotlinLine(INDENT_BUILDER, "$VAR_STYLE = $VAR_STYLE,")
            lines += kotlinLine(INDENT_STATEMENT, ")")
        } else {
            lines += kotlinLine(INDENT_STATEMENT, "$componentName($VAR_DATA_SET = $VAR_DATA_SET)")
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
