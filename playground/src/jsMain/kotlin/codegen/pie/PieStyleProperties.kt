package codegen.pie

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.PieChartDefaults
import model.PieStyleState
import model.StylePropertiesSnapshot

@Composable
fun pieStylePropertiesSnapshot(
    styleState: PieStyleState,
    itemCount: Int,
): StylePropertiesSnapshot {
    val defaultStyle = PieChartDefaults.style()
    val normalizedPieColors =
        styleState.pieColors?.let { colors ->
            normalizeColorCount(
                colors = colors,
                targetCount = itemCount,
            )
        }
    val currentStyle =
        PieChartDefaults.style(
            donutPercentage = styleState.donutPercentage ?: defaultStyle.donutPercentage,
            borderWidth = styleState.borderWidth ?: defaultStyle.borderWidth,
            pieAlpha = styleState.pieAlpha ?: defaultStyle.pieAlpha,
            legendVisible = styleState.legendVisible ?: defaultStyle.legendVisible,
            pieColors = normalizedPieColors ?: defaultStyle.pieColors,
        )

    return StylePropertiesSnapshot(
        current = currentStyle.getProperties(),
        defaults = defaultStyle.getProperties(),
    )
}

private fun normalizeColorCount(
    colors: List<androidx.compose.ui.graphics.Color>,
    targetCount: Int,
): List<androidx.compose.ui.graphics.Color> {
    if (targetCount <= 0 || colors.isEmpty()) return emptyList()
    return List(targetCount) { index -> colors[index % colors.size] }
}
