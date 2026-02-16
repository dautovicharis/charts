package codegen.common

fun buildChartImports(
    baseImports: List<String>,
    styleImport: String? = null,
    styleArguments: List<RenderedStyleArgument> = emptyList(),
): List<String> {
    val imports = mutableListOf<String>()
    imports += baseImports
    styleImport?.let { imports += it }
    imports += styleArguments.flatMap { it.additionalImports }
    return imports.distinct().sorted()
}
