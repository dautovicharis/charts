package codegen.stackedbar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.StackedBarChartDefaults
import model.StackedBarStyleState
import model.StylePropertiesSnapshot

@Composable
fun stackedBarStylePropertiesSnapshot(
    styleState: StackedBarStyleState,
    seriesCount: Int,
): StylePropertiesSnapshot {
    val defaultStyle = StackedBarChartDefaults.style()
    val normalizedBarColors =
        styleState.barColors?.let { colors ->
            normalizeColorCount(colors = colors, targetCount = seriesCount)
        }
    val currentStyle =
        StackedBarChartDefaults.style(
            barColors = normalizedBarColors ?: defaultStyle.barColors,
            barAlpha = styleState.barAlpha ?: defaultStyle.barAlpha,
            selectionLineVisible = styleState.selectionLineVisible ?: defaultStyle.selectionLineVisible,
            selectionLineWidth = styleState.selectionLineWidth ?: defaultStyle.selectionLineWidth,
            zoomControlsVisible = styleState.zoomControlsVisible ?: defaultStyle.zoomControlsVisible,
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
