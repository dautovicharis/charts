package codegen.multiline

import androidx.compose.runtime.Composable
import io.github.dautovicharis.charts.style.LineChartDefaults
import model.MultiLineStyleState
import model.StylePropertiesSnapshot

@Composable
fun multiLineStylePropertiesSnapshot(
    styleState: MultiLineStyleState,
    seriesCount: Int,
): StylePropertiesSnapshot {
    val defaultStyle = LineChartDefaults.style()
    val normalizedLineColors =
        styleState.lineColors?.let { colors ->
            normalizeColorCount(colors = colors, targetCount = seriesCount)
        }
    val currentStyle =
        LineChartDefaults.style(
            lineColors = normalizedLineColors ?: defaultStyle.lineColors,
            lineAlpha = styleState.lineAlpha ?: defaultStyle.lineAlpha,
            bezier = styleState.bezier ?: defaultStyle.bezier,
            pointVisible = styleState.pointVisible ?: defaultStyle.pointVisible,
            dragPointVisible = styleState.dragPointVisible ?: defaultStyle.dragPointVisible,
            pointColor = styleState.pointColor ?: defaultStyle.pointColor,
            dragPointColor = styleState.dragPointColor ?: defaultStyle.dragPointColor,
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
