package codegen.common

import model.CodegenMode
import model.StylePropertiesSnapshot

data class RenderedStyleArgument(
    val code: String,
    val additionalImports: Set<String> = emptySet(),
)

fun resolveStyleArguments(
    styleProperties: StylePropertiesSnapshot?,
    codegenMode: CodegenMode,
): List<RenderedStyleArgument> {
    if (styleProperties == null) {
        return emptyList()
    }

    val defaultsByName = styleProperties.defaults.toMap()
    return styleProperties.current.mapNotNull { (name, currentValue) ->
        val defaultValue = defaultsByName[name]
        val shouldRender =
            when (codegenMode) {
                CodegenMode.MINIMAL -> currentValue != defaultValue
                CodegenMode.FULL -> true
            }
        if (!shouldRender) {
            null
        } else {
            val literal = toKotlinLiteral(propertyName = name, value = currentValue)
            RenderedStyleArgument(
                code = "$name = ${literal.code},",
                additionalImports = literal.additionalImports,
            )
        }
    }
}
