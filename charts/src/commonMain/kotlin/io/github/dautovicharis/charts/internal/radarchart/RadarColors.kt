package io.github.dautovicharis.charts.internal.radarchart

import androidx.compose.ui.graphics.Color
import io.github.dautovicharis.charts.style.RadarChartStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private val DefaultCategoryColors = listOf(
    Color(0xFF4C78A8), // Blue
    Color(0xFFF58518), // Orange
    Color(0xFF54A24B), // Green
    Color(0xFFE45756), // Red
    Color(0xFFB279A2), // Purple
    Color(0xFF72B7B2), // Teal
    Color(0xFFFF9DA6), // Pink
    Color(0xFFEDC949)  // Yellow
)

internal fun categoryColors(
    style: RadarChartStyle,
    count: Int
): ImmutableList<Color> {
    if (count <= 0) return emptyList<Color>().toImmutableList()
    val baseColors = if (style.categoryColors.isNotEmpty()) {
        style.categoryColors
    } else {
        DefaultCategoryColors
    }
    val colors = mutableListOf<Color>()
    while (colors.size < count) {
        colors.addAll(baseColors)
    }
    return colors.take(count).toImmutableList()
}
