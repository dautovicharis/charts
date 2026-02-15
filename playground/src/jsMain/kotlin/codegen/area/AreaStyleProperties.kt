package codegen.area

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.StackedAreaChartDefaults
import model.AreaStyleState
import model.StylePropertiesSnapshot

@Composable
fun areaStylePropertiesSnapshot(
    styleState: AreaStyleState,
    seriesCount: Int,
): StylePropertiesSnapshot {
    val defaultStyle = StackedAreaChartDefaults.style()
    val normalizedAreaColors =
        styleState.areaColors?.let { colors ->
            normalizeColorCount(colors = colors, targetCount = seriesCount)
        }
    val normalizedLineColors =
        styleState.lineColors?.let { colors ->
            normalizeColorCount(colors = colors, targetCount = seriesCount)
        }
    val currentStyle =
        StackedAreaChartDefaults.style(
            areaColors = normalizedAreaColors ?: defaultStyle.areaColors,
            lineColors = normalizedLineColors ?: defaultStyle.lineColors,
            fillAlpha = styleState.fillAlpha ?: defaultStyle.fillAlpha,
            lineVisible = styleState.lineVisible ?: defaultStyle.lineVisible,
            lineWidth = styleState.lineWidth ?: defaultStyle.lineWidth,
            bezier = styleState.bezier ?: defaultStyle.bezier,
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
