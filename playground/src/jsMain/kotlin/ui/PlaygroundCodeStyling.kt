package ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

private val KotlinKeywords =
    setOf(
        "fun",
        "val",
        "var",
        "import",
        "class",
        "object",
        "interface",
        "data",
        "when",
        "if",
        "else",
        "for",
        "while",
        "return",
        "null",
        "true",
        "false",
    )

fun buildStyledCode(
    code: String,
    colors: ColorScheme,
): AnnotatedString {
    val keywordStyle = SpanStyle(color = colors.primary)
    val stringStyle = SpanStyle(color = colors.tertiary)
    val numberStyle = SpanStyle(color = colors.secondary)
    val punctuationStyle = SpanStyle(color = colors.onSurface.copy(alpha = 0.86f))
    val defaultStyle = SpanStyle(color = colors.onSurface)

    return buildAnnotatedString {
        val lines = code.split('\n')
        lines.forEachIndexed { index, line ->
            appendStyledLine(
                line = line,
                defaultStyle = defaultStyle,
                keywordStyle = keywordStyle,
                stringStyle = stringStyle,
                numberStyle = numberStyle,
                punctuationStyle = punctuationStyle,
            )
            if (index < lines.lastIndex) {
                append("\n")
            }
        }
    }
}

private fun AnnotatedString.Builder.appendStyledLine(
    line: String,
    defaultStyle: SpanStyle,
    keywordStyle: SpanStyle,
    stringStyle: SpanStyle,
    numberStyle: SpanStyle,
    punctuationStyle: SpanStyle,
) {
    var index = 0
    while (index < line.length) {
        val char = line[index]
        when {
            char == '"' -> {
                val end = findStringLiteralEnd(line, index + 1)
                pushStyle(stringStyle)
                append(line.substring(index, end))
                pop()
                index = end
            }
            char.isDigit() -> {
                val end = findNumberEnd(line, index + 1)
                pushStyle(numberStyle)
                append(line.substring(index, end))
                pop()
                index = end
            }
            char.isLetter() || char == '_' -> {
                val end = findWordEnd(line, index + 1)
                val word = line.substring(index, end)
                val style = if (word in KotlinKeywords) keywordStyle else defaultStyle
                pushStyle(style)
                append(word)
                pop()
                index = end
            }
            isPunctuation(char) -> {
                pushStyle(punctuationStyle)
                append(char)
                pop()
                index += 1
            }
            else -> {
                pushStyle(defaultStyle)
                append(char)
                pop()
                index += 1
            }
        }
    }
}

private fun findStringLiteralEnd(
    line: String,
    start: Int,
): Int {
    var index = start
    var escaped = false
    while (index < line.length) {
        val char = line[index]
        if (char == '"' && !escaped) {
            return index + 1
        }
        escaped = char == '\\' && !escaped
        if (char != '\\') {
            escaped = false
        }
        index += 1
    }
    return line.length
}

private fun findNumberEnd(
    line: String,
    start: Int,
): Int {
    var index = start
    while (index < line.length) {
        val char = line[index]
        if (!(char.isDigit() || char == '.' || char == 'f' || char == 'F' || char == 'L')) {
            break
        }
        index += 1
    }
    return index
}

private fun findWordEnd(
    line: String,
    start: Int,
): Int {
    var index = start
    while (index < line.length && (line[index].isLetterOrDigit() || line[index] == '_')) {
        index += 1
    }
    return index
}

private fun isPunctuation(char: Char): Boolean =
    char in
        setOf(
            '(',
            ')',
            '{',
            '}',
            '[',
            ']',
            ',',
            '.',
            ':',
            '=',
        )
