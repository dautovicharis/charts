package codegen.common

import kotlin.math.round

private const val INDENT = "    "
private const val FLOAT_ROUNDING_SCALE = 10_000f

fun kotlinLine(
    indentLevel: Int,
    content: String,
): String = "${INDENT.repeat(indentLevel)}$content"

fun formatKotlinFloatLiteral(value: Float): String {
    val rounded = round(value * FLOAT_ROUNDING_SCALE) / FLOAT_ROUNDING_SCALE
    val normalized = rounded.toString().removeSuffix(".0")
    return "${normalized}f"
}
