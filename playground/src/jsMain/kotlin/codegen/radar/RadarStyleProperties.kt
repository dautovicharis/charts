package codegen.radar

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.RadarChartDefaults
import model.RadarStyleState
import model.StylePropertiesSnapshot

@Composable
fun radarStylePropertiesSnapshot(
    styleState: RadarStyleState,
    seriesCount: Int,
): StylePropertiesSnapshot {
    val defaultStyle = RadarChartDefaults.style()
    val normalizedLineColors =
        styleState.lineColors?.let { colors ->
            normalizeColorCount(colors = colors, targetCount = seriesCount)
        }
    val currentStyle =
        RadarChartDefaults.style(
            lineColors = normalizedLineColors ?: defaultStyle.lineColors,
            lineWidth = styleState.lineWidth ?: defaultStyle.lineWidth,
            pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
            pointSize = styleState.pointSize ?: defaultStyle.pointSize,
            fillVisible = styleState.fillVisible ?: defaultStyle.fillVisible,
            fillAlpha = styleState.fillAlpha ?: defaultStyle.fillAlpha,
            gridVisible = styleState.gridVisible ?: defaultStyle.gridVisible,
            categoryLegendVisible = styleState.categoryLegendVisible ?: defaultStyle.categoryLegendVisible,
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
