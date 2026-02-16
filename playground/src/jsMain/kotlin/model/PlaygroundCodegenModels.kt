package model

enum class CodegenMode {
    MINIMAL,
    FULL,
}

data class GeneratedSnippet(
    val code: String,
)

data class StylePropertiesSnapshot(
    val current: List<Pair<String, Any>>,
    val defaults: List<Pair<String, Any>>,
)

interface ChartCodeGenerator<TConfig> {
    fun generate(config: TConfig): GeneratedSnippet
}

fun deriveFunctionName(
    title: String,
    chartType: ChartType,
): String {
    val parts = "[A-Za-z0-9]+".toRegex().findAll(title).map { it.value }.toList()
    val base = parts.joinToString(separator = "") { part -> part.replaceFirstChar { it.uppercase() } }
    val fallback = "Sample${chartType.codegenSuffix}"
    val withSuffix = if (base.isBlank()) fallback else "${base}${chartType.codegenSuffix}"
    return if (withSuffix.firstOrNull()?.isDigit() == true) "Sample$withSuffix" else withSuffix
}
