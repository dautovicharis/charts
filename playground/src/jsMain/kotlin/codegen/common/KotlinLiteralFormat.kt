package codegen.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kotlin.math.roundToInt

private const val COLOR_IMPORT = "import androidx.compose.ui.graphics.Color"
private const val DP_IMPORT = "import androidx.compose.ui.unit.dp"
private const val SP_IMPORT = "import androidx.compose.ui.unit.sp"

data class KotlinLiteral(
    val code: String,
    val additionalImports: Set<String> = emptySet(),
)

fun toKotlinLiteral(
    propertyName: String,
    value: Any,
): KotlinLiteral =
    when (value) {
        is Float -> KotlinLiteral(code = formatKotlinFloatLiteral(value))
        is Double -> KotlinLiteral(code = formatKotlinDoubleLiteral(value))
        is Int -> KotlinLiteral(code = value.toString())
        is Long -> KotlinLiteral(code = "${value}L")
        is Boolean -> KotlinLiteral(code = value.toString())
        is String -> KotlinLiteral(code = "\"${escapeKotlinString(value)}\"")
        is Color ->
            KotlinLiteral(
                code = colorLiteral(value),
                additionalImports = setOf(COLOR_IMPORT),
            )
        is Dp ->
            KotlinLiteral(
                code = "${formatScalar(value.value)}.dp",
                additionalImports = setOf(DP_IMPORT),
            )
        is TextUnit -> textUnitLiteral(propertyName, value)
        is List<*> -> listLiteral(propertyName = propertyName, values = value)
        else ->
            throw IllegalArgumentException(
                "Unsupported style property type for '$propertyName': ${value::class.simpleName}",
            )
    }

private fun listLiteral(
    propertyName: String,
    values: List<*>,
): KotlinLiteral {
    val literals =
        values.mapIndexed { index, value ->
            val nonNullValue =
                value
                    ?: throw IllegalArgumentException(
                        "Unsupported null list item for '$propertyName' at index $index",
                    )
            toKotlinLiteral(
                propertyName = "$propertyName[$index]",
                value = nonNullValue,
            )
        }

    return KotlinLiteral(
        code =
            if (literals.isEmpty()) {
                "listOf()"
            } else {
                "listOf(${literals.joinToString(", ") { literal -> literal.code }})"
            },
        additionalImports =
            literals
                .flatMap { it.additionalImports }
                .toSet(),
    )
}

private fun textUnitLiteral(
    propertyName: String,
    value: TextUnit,
): KotlinLiteral =
    when (value.type) {
        TextUnitType.Sp ->
            KotlinLiteral(
                code = "${formatScalar(value.value)}.sp",
                additionalImports = setOf(SP_IMPORT),
            )
        else ->
            throw IllegalArgumentException(
                "Unsupported TextUnit type for '$propertyName': ${value.type}",
            )
    }

private fun colorLiteral(color: Color): String {
    val alphaHex = channelToHex(color.alpha)
    val redHex = channelToHex(color.red)
    val greenHex = channelToHex(color.green)
    val blueHex = channelToHex(color.blue)
    return "Color(0x${alphaHex}${redHex}${greenHex}${blueHex})"
}

private fun channelToHex(value: Float): String =
    (value.coerceIn(0f, 1f) * 255f)
        .roundToInt()
        .toString(16)
        .uppercase()
        .padStart(length = 2, padChar = '0')

private fun formatKotlinDoubleLiteral(value: Double): String {
    if (value.isNaN()) return "Double.NaN"
    if (value == Double.POSITIVE_INFINITY) return "Double.POSITIVE_INFINITY"
    if (value == Double.NEGATIVE_INFINITY) return "Double.NEGATIVE_INFINITY"
    return value.toString()
}

private fun formatScalar(value: Float): String =
    value
        .toString()
        .removeSuffix(".0")
