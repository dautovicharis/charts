package io.github.dautovicharis.charts.internal.barstackedchart

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun generateColorShades(baseColor: Color, numberOfShades: Int): ImmutableList<Color> {
    val step = 0.6f / (numberOfShades)

    return (0 until numberOfShades).map { i ->
        val luminance = step * i
        baseColor.copy(
            red = (baseColor.red * (1 - luminance) + luminance).coerceIn(0f, 1f),
            green = (baseColor.green * (1 - luminance) + luminance).coerceIn(0f, 1f),
            blue = (baseColor.blue * (1 - luminance) + luminance).coerceIn(0f, 1f)
        )
    }.toImmutableList()
}
