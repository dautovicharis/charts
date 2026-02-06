package io.github.dautovicharis.charts.app.ui.composable

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.style.Style

typealias ColorNameResolver = (Color) -> String

data class StyleItems(
    val name: String,
    val items: List<StyleItem>
)

data class StyleItem(
    val name: String,
    val value: String,
    val color: Color? = null,
    val isChanged: Boolean
)

@Composable
fun ChartStyleItems(
    currentStyle: Style,
    defaultStyle: Style
): StyleItems {
    val resolver = materialColorNameResolver(MaterialTheme.colorScheme)
    return buildStyleItems(
        currentStyle = currentStyle,
        defaultStyle = defaultStyle,
        colorNameResolver = resolver
    )
}

fun buildStyleItems(
    currentStyle: Style,
    defaultStyle: Style,
    colorNameResolver: ColorNameResolver = { "Custom color" }
): StyleItems {

    val currentProperties = currentStyle.getProperties()
    val defaultProperties = defaultStyle.getProperties().toMap()

    val items = currentProperties.map { (name, currentValue) ->
        val defaultValue = defaultProperties[name]
        val isChanged = currentValue != defaultValue

        when (currentValue) {
            is List<*> -> {
                val value = if (isChanged) "Custom" else "Default"
                StyleItem(
                    name = name,
                    value = value,
                    isChanged = isChanged
                )
            }
            is Color -> {
                StyleItem(
                    name = name,
                    value = colorNameResolver(currentValue),
                    color = currentValue,
                    isChanged = isChanged
                )
            }
            else -> {
                StyleItem(
                    name = name,
                    value = currentValue.toString(),
                    isChanged = isChanged
                )
            }
        }
    }

    val hasCustomValues = items.any { it.isChanged }
    val baseName = currentStyle::class.simpleName ?: "Style"
    val styleName = if (hasCustomValues) "$baseName (Custom)" else "$baseName (Default)"
    return StyleItems(
        name = styleName,
        items = items
    )
}

private fun materialColorNameResolver(colorScheme: ColorScheme): ColorNameResolver {
    val colorNames = mapOf(
        colorScheme.primary to "MaterialTheme\n.colorScheme\n.primary",
        colorScheme.secondary to "MaterialTheme\n.colorScheme\n.secondary",
        colorScheme.tertiary to "MaterialTheme\n.colorScheme\n.tertiary",
        colorScheme.background to "MaterialTheme\n.colorScheme\n.background",
        colorScheme.surface to "MaterialTheme\n.colorScheme\n.surface",
        colorScheme.error to "MaterialTheme\n.colorScheme\n.error",
        colorScheme.onPrimary to "MaterialTheme\n.colorScheme\n.onPrimary",
        colorScheme.onSecondary to "MaterialTheme\n.colorScheme\n.onSecondary",
        colorScheme.onBackground to "MaterialTheme\n.colorScheme\n.onBackground",
        colorScheme.onSurface to "MaterialTheme\n.colorScheme\n.onSurface",
        colorScheme.onError to "MaterialTheme\n.colorScheme\n.onError"
    )

    return { color -> colorNames[color] ?: "Custom color" }
}
